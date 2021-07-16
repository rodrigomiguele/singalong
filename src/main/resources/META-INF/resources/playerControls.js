class PlayerControls{
    constructor(karaoke, showControls = false){
        this.karaoke = karaoke;
        this.showControls = showControls;
        this.initControls();
    }

    initControls(){
        if(this.showControls){
            var originalStartCallback = this.karaoke.videoStartCallback;
            var originalEndCallback = this.karaoke.videoEndCallback;
            var reference = this;
            this.karaoke.videoStartCallback = (event)=>{
                reference.onPlayerStarted(event);
                if(originalStartCallback){
                    originalStartCallback(event);
                }
            }

            this.karaoke.videoEndCallback = (event)=>{
                reference.onVideoEnd(event);
                if(originalEndCallback) {
                    originalEndCallback(event);
                }
            }
        } else {
            document.querySelector("#player_controls").setAttribute("class", "player_blocked");
            document.querySelectorAll("#player_controls div").forEach((e)=>{
                e.setAttribute("class", "display_none");
            });
        }
    }

    onPlayerStarted(event){
        this.player = document.querySelector("#player");
        this.playerControls = document.querySelector("#player_controls");
        var reference = this;
        this.player.onmouseenter = ()=>{
            reference.turnOnControls();
        }
        this.playerControls.onmouseleave = ()=>{
            reference.turnOffControls();
        }
        this.setupButtons();
    }

    turnOnControls(){
        this.playerControls.setAttribute("class", "player_visible");
        if(this.karaoke.player.getPlayerState() == 1){
            play.setAttribute("class", "display_none");
            pause.toggleAttribute("class");
        } else {
            pause.setAttribute("class", "display_none");
            play.toggleAttribute("class");
        }
    }

    turnOffControls(){
        this.playerControls.toggleAttribute("class");
        if(this.karaoke.player.getPlayerState() == 1){
            play.setAttribute("class", "display_none");
            pause.toggleAttribute("class");
        } else {
            pause.setAttribute("class", "display_none");
            play.toggleAttribute("class");
        }
    }

    setupButtons(){
        var play = this.playerControls.querySelector("#play");
        var pause = this.playerControls.querySelector("#pause");
        var skip = this.playerControls.querySelector("#skip");
        var reference = this;

        play.onclick = ()=>{
            karaoke.player.playVideo();
            reference.turnOffControls();
            play.setAttribute("class", "display_none");
            pause.toggleAttribute("class");
            reference.turnOffControls();
        }

        pause.onclick = ()=>{
            karaoke.player.pauseVideo();
            reference.turnOffControls();
            pause.setAttribute("class", "display_none");
            play.toggleAttribute("class");
            reference.turnOffControls();
        }

        skip.onclick = ()=>{
            karaoke.player.seekTo(Number.MAX_VALUE);
            reference.turnOffControls();
        }
    }

    onVideoEnd(event){
        document.querySelector("#player_controls").toggleAttribute("class");
    }

}