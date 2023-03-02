package com.engine.color;

import com.badlogic.gdx.graphics.Color;

public class WaterColorProvider extends ChangingColorProvider {
    public WaterColorProvider() {
        super(new Color[]{Color.WHITE, Color.BLUE}, 0.001f);
    }
}
