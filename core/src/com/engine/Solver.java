package com.engine;

import com.badlogic.gdx.Gdx;
import com.engine.particle.Particle;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.engine.Const.*;

public class Solver {
    private final List<Particle> particles = new LinkedList<>();
    private final int subSteps;
    private Cell[][] currentGrid;
    private final ParticleStepJob[] particleStepJobs = new ParticleStepJob[threads * 3];
    private final Subscriber[] particleStepSubscribers = new Subscriber[threads];
    private final LinkedBlockingQueue<ParticleStepJob> stepPipeline = new LinkedBlockingQueue<>();
    private final AtomicInteger threadResponse = new AtomicInteger(particleStepJobs.length);

    public Solver(int subSteps) {
        this.subSteps = subSteps;
        this.currentGrid = freshGrid();

        for (int i = 0; i < this.particleStepJobs.length; i++) {
            this.particleStepJobs[i] = new ParticleStepJob(i, this.particleStepJobs.length);
        }

        for (int i = 0; i < threads; i++) {
            this.particleStepSubscribers[i] = new Subscriber(this.stepPipeline, this.threadResponse);
            new Thread(this.particleStepSubscribers[i]).start();
        }
    }

    public final void update() {
        float dt = delta / this.subSteps;
        for (int i = 1; i <= this.subSteps; i++) {
            Cell[][] nextGrid = freshGrid();
            while (this.threadResponse.get() < this.particleStepJobs.length) {
                //Wait for all threads to finish solving collisions
            }
            this.threadResponse.set(0);
            for (ParticleStepJob particleStepJob : this.particleStepJobs) {
                particleStepJob.info(this.currentGrid, nextGrid, dt);
                this.stepPipeline.add(particleStepJob);
            }

            this.currentGrid = nextGrid;
        }
    }

    private Cell[][] freshGrid() {
        Cell[][] grid = new Cell[(Gdx.graphics.getWidth() / (P_RADIUS << 1))][(Gdx.graphics.getHeight() / (P_RADIUS << 1))];
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                grid[row][col] = new Cell();
            }
        }

        return grid;
    }

    public void addParticles(List<Particle> particles) {
        for (Particle p : particles) {
            addToGrid(p);
            this.particles.add(p);
        }
    }

    private void addToGrid(Particle p) {
        int[] cords = p.getCoordinates(this.currentGrid);
        this.currentGrid[cords[0]][cords[1]].addParticle(p);
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public int size() {
        return particles.size();
    }

    public void dispose() {
        for (Subscriber subscriber : this.particleStepSubscribers) {
            subscriber.unsubscribe();
        }
    }
}
