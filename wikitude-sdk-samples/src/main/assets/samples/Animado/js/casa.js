var allCurrentModels = [];

var World = {

    platformAssisstedTrackingSupported: false,
    createOverlaysCalled: false,
    canStartTrackingIntervalHandle: null,
    angleModel: 0.0,
    animation: null,
    resetAnimation: null,
    modelSound: null,
    animationStarted: false,
    arModel: null,
    selectedCar: null,

    init: function initFn() {
        AR.hardware.smart.isPlatformAssistedTrackingSupported();
    },

    createARModel: function createARModelFn(xpos, ypos, name, animationName, resetAnimationName){
        World.arModel = new AR.Model(name, {
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

        World.animation = new AR.ModelAnimation(this.arModel, animationName);

        World.resetAnimation = new AR.ModelAnimation(this.arModel, resetAnimationName);

        World.modelSound = new AR.Sound("assets/sounds/rosas.mp3", {
            onError : function(){
                alert(errorMessage);
            }
        });
        World.modelSound.load();

        World.animationStarted = false;

        World.resetAnimation.start();
        World.resetAnimation.stop();
        World.resetAnimation.destroy();
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
                    document.getElementById("tracking-start-stop-button").style.visibility = "hidden";
                    document.getElementById("tracking-height-slider-container").style.visibility = "visible";
                    document.getElementById("distanceMessage").style.visibility = "visible";
                    document.getElementById("abre-alas-button").style.visibility = "visible";
                    document.getElementById("fecha-alas-button").style.visibility = "visible";
                    document.getElementById("change-direction-slider-container").style.visibility = "hidden";
                    document.getElementById("angleMessage").style.visibility = "hidden";
                    document.getElementById("animation-pause-resume-button").style.visibility = "hidden";
                } else{
                    document.getElementById("tracking-start-stop-button").style.visibility = "visible";
                    document.getElementById("abre-alas-button").style.visibility = "hidden";
                    document.getElementById("fecha-alas-button").style.visibility = "hidden";
                    document.getElementById("tracking-height-slider-container").style.visibility = "hidden";
                    document.getElementById("distanceMessage").style.visibility = "hidden";
                    document.getElementById("change-direction-slider-container").style.visibility = "visible";
                    document.getElementById("angleMessage").style.visibility = "visible";
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
                if (World.selectedCar == "abre_alas"){
                    World.createARModel(0.0, 0.0, "assets/models/abre_alas.wt3",
                    "Plataforma01|Carro Andando_Plataforma01_animation",
                    "Plataforma01|Carro Parado_Plataforma01_animation");
                }else{
                    World.createARModel(0.0, 0.0, "assets/models/fecha_alas.wt3",
                    "Base_carro|Carro Andando_Base_carro_animation",
                    "Base_carro|Carro Parado_Base_carro_animation");
                }
                World.addModel();
                document.getElementById("animation-pause-resume-button").src = "assets/buttons/start.png";
                World.showUserInstructions("Pressionando o play, o carro começará a andar! Você pode o acompanhar, movendo suavemente o seu dispositivo!");
            },
            onTrackingStopped: function onTrackingStoppedFn() {
                /* Do something when tracking is stopped (lost). */
                World.tracker.state = AR.InstantTrackerState.INITIALIZING;
                World.resetModels();
                World.showUserInstructions("Você pode reiniciar a experiência a qualquer momento!");
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

    changeTrackerState: function changeTrackerStateFn(id) {
        if (id == "abre-alas-button"){
            World.selectedCar = "abre_alas";
        }else{
            World.selectedCar = "fecha_alas";
        }

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
                World.animation.start(-1);
                World.animationStarted = true;
                World.modelSound.play();
            }else{
                World.animation.resume();
                World.modelSound.resume();
            }
            document.getElementById("animation-pause-resume-button").src = "assets/buttons/pause.png";
        }
    },

    resetModels: function resetModelsFn() {
        World.modelSound.stop();
        World.animation.stop();
        World.animation.destroy();
        this.instantTrackable.drawables.removeCamDrawable(allCurrentModels);
        allCurrentModels = [];
    },

    onError: function onErrorFn(error) {
        alert(error);
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
            World.createOverlays();
            break;
        case AR.hardware.smart.SmartAvailability.CHECKING_QUERY_ONGOING:
            /* Query currently ongoing; be patient and do nothing or inform the user about the ongoing process. */
            break;
        case AR.hardware.smart.SmartAvailability.UNSUPPORTED:
            /* Not supported, create the scene now without platform assisted tracking enabled. */
            World.createOverlays();
            break;
        case AR.hardware.smart.SmartAvailability.SUPPORTED_UPDATE_REQUIRED:
        case AR.hardware.smart.SmartAvailability.SUPPORTED:
            /*
                Supported, create the scene now with platform assisted tracking enabled SUPPORTED_UPDATE_REQUIRED
                may be followed by SUPPORTED, make sure not to create the scene twice (see check in createOverlays).
             */
            World.platformAssisstedTrackingSupported = false;
            World.createOverlays();
            break;
    }
};

World.init();