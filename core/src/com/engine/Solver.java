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
    private final CollisionJob[] collisionJobs = new CollisionJob[threads * 3];
    private final Subscriber[] collisionSubscribers = new Subscriber[threads];
    private final LinkedBlockingQueue<CollisionJob> collisionPipeline = new LinkedBlockingQueue<>();
    private final AtomicInteger responseCounter = new AtomicInteger(collisionJobs.length);

    public Solver(int subSteps) {
        this.subSteps = subSteps;
        this.currentGrid = freshGrid();

        for (int i = 0; i < this.collisionJobs.length; i++) {
            this.collisionJobs[i] = new CollisionJob(i, this.collisionJobs.length);
        }

        for (int i = 0; i < threads; i++) {
            this.collisionSubscribers[i] = new Subscriber(this.collisionPipeline, this.responseCounter);
            new Thread(collisionSubscribers[i]).start();
        }
    }

    public final void update() {
        float dt = delta / this.subSteps;
        for (int i = 1; i <= this.subSteps; i++) {
            for (Particle p : particles) {
                p.accelerate(GRAVITY);
            }
            Cell[][] nextGrid = freshGrid();
            while (this.responseCounter.get() < this.collisionJobs.length) {
                //wait for all jobs to finish
            }
            this.responseCounter.set(0);
            for (CollisionJob collisionJob : this.collisionJobs) {
                collisionJob.info(this.currentGrid, nextGrid, dt);
                this.collisionPipeline.add(collisionJob);
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
        for (Subscriber subscriber : this.collisionSubscribers) {
            subscriber.unsubscribe();
        }
    }
}
