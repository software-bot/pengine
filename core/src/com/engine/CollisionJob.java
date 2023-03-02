
package com.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.engine.particle.Particle;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CollisionJob implements Runnable {
    public final static int[][] moves = new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
    private final int id, all;
    private int start, end;
    private volatile Cell[][] grid;
    private volatile Cell[][] nextGrid;
    private volatile float delta;
    private final Particle constrain;
    private final CyclicBarrier startPoint;
    private final CyclicBarrier endPoint;

    public CollisionJob(int id, int all, Particle constrain, CyclicBarrier startPoint, CyclicBarrier endPoint) {
        this.id = id;
        this.all = all;
        this.constrain = constrain;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public void info(Cell[][] grid, Cell[][] nextGrid, float delta) {
        this.delta = delta;
        this.grid = grid;
        this.nextGrid = nextGrid;
        int step = (this.grid[0].length / all);
        this.start = step * id;
        if (this.id == all - 1) {
            this.end = grid[0].length;
        } else
            this.end = start + step;
    }

    @Override
    public void run() {
        while (true) {
            waitForStart();
            for (int row = 0; row < grid.length; row++) {
                for (int col = start; col < end; col++) {
                    Cell main = this.grid[row][col];
                    List<Particle> particleList = main.getParticles();
                    for (int i = 0; i < particleList.size(); i++) {
                        Particle p1 = particleList.get(i);
                        step(p1, delta);
                        addToGrid(p1, nextGrid);
                        if (row == 0 || row == grid.length - 1 || col == 0 || col == grid[row].length - 1) {
                            applyBoundaries2(p1);
                        }
                        for (int j = i + 1; j < particleList.size(); j++) {
                            collide(p1, particleList.get(j));
                        }
                        collideWithNeighbours(p1, row, col);
                    }
                }
            }
            waitForEnd();
        }
    }

    private void waitForStart() {
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

    private void collideWithNeighbours(Particle p, int row, int col) {
        for (int[] move : moves) {
            int nRow = row + move[0];
            int nCol = col + move[1];
            if (isOutOfBounds(nRow, nCol)) continue;
            final Cell n = this.grid[nRow][nCol];
            List<Particle> NPList = n.getParticles();
            for (int i = 0; i < NPList.size(); i++) {
                collide(p, NPList.get(i));
            }
        }
    }

    private boolean isOutOfBounds(int row, int col) {
        return row < 0 || row >= grid.length || col < 0 || col >= grid[row].length || grid[row][col].isEmpty();
    }

    private void collide(Particle p1, Particle p2) {
        if (p1 == p2 || p1 == null || p2 == null) return;
        float dst = p1.getPosition().dst(p2.getPosition());
        int minDst = p1.getRadius() + p2.getRadius();

        if (dst < minDst) {
            Vector2 v = p1.getPosition().cpy().sub(p2.getPosition());
            Vector2 n = v.scl(1f / dst);
            float delta = 0.225f * (dst - minDst);

            p1.getPosition().sub(n.scl(delta));
            p2.getPosition().add(n);
        }
    }

    private void applyBoundaries2(Particle p) {
        float maxDamp = 0.8f;
        if (p.getPosition().x - p.getRadius() < 0 || p.getPosition().x + p.getRadius() > Gdx.graphics.getWidth()) {
            Vector2 v = p.getVelocity(1f);
            float ang;
            if (p.getPosition().x - p.getRadius() < 0) {
                ang = (float) Math.min(Math.sqrt(2f / Math.abs(180 - v.angleDeg())), maxDamp);
                p.getPosition().x = p.getRadius();
            } else {
                ang = (float) Math.min(Math.sqrt(2f / Math.abs(360 - v.angleDeg())), maxDamp);
                p.getPosition().x = Gdx.graphics.getWidth() - p.getRadius();
            }
            v.scl(1 - ang);
            v.x *= -1;
            p.setPrevPosition(p.getPosition().cpy().sub(v));
        }
        if (p.getPosition().y - p.getRadius() < 0 || p.getPosition().y + p.getRadius() > Gdx.graphics.getHeight()) {
            Vector2 v = p.getVelocity(1f);
            float ang;
            if (p.getPosition().y - p.getRadius() < 0) {
                float test = Math.abs(270 - v.angleDeg());
                if (test > 10) ang = 0.01f;
                else
                    ang = (float) Math.min(Math.sqrt(2f / Math.abs(270 - v.angleDeg())), maxDamp);
                p.getPosition().y = p.getRadius();
            } else {
                ang = (float) Math.min(Math.sqrt(2f / Math.abs(90 - v.angleDeg())), maxDamp);
                p.getPosition().y = Gdx.graphics.getWidth() - p.getRadius();
            }

            v.scl(1 - ang);
            v.y *= -1;
            p.setPrevPosition(p.getPosition().cpy().sub(v));
        }
    }

    private void addToGrid(Particle p, Cell[][] grid) {
        int[] cords = p.getCoordinates(grid);
        grid[cords[0]][cords[1]].addParticle(p);
    }

    private void step(Particle p, float delta) {
        p.update(delta);
    }
}
