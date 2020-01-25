var allCurrentModels = [];

var World = {

    platformAssisstedTrackingSupported: false,
    createOverlaysCalled: false,
    canStartTrackingIntervalHandle: null,
    angleModel: 0.0,

    init: function initFn() {
        /*
            When you'd like to make use of the SMART feature, make sure to call this function and await the result
            in the AR.hardware.smart.onPlatformAssistedTrackingAvailabilityChanged callback.
         */
        AR.hardware.smart.isPlatformAssistedTrackingSupported();
    },

    createARModel: function createARModelFn(xpos, ypos){
        this.arModel = new AR.Model("assets/models/carro_robo.wt3", {
            scale: {
               x: 0.01,
               y: 0.01,
               z: 0.01
            },
            translate: {
               x: xpos,
               y: ypos
            },
            rotate: {
               z: World.angleModel
            },
            onDragChanged: function(relativeX, relativeY, intersectionX, intersectionY) {
                this.translate = {x:intersectionX, y:intersectionY};
            },
            onError: World.onError
        });

        this.animation = new AR.ModelAnimation(this.arModel, "Base_carro|Carro Andando_Base_carro_animation");

        this.resetAnimation = new AR.ModelAnimation(this.arModel, "Base_carro|Carro Parado_Base_carro_animation");

        this.modelSound = new AR.Sound("assets/sounds/rosas.wav", {
            onError : function(){
                alert(errorMessage);
            }
        });
        this.modelSound.load();

        this.animationStarted = false;
    },

    createOverlays: function createOverlaysFn() {
        if (World.createOverlaysCalled) {
            return;
        }

        World.createOverlaysCalled = true;

        var crossHairsRedImage = new AR.ImageResource("assets/crosshairs_red.png", {
            onError: World.onError
        });
        this.crossHairsRedDrawable = new AR.ImageDrawable(crossHairsRedImage, 1.0);

        var crossHairsBlueImage = new AR.ImageResource("assets/crosshairs_blue.png", {
            onError: World.onError
        });
        this.crossHairsBlueDrawable = new AR.ImageDrawable(crossHairsBlueImage, 1.0);

        var crossHairsGreenImage = new AR.ImageResource("assets/crosshairs_green.png", {
            onError: World.onError
        });
        this.crossHairsGreenDrawable = new AR.ImageDrawable(crossHairsGreenImage, 1.0);

        this.tracker = new AR.InstantTracker({
            smartEnabled: false,
            onChangedState: function onChangedStateFn(state) {
                if (state === AR.InstantTrackerState.INITIALIZING) {
                    document.getElementById("tracking-start-stop-button").src = "assets/buttons/start.png";
                    document.getElementById("tracking-height-slider-container").style.visibility = "visible";
                    document.getElementById("change-direction-slider-container").style.visibility = "hidden";
                    document.getElementById("animation-pause-resume-button").style.visibility = "hidden";
                } else{
                    document.getElementById("tracking-start-stop-button").src = "assets/buttons/stop.png";
                    document.getElementById("tracking-height-slider-container").style.visibility = "hidden";
                    document.getElementById("change-direction-slider-container").style.visibility = "visible";
                    document.getElementById("animation-pause-resume-button").style.visibility = "visible";
                }
            },
            /*
                Device height needs to be as accurate as possible to have an accurate scale returned by the Wikitude
                SDK.
             */
            deviceHeight: 2.5,
            onError: World.onError,
            onChangeStateError: World.onError
        });

        this.instantTrackable = new AR.InstantTrackable(this.tracker, {
            drawables: {
                cam: World.crossHairsBlueDrawable,
                initialization: World.crossHairsRedDrawable
            },
            onTrackingStarted: function onTrackingStartedFn() {
                /* Do something when tracking is started (recognized). */
                World.createARModel(0.0, 0.0);
                World.addModel();

                document.getElementById("animation-pause-resume-button").src = "assets/buttons/start.png";
            },
            onTrackingStopped: function onTrackingStoppedFn() {
                /* Do something when tracking is stopped (lost). */
                World.tracker.state = AR.InstantTrackerState.INITIALIZING;
                World.resetModels();
            },

            onError: World.onError
        });

        World.canStartTrackingIntervalHandle = setInterval(
            function() {
                if (World.tracker.canStartTracking) {
                    World.instantTrackable.drawables.initialization = [World.crossHairsGreenDrawable];

                } else {
                    World.instantTrackable.drawables.initialization = [World.crossHairsRedDrawable];
                }
            },
            1000
        );
    },

    changeTrackerState: function changeTrackerStateFn() {
        if (this.tracker.state === AR.InstantTrackerState.INITIALIZING) {
            this.tracker.state = AR.InstantTrackerState.TRACKING;

        } else {
            this.tracker.state = AR.InstantTrackerState.INITIALIZING;
            World.resetModels();
        }
    },

    changeTrackingHeight: function changeTrackingHeightFn(height) {
        World.tracker.deviceHeight = parseFloat(height);
    },

    changeObjectAngle: function changeObjectAngleFn(angle) {
        World.angleModel = parseFloat(angle);
        World.arModel.rotate.z = World.angleModel;
    },

    addModel: function addModelFn() {
        if (this.tracker.state === AR.InstantTrackerState.TRACKING) {
            allCurrentModels.push(World.arModel);
            this.instantTrackable.drawables.addCamDrawable(World.arModel);
        }
    },

    pauseOrResume: function pauseOrResumeFn(){
        if (World.animation.isRunning()){
            World.animation.pause();
            World.modelSound.pause();
            document.getElementById("animation-pause-resume-button").src = "assets/buttons/start.png";
        }
        else{
            if (!World.animationStarted){
                World.resetAnimation.start();
                World.resetAnimation.destroy();
                World.animation.start(-1);
                World.animationStarted = true;
                World.modelSound.play();
            }else{
                World.resetAnimation.start();
                World.resetAnimation.destroy();
                World.animation.resume();
                World.modelSound.resume();
            }
            document.getElementById("animation-pause-resume-button").src = "assets/buttons/pause.png";
        }
    },

    anima: function animaFn(){

    },

    resetModels: function resetModelsFn() {
        World.modelSound.stop();
        World.animation.pause();
        World.animation.destroy();
        World.arModel.destroy();
        this.instantTrackable.drawables.removeCamDrawable(allCurrentModels);
        allCurrentModels = [];
    },

    onError: function onErrorFn(error) {
        alert(error);

        /* if license check failed, stop repeatedly calling `canStartTracking` */
        if (error.id === 1001 && error.domain === "InstantTracking") {
            clearInterval(World.canStartTrackingIntervalHandle);
        }
    },

    showUserInstructions: function showUserInstructionsFn(message) {
        document.getElementById('loadingMessage').innerHTML = message;
    }
};

