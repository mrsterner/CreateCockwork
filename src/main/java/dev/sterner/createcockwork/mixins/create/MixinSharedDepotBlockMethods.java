package dev.sterner.createcockwork.mixins.create;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.logistics.depot.SharedDepotBlockMethods;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(SharedDepotBlockMethods.class)
public abstract class MixinSharedDepotBlockMethods {
    @WrapOperation(method = "onLanded", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;blockPosition()Lnet/minecraft/core/BlockPos;"
    ))
    private static BlockPos redirectBlockPosition(Entity instance, Operation<BlockPos> operation) {
        BlockPos result;
        Ship ship = VSGameUtilsKt.getShipObjectManagingPos(instance.level(), instance.getOnPos());
        if (ship != null) {
            Vector3d tempVec = new Vector3d(instance.position().x, instance.position().y, instance.position().z);
            ship.getWorldToShip().transformPosition(tempVec, tempVec);
            result = new BlockPos((int) Math.floor(tempVec.x), (int) Math.floor(tempVec.y), (int) Math.floor(tempVec.z));
            return result;
        }
        return operation.call(instance);
    }
}
