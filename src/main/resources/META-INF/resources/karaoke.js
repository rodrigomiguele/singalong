class Karaoke {
    constructor(YT, videoStartCallback = null, videoEndCallback = null, videoPausedCallback = null, videoPlayCallback = null){
        this.YT = YT;
        this.videoStartCallback = videoStartCallback;
        this.videoEndCallback = videoEndCallback;
        this.videoPausedCallback = videoPausedCallback;
        this.videoPlayCallback = videoPlayCallback;
    }

    clearPlayer(){
        var playerElement = document.querySelector('#player');
        var div = document.createElement('div');
        var message = document.createElement('span');
        message.setAttribute('id', 'message');
        var video = document.createElement('video');
        video.setAttribute('autoplay', '');
        video.setAttribute('muted', '');
        video.setAttribute('loop', '');
        var source = document.createElement('source');
        source.setAttribute('src', 'background.mp4');
        source.setAttribute('type', 'video/mp4');
        video.appendChild(source);
        div.appendChild(message);
        div.appendChild(video);
        div.setAttribute("id", "player");

        if(playerElement.nodeName === "IFRAME"){
            var parent = playerElement.parentElement;
            parent.removeChild(playerElement);
            parent.appendChild(div);
        }
        if(playerElement.nodeName === "DIV" && playerElement.innerText === ""){
            playerElement.innerHTML = div.innerHTML;
        }

        this.player = null;
        this.count = null;
    }

    playMusic(video, startAt = null){
        this.clearPlayer();
        this.player = new this.YT.Player('player', {
            height: window.innerHeight,
            width: window.innerWidth,
            videoId: video,
            playerVars: { 'controls': 0 },
            events: {
                'onReady': this.onPlayerReady,
                'onStateChange': this.onPlayerStateChange,
                'onError': this.onPlayerError
            }
        });
        this.player.reference = this;
        this.startAt = startAt;
        this.serverTime = new Date().getTime();
    }

    countDownToNextMusic(duration, nextMusic){
        this.clearPlayer();
        this.count = duration;
        this.nextMusic = nextMusic;
        var reference = this;
        var countdown = setInterval(()=>{
            if(this.count == -1){
                clearInterval(countdown);
                reference.playMusic(nextMusic.video);
            } else {
                message.innerHTML = "A próxima música é <b>" + nextMusic.title + "</b> cantada por <b>" + nextMusic.name + "</b> e vai começar em " + this.count + " segundo(s)." ;
                this.count--;
            }
        }, 1000);
    }

    noMusicsAhead(){
        this.clearPlayer();
        var message = document.querySelector("#message");
        message.innerText = 'Nenhum vídeo na fila no momento';
    }

    onPlayerReady(event) {
        event.target.playVideo();
        if(event.target.reference.startAt){
            event.target.seekTo(event.target.reference.startAt + (new Date().getTime() - event.target.reference.serverTime)/1000);
        }
        if(event.target.reference.videoStartCallback) {
            event.target.reference.videoStartCallback(event);
        }
    }

    onPlayerStateChange(event) {
        if (event.data == YT.PlayerState.ENDED) {
            event.target.reference.clearPlayer();
            if(event.target.reference.videoEndCallback){
                event.target.reference.videoEndCallback(event);
            }
        } else if (event.data == YT.PlayerState.PAUSED) {
            event.target.reference.onPauseVideo(event);
        } else if (event.data == YT.PlayerState.PLAYING) {
            event.target.reference.onPlayVideo(event);
        }
    }

    onPauseVideo(event){
        if(this.videoPausedCallback){
            this.videoPausedCallback(event);
        }
    }

    onPlayVideo(event){
        if(this.videoPlayCallback){
            this.videoPlayCallback(event);
        }
    }

    onPlayerError(event){
        console.log(event);
    }

    mute(){
        if(this.player){
            this.player.mute();
        }
    }

    getState(){
        if(this.player){
            return {type: "PLAYING_MUSIC", music: this.nextMusic, seconds: this.player.getCurrentTime()};
        }
        return {type: "COUNTDOWN_TO_NEXT_MUSIC", music: this.nextMusic, seconds: this.count};
    }

    stopVideo() {
        this.player.stopVideo();
    }

}