AR.hardware.smart.onPlatformAssistedTrackingAvailabilityChanged = function(availability) {
    switch (availability) {
        case AR.hardware.smart.SmartAvailability.INDETERMINATE_QUERY_FAILED:
            /* Query failed for some reason; try again or accept the fact. */
            World.showUserInstructions("Não foi possivel determinar se a assistência de plataforma é suportada.<br>" +
                "Rodando sem assistência de plataforma (ARCore).");
            World.createOverlays();
            break;
        case AR.hardware.smart.SmartAvailability.CHECKING_QUERY_ONGOING:
            /* Query currently ongoing; be patient and do nothing or inform the user about the ongoing process. */
            break;
        case AR.hardware.smart.SmartAvailability.UNSUPPORTED:
            /* Not supported, create the scene now without platform assisted tracking enabled. */
            World.showUserInstructions("Rodando sem assistência de plataforma (ARCore).");
            World.createOverlays();
            break;
        case AR.hardware.smart.SmartAvailability.SUPPORTED_UPDATE_REQUIRED:
        case AR.hardware.smart.SmartAvailability.SUPPORTED:
            /*
                Supported, create the scene now with platform assisted tracking enabled SUPPORTED_UPDATE_REQUIRED
                may be followed by SUPPORTED, make sure not to create the scene twice (see check in createOverlays).
             */
            World.platformAssisstedTrackingSupported = false;
            World.showUserInstructions("Rodando com assistência de plataforma (ARCore). <br> ");
            World.createOverlays();
            break;
    }
};

World.init();