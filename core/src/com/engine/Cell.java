package com.engine;

import com.engine.particle.Particle;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private final ArrayList<Particle> particles = new ArrayList<>();

    public synchronized void addParticle(Particle p) {
        this.particles.add(p);
    }

    public List<Particle> getParticles() {
        return this.particles;
    }

    public boolean isEmpty() {
        return this.particles.isEmpty();
    }
}
