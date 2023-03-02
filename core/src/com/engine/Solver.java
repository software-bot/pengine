package com.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.engine.particle.Particle;
import com.engine.particle.ParticleStateDump;
import com.engine.particle.StreamParticleProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;

import static com.engine.Const.*;

public class Solver {
    private final ArrayList<Particle> particles = new ArrayList<>();
    private final int subSteps;
    private final Particle constrain = new Particle(new Vector2(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f), Gdx.graphics.getWidth() / 2);
    private Cell[][] currentGrid;
    private final CollisionJob[] workers = new CollisionJob[threads];
    private final CyclicBarrier endPoint;
    private final CyclicBarrier startPoint;
    private final Provider<List<Particle>> particleStream;

    public Solver(int subSteps) {
        this.subSteps = subSteps;
        this.currentGrid = freshGrid();
        this.startPoint = new CyclicBarrier(threads + 1);
        this.endPoint = new CyclicBarrier(threads + 1);
        for (int i = 0; i < this.workers.length; i++) {
            this.workers[i] = new CollisionJob(
                    i,
                    this.workers.length,
                    this.constrain,
                    this.startPoint,
                    this.endPoint
            );

            new Thread(workers[i]).start();
        }
        this.particleStream = new StreamParticleProvider();
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

    public ArrayList<Particle> getParticles() {
        return particles;
    }

    private void addToGrid(Particle p) {
        int[] cords = p.getCoordinates(this.currentGrid);
        this.currentGrid[cords[0]][cords[1]].addParticle(p);
    }

    public void click(Vector2 center, int dir) {
        if (dir == 0) {
            for (Particle p : particles) {
                Vector2 acc = center.cpy().sub(p.getPosition()).setLength(50 / p.getPosition().dst(center));
                p.getPrevPosition().add(acc.scl(-1));
            }
        } else if (dir == 1)
            for (Particle p : particles) {
                Vector2 acc = center.cpy().sub(p.getPosition()).setLength(80 / p.getPosition().dst(center));
                p.getPrevPosition().add(acc);
            }
    }

    public void update() {
        float dt = delta / subSteps;
        for (int i = 1; i <= subSteps; i++) {
            for (Particle p : particles) {
                applyGravity(p);
            }
            Cell[][] nextGrid = freshGrid();
            for (CollisionJob worker : workers) {
                worker.info(this.currentGrid, nextGrid, dt);
            }
            start();
            waitForEnd();
            this.currentGrid = nextGrid;
        }
    }

    private void streamParticles() {
        if (size() > Const.particles) return;

        addParticles(this.particleStream.provide());
    }

    private List<ParticleStateDump> dumpState() {
        List<ParticleStateDump> state = new ArrayList<>(this.particles.size());
        for (Particle p : this.particles) {
            state.add(new ParticleStateDump(p.getPosition(), p.getColor()));
        }
        return state;
    }

    private void applyBoundaries(Particle p) {
        float dist = this.constrain.getPosition().dst(p.getPosition());
        if (dist > this.constrain.getRadius() - p.getRadius()) {
            Vector2 v = this.constrain.getPosition().cpy().sub(p.getPosition());
            v.setLength(dist - (this.constrain.getRadius() - p.getRadius()));
            p.getPosition().add(v);
        }
    }

    private void applyGravity(Particle p) {
        p.accelerate(GRAVITY);
    }

    private void start() {
        try {
            startPoint.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    private void waitForEnd() {
        try {
            endPoint.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
    }

    public int size() {
        return particles.size();
    }

    public List<Particle> currentState() {
        return new ArrayList<>(this.particles);
    }
}
