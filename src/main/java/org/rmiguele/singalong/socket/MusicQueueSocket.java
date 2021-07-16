package org.rmiguele.singalong.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.rmiguele.singalong.domain.ClientMessage;
import org.rmiguele.singalong.domain.ServerMessage;
import org.rmiguele.singalong.service.MusicQueue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toSet;
import static org.rmiguele.singalong.domain.ServerMessage.MessageType.INFORM_NEW_FOLLOWER;
import static org.rmiguele.singalong.domain.ServerMessage.MessageType.INFORM_NEW_LEADER;
import static org.rmiguele.singalong.domain.ServerMessage.MessageType.LEADER_PAUSED_MUSIC;
import static org.rmiguele.singalong.domain.ServerMessage.MessageType.MUSIC_END_ON_LEADER;
import static org.rmiguele.singalong.domain.ServerMessage.MessageType.PLAY_MUSIC;
import static org.rmiguele.singalong.domain.ServerMessage.MessageType.REQUEST_STATE_TO_LEADER;
import static org.rmiguele.singalong.domain.ServerMessage.MessageType.SYNC_WITH_LEADER;
import static org.rmiguele.singalong.domain.ServerMessage.ofType;

@ServerEndpoint("/karaoke/{hash}")
@ApplicationScoped
public class MusicQueueSocket {

    MusicQueue musicQueue;

    Map<String, Session> sessions = new ConcurrentHashMap<>();

    Queue<String> clients = new ConcurrentLinkedQueue<>();

    Queue<String> clientsRequestsToSync = new ConcurrentLinkedQueue<>();

    ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    public MusicQueueSocket(MusicQueue musicQueue) {
        this.musicQueue = musicQueue;
        this.musicQueue.registerConsumer(queuedMusic -> sessions.keySet().forEach(this::playNextMusic));
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("hash") String username) {
        sessions.put(username, session);
        clients.add(username);
        onPeerConnected(username);
    }

    @OnClose
    public void onClose(Session session, @PathParam("hash") String username) {
        sessions.remove(username);
        clients.remove(username);
        if (!clients.isEmpty()) {
            this.sendMessage(getLeader(), ofType(INFORM_NEW_LEADER));
        }
    }

    @OnError
    public void onError(Session session, @PathParam("hash") String username, Throwable throwable) {
        throwable.printStackTrace();
        sessions.remove(username);
        clients.remove(username);
        if (!clients.isEmpty()) {
            this.sendMessage(getLeader(), ofType(INFORM_NEW_LEADER));
        }
    }

    @OnMessage
    public void onMessage(String message, @PathParam("hash") String username) throws JsonProcessingException {
        ClientMessage clientMessage = objectMapper.readValue(message, ClientMessage.class);
        switch (clientMessage.getType()) {
            case MUSIC_END_ON_LEADER:
                onMusicEndOnLeader(username);
                break;
            case SYNC_WITH_LEADER:
                onSyncWithLeader(username);
                break;
            case LEADER_INFORMING_STATE:
                onLeaderInformingState(username, clientMessage);
                break;
            case LEADER_PAUSED_MUSIC:
                onLeaderPausedMusic(username);
                break;
            case LEADER_PLAYED_MUSIC:
                onLeaderPlayedMusic(username, clientMessage);
                break;
        }
    }

    private void onLeaderPlayedMusic(String username, ClientMessage clientMessage) {
        if (peerIsLeader(username)) {
            long start = System.currentTimeMillis();
            getFollowers().forEach(new Consumer<String>() {
                @Override
                public void accept(String follower) {
                    sendMessage(follower, ofType(SYNC_WITH_LEADER)
                            .withMusic(clientMessage.getMusic())
                            .withStateType(clientMessage.getStateType())
                            .withSeconds(clientMessage.getSeconds() + ((System.currentTimeMillis() - start) / 1000)));
                }
            });
        }
    }

    private void onLeaderPausedMusic(String username) {
        if (peerIsLeader(username)) {
            getFollowers().forEach(user -> sendMessage(user, ofType(LEADER_PAUSED_MUSIC)));
        }
    }

    private void onPeerConnected(String user) {
        if (peerIsLeader(user)) {
            this.sendMessage(user, ofType(INFORM_NEW_LEADER));
            this.playNextMusic(user);
        } else {
            this.sendMessage(user, ofType(INFORM_NEW_FOLLOWER));
        }
    }

    private void onSyncWithLeader(String username) {
        if (peerIsFollower(username)) {
            clientsRequestsToSync.add(username);
            sendMessage(getLeader(), ofType(REQUEST_STATE_TO_LEADER));
        }
    }

    private void onLeaderInformingState(String username, ClientMessage clientMessage) {
        if (peerIsLeader(username)) {
            long start = System.currentTimeMillis();
            while (!clientsRequestsToSync.isEmpty()) {
                String syncTo = clientsRequestsToSync.poll();
                sendMessage(syncTo, ofType(SYNC_WITH_LEADER)
                        .withMusic(clientMessage.getMusic())
                        .withStateType(clientMessage.getStateType())
                        .withSeconds(clientMessage.getSeconds() + ((System.currentTimeMillis() - start) / 1000)));
            }
        }
    }

    private void onMusicEndOnLeader(String user) {
        if (peerIsLeader(user)) {
            musicQueue.poll();
            playNextMusic(getLeader());
            getFollowers().forEach(username -> sendMessage(username, ofType(MUSIC_END_ON_LEADER)));
        }
    }

    private boolean peerIsLeader(String username) {
        return username.equals(clients.peek());
    }

    private String getLeader() {
        return clients.peek();
    }

    private boolean peerIsFollower(String username) {
        return !this.peerIsLeader(username);
    }

    private Set<String> getFollowers() {
        return clients.stream().filter(this::peerIsFollower).collect(toSet());
    }

    private void playNextMusic(String username) {
        sendMessage(username, ofType(PLAY_MUSIC).withMusic(musicQueue.peek()));
    }

    private void sendMessage(String username, ServerMessage message) {
        try {
            String value = objectMapper.writeValueAsString(message);
            sessions.get(username).getAsyncRemote().sendObject(value, result -> {
                if (result.getException() != null) {
                    throw new RuntimeException("Unable to send message: " + result.getException(), result.getException());
                }
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}