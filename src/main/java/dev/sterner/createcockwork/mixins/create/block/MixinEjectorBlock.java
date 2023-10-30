package dev.sterner.createcockwork.mixins.create.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.logistics.depot.EjectorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(EjectorBlock.class)
public abstract class MixinEjectorBlock {
    @Redirect(method = "updateEntityAfterFallOn", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;blockPosition()Lnet/minecraft/core/BlockPos;"
    ))
    private BlockPos redirectBlockPosition(Entity instance) {
        return instance.getOnPos();
    }

    @WrapOperation(method = "updateEntityAfterFallOn", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;position()Lnet/minecraft/world/phys/Vec3;"
    ))
    private Vec3 redirectEntityPosition(Entity instance, Operation<Vec3> operation) {
        Vec3 result = instance.position();
        if (VSGameUtilsKt.getShipManagingPos(instance.level(), instance.position()) == null) {
            Ship ship = VSGameUtilsKt.getShipManagingPos(instance.level(), instance.getOnPos());
            if (ship != null) {
                Vector3d tempVec = VectorConversionsMCKt.toJOML(result);
                ship.getWorldToShip().transformPosition(tempVec, tempVec);
                result = VectorConversionsMCKt.toMinecraft(tempVec);
                return result;
            }
        }
        return operation.call(instance);
    }

    @WrapOperation(method = "updateEntityAfterFallOn", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setPos(DDD)V"
    ))
    private void redirectSetPos(Entity instance, double x, double y, double z, Operation<Void> operation) {
        Ship ship = VSGameUtilsKt.getShipManagingPos(instance.level(), instance.getOnPos());
        if (ship != null) {
            Vector3d tempVec = new Vector3d();
            ship.getTransform().getShipToWorld().transformPosition(x, y, z, tempVec);
            instance.setPos(tempVec.x, tempVec.y, tempVec.z);
        } else {
            operation.call(instance, x, y, z);
        }
    }
}
