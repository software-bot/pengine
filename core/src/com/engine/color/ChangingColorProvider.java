package com.engine.color;

import com.badlogic.gdx.graphics.Color;
import com.engine.Provider;

public class ChangingColorProvider implements Provider<Color> {

    private final Color[] colors;
    private float red;
    private float green;
    private float blue;
    private int c = 0;
    private final float step;

    protected ChangingColorProvider(Color[] colors, float step) {
        if (colors == null || colors.length == 0) {
            throw new IllegalStateException("Changing colors need at least one color");
        }
        this.colors = colors;
        this.red = colors[c].r;
        this.green = colors[c].g;
        this.blue = colors[c].b;
        this.step = step;
    }

    @Override
    public Color provide() {
        if (c == colors.length) c = 0;
        if (red < colors[c].r) red += step;
        if (green < colors[c].g) green += step;
        if (blue < colors[c].b) blue += step;

        if (red > colors[c].r) red -= step;
        if (green > colors[c].g) green -= step;
        if (blue > colors[c].b) blue -= step;

        if (Math.abs(red - colors[c].r) < step) red = colors[c].r;
        if (Math.abs(green - colors[c].g) < step) green = colors[c].g;
        if (Math.abs(blue - colors[c].b) < step) blue = colors[c].b;

        if (red == colors[c].r && green == colors[c].g && blue == colors[c].b) c++;

        return new Color(red, green, blue, 1f);
    }
}
