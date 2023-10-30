package dev.sterner.createcockwork.mixins.create.packets;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.track.CurvedTrackDestroyPacket;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(value = CurvedTrackDestroyPacket.class)
public abstract class MixinCurvedTrackDestroyPacket {
    @Unique
    private Level world;

    @WrapOperation(
            method = "applySettings(Lnet/minecraft/server/level/ServerPlayer;Lcom/simibubi/create/content/trains/track/TrackBlockEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/BlockPos;closerThan(Lnet/minecraft/core/Vec3i;D)Z"
            )
    )
    private boolean e$redirectCloserThan(final BlockPos instance, final Vec3i vec3i, final double v, Operation<Boolean> operation) {
        BlockPos blockPos;
        if (VSGameUtilsKt.isBlockInShipyard(this.world, instance)) {
            final Ship ship = VSGameUtilsKt.getShipManagingPos(this.world, instance);
            final Vector3d tempVec = VSGameUtilsKt.toWorldCoordinates(ship, instance);
            blockPos = new BlockPos((int) tempVec.x, (int) tempVec.y, (int) tempVec.z);
            return blockPos.closerThan(vec3i, v);
        }
        return operation.call(instance, vec3i, v);
    }

    @Inject(remap = false,
            method = "applySettings(Lnet/minecraft/server/level/ServerPlayer;Lcom/simibubi/create/content/trains/track/TrackBlockEntity;)V",
            at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void injectCaptureLevel(final ServerPlayer player, final TrackBlockEntity te, final CallbackInfo ci) {
        this.world = player.level();
    }
}
