package com.engine.color;

import com.badlogic.gdx.graphics.Color;

public class RainbowColorProvider extends ChangingColorProvider {

    public RainbowColorProvider() {
        super(new Color[]{
                new Color(148f / 255f, 0, 211f / 255f, 1f),
                new Color(75f / 255f, 0, 130f / 255f, 1f),
                new Color(0f, 0, 1f, 1f),
                new Color(0f, 1, 0, 1f),
                new Color(1, 1, 0, 1f),
                new Color(1f, 127f / 255f, 0f, 1f),
                new Color(1f, 0f, 0f, 1f)
        }, 0.0001f);
    }
}
