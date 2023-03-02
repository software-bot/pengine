package com.engine.particle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.engine.Provider;
import com.engine.color.RainbowColorProvider;
import com.engine.color.WaterColorProvider;

import java.util.ArrayList;
import java.util.List;

import static com.engine.Const.P_RADIUS;

public class LakeParticleProvider implements Provider<List<Particle>> {
    private final int depth;
    private final Provider<Color> colorProvider;

    public LakeParticleProvider(int depth) {
        this.depth = depth;
        this.colorProvider = new RainbowColorProvider();
    }

    public LakeParticleProvider(int depth, Provider<Color> colorProvider) {
        this.depth = depth;
        this.colorProvider = colorProvider;
    }

    @Override
    public List<Particle> provide() {
        List<Particle> particles = new ArrayList<>();
        int r2 = P_RADIUS << 1;

        for (int row = 100; row <= Gdx.graphics.getHeight() - r2; row += (r2 + 1)) {
            for (int col = r2; col < (depth * r2); col += (r2 + 1)) {
                Particle p = new Particle(new Vector2((col), (row)), P_RADIUS);
                p.setPrevPosition(p.getPosition().cpy().sub(new Vector2(0.8f, 0f)));
                p.setColor(this.colorProvider.provide());
                particles.add(p);
            }
        }

        for (int row = 100; row <= Gdx.graphics.getHeight() - 100; row += (r2 + 1)) {
            for (int col = Gdx.graphics.getWidth() - r2; col > (Gdx.graphics.getWidth() - (depth * r2)); col -= (r2 + 1)) {
                Particle p = new Particle(new Vector2((col), (row)), P_RADIUS);
                p.setPrevPosition(p.getPosition().cpy().sub(new Vector2(-0.8f, 0f)));
                p.setColor(this.colorProvider.provide());
                particles.add(p);
            }
        }
        return particles;
    }
}
