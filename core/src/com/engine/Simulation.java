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
import com.engine.texture.TriangleTextureProvider;

import java.util.List;

import static com.engine.Const.*;

public class Simulation extends ApplicationAdapter implements InputProcessor {

    private final Provider<List<Particle>> initialStateProvider = new LakeParticleProvider(50);
    private SpriteBatch batch;
    private Texture texture;
    private Solver solver;

    @Override
    public void create() {
        this.batch = new SpriteBatch();
        this.texture = new TriangleTextureProvider().provide();
        this.solver = new Solver(subSteps);
        Gdx.input.setInputProcessor(this);

        this.solver.addParticles(this.initialStateProvider.provide());
    }

    @Override
    public void render() {
        this.solver.update();
        draw();
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
        this.solver.click(new Vector2(screenX, Gdx.graphics.getHeight() - screenY), button);
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
