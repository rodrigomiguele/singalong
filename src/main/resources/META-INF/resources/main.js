var karaoke;
var playerControls;
var user;
var isLeader = false;
var state;

function initKaraoke(){
    if(YT){
        karaoke = new Karaoke(YT, null, onMusicEnd, onMusicPaused, onMusicPlayed);
        karaoke.noMusicsAhead();
        openConnection();
    } else {
        setTimeout(initKaraoke(), 300);
    }
}

function uuidv4() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

function openConnection(){
    if (!user){
        user = uuidv4();
    }
    queueSocket = new WebSocket(window.location.origin.replace("http", "ws") + "/karaoke/" + user);
    queueSocket.onmessage = function (event) {
        var message = JSON.parse(event.data);
        switch(message.type) {
            case "PLAY_MUSIC":
                onPlayMusic(message);
                break;
            case "INFORM_NEW_LEADER":
                onInformNewLeader(message);
                break;
            case "INFORM_NEW_FOLLOWER":
                onInformNewFollower(message);
                break;
            case "SYNC_WITH_LEADER":
                onSyncWithLeader(message);
                break;
            case "REQUEST_STATE_TO_LEADER":
                onRequestStateToLeader(message);
                break;
            case "MUSIC_END_ON_LEADER":
                onMusicEndOnLeader(message);
                break;
            case "LEADER_PAUSED_MUSIC":
                onLeaderPausedMusic(message);
                break;
            default:
                console.log("Mensagem não reconhecida. Ignorando.");
        }
    };
}

function onPlayMusic(message){
    karaoke.countDownToNextMusic(config["COUNT_TO_NEXT_MUSIC"], message.music);
}

function onInformNewLeader(message){
    isLeader = true;
    playerControls = new PlayerControls(karaoke, isLeader);
}

function onInformNewFollower(message){
    isLeader = false;
    playerControls = new PlayerControls(karaoke, isLeader);
    var time = new Date().getTime();
    sendMessage({type: "SYNC_WITH_LEADER", time: time});
}

function onSyncWithLeader(message){
    if(!isLeader){
        var start = new Date().getTime();
        switch(message.stateType){
            case "COUNTDOWN_TO_NEXT_MUSIC":
                karaoke.countDownToNextMusic(message.seconds + ((new Date().getTime() - start)/1000), message.music);
                break;
            case "PLAYING_MUSIC":
                karaoke.playMusic(message.music.video, message.seconds  + ((new Date().getTime() - start)/1000));
                break;
            default:
                console.log("Estado não reconhecido. Reconectando.");
                window.location = window.location;
        }
    }
}

function onMusicEndOnLeader(message){
    if(!isLeader) {
        var time = new Date().getTime();
        sendMessage({type: "SYNC_WITH_LEADER", time: time});
    }
}

function onRequestStateToLeader(message){
    if(isLeader){
        var time = new Date().getTime();
        var state = karaoke.getState();
        sendMessage({type:"LEADER_INFORMING_STATE", stateType: state.type, seconds: state.seconds, music: state.music, time: time});
    }
}

function onLeaderPausedMusic(message){
    karaoke.stopVideo();
}

function sendMessage(message){
    queueSocket.send(JSON.stringify(message));
}

function onMusicPaused(){
    if(isLeader){
        var time = new Date().getTime();
        sendMessage({type: "LEADER_PAUSED_MUSIC", time: time});
    }
}

function onMusicPlayed(){
    if(isLeader){
        var time = new Date().getTime();
        var state = karaoke.getState();
        sendMessage({type:"LEADER_PLAYED_MUSIC", stateType: state.type, seconds: state.seconds, music: state.music, time: time});
    }
}

function onMusicEnd(){
    if(isLeader){
        var time = new Date().getTime();
        sendMessage({type: "MUSIC_END_ON_LEADER", time: time});
    }
}

initKaraoke();