package dev.sterner.createcockwork.mixinducks;

import com.jozufozu.flywheel.backend.RenderLayer;
import org.joml.Matrix4f;

public interface MixinInstancingEngineDuck {
    void render(Matrix4f viewProjection, double camX, double camY, double camZ, RenderLayer layer);
}
