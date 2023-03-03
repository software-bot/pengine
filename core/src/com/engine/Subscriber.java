package com.engine;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Subscriber implements Runnable {
    private final LinkedBlockingQueue<ParticleStepJob> eventBus;
    private volatile boolean isSubscribed = true;
    private final AtomicInteger responseCounter;

    public Subscriber(LinkedBlockingQueue<ParticleStepJob> collisionPipeline, AtomicInteger responseCounter) {
        this.eventBus = collisionPipeline;
        this.responseCounter = responseCounter;
    }

    @Override
    public void run() {
        while (isSubscribed) {
            try {
                ParticleStepJob job = eventBus.poll(10, TimeUnit.SECONDS);
                if (job != null) {
                    job.execute();
                    this.responseCounter.incrementAndGet();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public final void unsubscribe() {
        this.isSubscribed = false;
    }
}
