package dev.sterner.createcockwork.mixins.create.client;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.render.ContraptionRenderDispatcher;
import net.minecraft.core.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.mod.common.VSClientGameUtils;

@Mixin(value = ContraptionRenderDispatcher.class, remap = false)
public abstract class MixinContraptionRenderDispatcher {

    @WrapOperation(//TODO wrapop doesnt to anything
            method = "renderActors",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/jozufozu/flywheel/util/transform/TransformStack;translate(Lnet/minecraft/core/Vec3i;)Ljava/lang/Object;"
            )
    )
    private static Object redirectTranslate(final TransformStack instance, final Vec3i vec3i, Operation<Object> operation) {
        VSClientGameUtils.transformRenderIfInShipyard((PoseStack) instance, vec3i.getX(), vec3i.getY(), vec3i.getZ());
        return instance;
    }
}
