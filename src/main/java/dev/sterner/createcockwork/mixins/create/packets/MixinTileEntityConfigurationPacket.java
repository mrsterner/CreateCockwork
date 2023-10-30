package dev.sterner.createcockwork.mixins.create.packets;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(BlockEntityConfigurationPacket.class)
public abstract class MixinTileEntityConfigurationPacket {
    @Unique
    private Level _clockworkLevel;

    @WrapOperation(
            method = "lambda$handle$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/BlockPos;closerThan(Lnet/minecraft/core/Vec3i;D)Z"
            )
    )
    private boolean c$redirectCloserThan(final BlockPos instance, final Vec3i vec3i, final double v, Operation<Boolean> operation) {
        BlockPos blockPos;
        if (VSGameUtilsKt.isBlockInShipyard(this._clockworkLevel, instance)) {
            final Ship ship = VSGameUtilsKt.getShipManagingPos(this._clockworkLevel, instance);
            final Vector3d tempVec = VSGameUtilsKt.toWorldCoordinates(ship, instance);
            blockPos = new BlockPos((int) tempVec.x, (int) tempVec.y, (int) tempVec.z);
            return blockPos.closerThan(vec3i, v);
        }
        return operation.call(instance, vec3i, v);
    }

    @Redirect(
            method = "lambda$handle$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;isLoaded(Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    private boolean injectCaptureLevel(final Level instance, final BlockPos pos) {
        this._clockworkLevel = instance;
        return instance.isLoaded(pos);
    }
}
