package com.engine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.engine.particle.LakeParticleProvider;
import com.engine.particle.Particle;
import com.engine.particle.StreamParticleProvider;
import com.engine.texture.TriangleTextureProvider;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.engine.Const.P_RADIUS;
import static com.engine.Const.subSteps;

public class Simulation extends ApplicationAdapter implements InputProcessor {

    private Provider<List<Particle>> streamProvider;
    private SpriteBatch batch;
    private Texture texture;
    private Solver solver;

    @Override
    public void create() {
        this.streamProvider = new StreamParticleProvider();
        this.batch = new SpriteBatch();
        this.texture = new TriangleTextureProvider().provide();
        this.solver = new Solver(subSteps);
        this.solver.addParticles(new LakeParticleProvider(50).provide());
        Gdx.input.setInputProcessor(this);
        Executors.newSingleThreadExecutor().submit(this::displayMetadata);
    }

    @Override
    public void render() {
        streamParticles();
        this.solver.update();
        draw();
    }

    private void streamParticles() {
        if (this.solver.size() > Const.particles) return;
        this.solver.addParticles(this.streamProvider.provide());
    }

    private void displayMetadata() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException ignored) {
            //Ignored
        } finally {
            System.out.println(Gdx.graphics.getFramesPerSecond() + " | " + this.solver.size());
        }
        displayMetadata();
    }


    private void draw() {
        List<Particle> drawable = this.solver.getParticles();
        ScreenUtils.clear(Color.BLACK);
        this.batch.begin();
        float r2 = P_RADIUS << 1;
        drawable.forEach(p -> {
            this.batch.setColor(p.getColor());
            this.batch.draw(texture,
                    p.getPosition().x - P_RADIUS,
                    p.getPosition().y - P_RADIUS,
                    r2,
                    r2
            );
        });
        this.batch.end();
    }

    @Override
    public void dispose() {
        this.batch.dispose();
        this.texture.dispose();
        this.solver.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 center = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);
        List<Particle> particles = this.solver.getParticles();
        for (Particle p : particles) {
            Vector2 acc = center.cpy().sub(p.getPosition()).setLength(10 / p.getPosition().dst(center));
            p.getPrevPosition().add(acc.scl(button == 0 ? 1 : -1));
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
