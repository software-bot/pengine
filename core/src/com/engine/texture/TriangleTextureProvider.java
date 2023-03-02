package com.engine.texture;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.engine.Provider;

import static com.engine.Const.P_RADIUS;

public class TriangleTextureProvider implements Provider<Texture> {
    @Override
    public Texture provide() {
        Pixmap pixmap = new Pixmap(P_RADIUS << 1, P_RADIUS << 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1f);
        pixmap.fillTriangle(0, 0, P_RADIUS << 1, 0, P_RADIUS, P_RADIUS);
        return new Texture(pixmap);
    }
}
