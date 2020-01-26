var World = {
    latitude: 0.0,
    longitude: 0.0,
    altitude: 0.0,
    used: false,
    animation: null,
    modelSound: null,

    init: function initFn() {
        AR.context.onLocationChanged = World.locationChanged;
        World.worldLoaded();
    },

    locationChanged: function locationChangedFn(lat, lon, alt, acc) {
        /*
            The custom function World.onLocationChanged checks with the flag World.initiallyLoadedData if the
            function was already called. With the first call of World.onLocationChanged an object that contains geo
            information will be created which will be later used to create a marker using the
            World.loadPoisFromJsonData function.
        */
        if (!World.used){
            World.latitude = lat;
            World.longitude = lon;
            World.altitude = alt;
        }
    },

    createModelAtLocation: function createModelAtLocationFn() {
        /*
            First a location where the model should be displayed will be defined. This location will be relativ to
            the user.
        */
        var userLocation = new AR.GeoLocation(World.latitude, World.longitude, World.altitude);
        var modelLocation = new AR.RelativeLocation(userLocation, 2, 0, -2);
        World.used = true;

        /* Next the model object is loaded. */
        var model = new AR.Model("assets/models/carro_robo.wt3", {
            onLoaded: this.worldLoaded,
            onError: World.onError,
            scale: {
                x: 0.01,
                y: 0.01,
                z: 0.01
            }
        });

        World.animation = new AR.ModelAnimation(model, "Base_carro|Carro Andando_Base_carro_animation");

        World.modelSound = new AR.Sound("assets/sounds/rosas.wav", {
            onError : function(){
                alert(errorMessage);
            }
        });
        World.modelSound.load();

        var animationStarted = false;

        var indicatorImage = new AR.ImageResource("assets/indi.png", {
            onError: World.onError
        });

        var indicatorDrawable = new AR.ImageDrawable(indicatorImage, 0.1, {
            verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP
        });

        /* Putting it all together the location and 3D model is added to an AR.GeoObject. */
        this.geoObject = new AR.GeoObject(modelLocation, {
            drawables: {
                cam: [model],
                indicator: [indicatorDrawable]
            }
        });

        geoObject.onDragChanged: function(xNormalized, yNormalized) {
                                    this.translate = {x:intersectionX, y:intersectionY}}
    },

    onError: function onErrorFn(error) {
        alert(error);
    },

    worldLoaded: function worldLoadedFn() {
        document.getElementById("loadingMessage").style.display = "none";
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
};

World.init();