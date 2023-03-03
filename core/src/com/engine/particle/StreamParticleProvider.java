package com.engine.particle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.engine.Provider;
import com.engine.color.RainbowColorProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.engine.Const.P_RADIUS;

public class StreamParticleProvider implements Provider<List<Particle>> {

    private final Vector2 position, velocity;
    private final int streamFrames;
    private int frames, pipes = 10;
    private final Provider<Color> colorProvider;


    public StreamParticleProvider() {
        this(
                new Vector2(P_RADIUS << 1, Gdx.graphics.getHeight() - (P_RADIUS)),
                new Vector2(0.8f, -0.5f),
                4,
                new RainbowColorProvider()
        );
    }

    public StreamParticleProvider(Vector2 position, Vector2 velocity, int streamFrames, Provider<Color> colorProvider) {
        this.position = position.cpy();
        this.velocity = velocity.cpy();
        this.streamFrames = streamFrames;
        this.frames = 0;
        this.colorProvider = colorProvider;
    }

    @Override
    public List<Particle> provide() {
        List<Particle> particles = new ArrayList<>();
        if (this.frames > 500 && this.frames % this.streamFrames == 0) {
            for (int i = 1; i <= pipes; i++) {
                Vector2 center = this.position.cpy();
                center.y -= i * (P_RADIUS << 1);
                Particle p = new Particle(center, ThreadLocalRandom.current().nextInt(P_RADIUS, P_RADIUS + 1));
                p.setPrevPosition(p.getPosition().cpy().sub(this.velocity));
                p.setColor(this.colorProvider.provide());
                particles.add(p);
            }
        }
        this.frames++;
        if (frames % 500 == 0) pipes++;
        return particles;
    }
}
