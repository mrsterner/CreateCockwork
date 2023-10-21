package dev.sterner.createcockwork.mixins.create.client;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.render.ContraptionRenderInfo;
import com.simibubi.create.content.contraptions.render.FlwContraption;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSClientGameUtils;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin({FlwContraption.class})
public class MixinFlwContraption extends ContraptionRenderInfo {
    public MixinFlwContraption(Contraption contraption, VirtualRenderWorld renderWorld) {
        super(contraption, renderWorld);
    }

    @Inject(
            at = {@At("HEAD")},
            method = {"setupModelViewPartial"},
            cancellable = true,
            remap = false
    )
    private static void beforeSetupModelViewPartial(Matrix4f matrix, Matrix4f modelMatrix, AbstractContraptionEntity entity, double camX, double camY, double camZ, float pt, CallbackInfo ci) {
        Ship var12 = VSGameUtilsKt.getShipManaging(entity);
        if (var12 instanceof ClientShip ship) {
            VSClientGameUtils.transformRenderWithShip(ship.getRenderTransform(), matrix,
                    Mth.lerp(pt, entity.xOld, entity.getX()),
                    Mth.lerp(pt, entity.yOld, entity.getY()),
                    Mth.lerp(pt, entity.zOld, entity.getZ()), camX, camY, camZ);
            matrix.mul(modelMatrix);
            ci.cancel();
        }

    }

    @Redirect(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/AABB;move(DDD)Lnet/minecraft/world/phys/AABB;"
            ),
            method = {"beginFrame"}
    )
    private AABB transformLightboxToWorld(AABB aabb, double negCamX, double negCamY, double negCamZ) {
        return VSGameUtilsKt.transformAabbToWorld(this.contraption.entity.level(), aabb).move(negCamX, negCamY, negCamZ);
    }
}