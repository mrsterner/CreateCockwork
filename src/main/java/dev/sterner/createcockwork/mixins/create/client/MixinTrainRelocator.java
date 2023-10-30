package dev.sterner.createcockwork.mixins.create.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.entity.TrainRelocator;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(TrainRelocator.class)
public abstract class MixinTrainRelocator {

    @WrapOperation(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;closerThan(Lnet/minecraft/core/Position;D)Z"))
    private static boolean j$redirectCloserThan(final Vec3 instance, final Position arg, final double d, Operation<Boolean> operation) {
        Vec3 newVec3;
        Level world = Minecraft.getInstance().player.level();
        final Ship ship = VSGameUtilsKt.getShipManagingPos(world, arg);
        if (ship != null) {
            newVec3 = VSGameUtilsKt.toWorldCoordinates(ship, (Vec3) arg);
            return instance.closerThan(newVec3, d);
        }
        return operation.call(instance, arg, d);
    }
}
