package com.engine;

import com.badlogic.gdx.math.Vector2;

public interface Const {
    int threads = Runtime.getRuntime().availableProcessors();
    int particles = 20000;
    float delta = 0.004f;
    int subSteps = 3;
    Vector2 GRAVITY = new Vector2(0, -400f);
    int P_RADIUS = 3;


}
