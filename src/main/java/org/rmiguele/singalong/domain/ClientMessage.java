package org.rmiguele.singalong.domain;

public class ClientMessage {

    private MessageType type;

    private QueuedMusic music;

    private Double seconds;

    private Long time;

    private StateType stateType;

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

    public Double getSeconds() {
        return seconds;
    }

    public void setSeconds(Double seconds) {
        this.seconds = seconds;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public StateType getStateType() {
        return stateType;
    }

    public void setStateType(StateType stateType) {
        this.stateType = stateType;
    }

    public enum MessageType {
        MUSIC_END_ON_LEADER, SYNC_WITH_LEADER, LEADER_INFORMING_STATE, LEADER_PAUSED_MUSIC, LEADER_PLAYED_MUSIC;
    }

}
