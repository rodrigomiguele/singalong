package org.rmiguele.singalong.domain;

public class ServerMessage {

    private MessageType type;

    private QueuedMusic music;

    private StateType stateType;

    private Double seconds;

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public QueuedMusic getMusic() {
        return music;
    }

    public void setMusic(QueuedMusic music) {
        this.music = music;
    }

    public StateType getStateType() {
        return stateType;
    }

    public void setStateType(StateType stateType) {
        this.stateType = stateType;
    }

    public Double getSeconds() {
        return seconds;
    }

    public void setSeconds(Double seconds) {
        this.seconds = seconds;
    }

    public static ServerMessage ofType(MessageType messageType) {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setType(messageType);
        return serverMessage;
    }

    public ServerMessage withSeconds(Double seconds) {
        this.seconds = seconds;
        return this;
    }

    public ServerMessage withStateType(StateType stateType) {
        this.stateType = stateType;
        return this;
    }

    public ServerMessage withMusic(QueuedMusic music) {
        this.music = music;
        return this;
    }

    public enum MessageType {
        PLAY_MUSIC, INFORM_NEW_LEADER, INFORM_NEW_FOLLOWER, SYNC_WITH_LEADER, REQUEST_STATE_TO_LEADER, MUSIC_END_ON_LEADER, LEADER_PAUSED_MUSIC;
    }

}
