package dev.sterner.createcockwork.mixins.create.packets;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsInputPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(ControlsInputPacket.class)
public abstract class MixinControlsInputPacket {
    @Unique
    private Level level;

    @WrapOperation(
            method = "lambda$handle$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;closerThan(Lnet/minecraft/core/Position;D)Z"
            )
    )
    private boolean b$redirectCloserThan(final Vec3 instance, final Position arg, final double d, Operation<Boolean> operation) {
        Vec3 newVec3;
        if (VSGameUtilsKt.isBlockInShipyard(this.level, new BlockPos((int) instance.x, (int) instance.y, (int) instance.z))) {
            final Ship ship = VSGameUtilsKt.getShipManagingPos(this.level, instance);
            newVec3 = VSGameUtilsKt.toWorldCoordinates(ship, instance);
            return newVec3.closerThan(arg, d);
        }
        return operation.call(instance, arg, d);
    }

    @Redirect(
            method = "lambda$handle$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;getCommandSenderWorld()Lnet/minecraft/world/level/Level;"
            )
    )
    private Level stealLevel(ServerPlayer player) {
        return (level = player.getCommandSenderWorld());
    }
}
