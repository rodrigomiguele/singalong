package org.rmiguele.singalong.service;

import io.vertx.core.impl.ConcurrentHashSet;
import org.rmiguele.singalong.domain.QueuedMusic;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

@ApplicationScoped
public class MusicQueue {

    Queue<QueuedMusic> musicQueue = new ConcurrentLinkedQueue<>();

    Set<Consumer<QueuedMusic>> consumers = new ConcurrentHashSet<>();

    public MusicQueue() {
        QueuedMusic music1 = new QueuedMusic();
        music1.setVideo("ZuUmT4XL-bQ");
        music1.setName("Zezinho");
        music1.setTitle("ABC");
        musicQueue.add(music1);
        QueuedMusic music2 = new QueuedMusic();
        music2.setVideo("Q5AvWSNRiN8");
        music2.setName("Huguinho");
        music2.setTitle("BCD");
        musicQueue.add(music2);
        QueuedMusic music3 = new QueuedMusic();
        music3.setVideo("dQFVGt_hT84");
        music3.setName("Luizinho");
        music3.setTitle("CDF");
        musicQueue.add(music3);
    }

    public void registerConsumer(Consumer<QueuedMusic> consumer) {
        this.consumers.add(consumer);
    }

    public QueuedMusic poll() {
        QueuedMusic poll = musicQueue.poll();
        if (musicQueue.isEmpty()) {
            QueuedMusic music1 = new QueuedMusic();
            music1.setVideo("ZuUmT4XL-bQ");
            music1.setName("Zezinho");
            music1.setTitle("ABC");
            musicQueue.add(music1);
            QueuedMusic music2 = new QueuedMusic();
            music2.setVideo("Q5AvWSNRiN8");
            music2.setName("Huguinho");
            music2.setTitle("BCD");
            musicQueue.add(music2);
            QueuedMusic music3 = new QueuedMusic();
            music3.setVideo("dQFVGt_hT84");
            music3.setName("Luizinho");
            music3.setTitle("CDF");
            musicQueue.add(music3);
        }
        return poll;
    }

    public QueuedMusic peek() {
        return musicQueue.peek();
    }

    public boolean add(QueuedMusic queuedMusic) {
        this.consumers.forEach(consumer -> consumer.accept(queuedMusic));
        return musicQueue.add(queuedMusic);
    }

    public Collection<QueuedMusic> getMusicQueue() {
        return Collections.unmodifiableCollection(musicQueue);
    }
}
