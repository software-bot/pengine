package com.engine.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;


public class ParticleStateDump {
    public final Vector2 position;
    public final Color color;

    public ParticleStateDump(Vector2 position, Color color) {
        this.position = position.cpy();
        this.color = color;
    }
}
