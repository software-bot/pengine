package com.engine;

import com.badlogic.gdx.math.Vector2;

public interface Const {
    int threads = 8;
    int particles = 40000;
    float delta = 0.002f;
    int subSteps = 2;
    Vector2 GRAVITY = new Vector2(0, -400f);
    int P_RADIUS = 2;


}
