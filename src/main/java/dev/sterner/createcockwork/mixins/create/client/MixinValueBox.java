package dev.sterner.createcockwork.mixins.create.client;


import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.mod.common.VSClientGameUtils;

@Mixin({ValueBox.class})
public class MixinValueBox {
    public MixinValueBox() {
    }

    @Redirect(
            method = {"render"},
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V"
            ),
            require = 0
    )
    public void redirectTranslate(PoseStack instance, double x, double y, double z) {
        VSClientGameUtils.transformRenderIfInShipyard(instance, x, y, z);
    }
}