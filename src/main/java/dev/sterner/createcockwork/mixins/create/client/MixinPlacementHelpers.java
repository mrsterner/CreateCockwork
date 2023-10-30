package dev.sterner.createcockwork.mixins.create.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.foundation.placement.PlacementHelpers;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(value = PlacementHelpers.class)
public class MixinPlacementHelpers {
    @WrapOperation(remap = false, method = "drawDirectionIndicator", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/VecHelper;getCenterOf(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 redirectGetCenterOf(Vec3i pos, Operation<Vec3> operation) {
        Level world = Minecraft.getInstance().level;
        if (world != null && VSGameUtilsKt.isBlockInShipyard(world, pos.getX(), pos.getY(), pos.getZ()) && VSGameUtilsKt.getShipManagingPos(world, pos.getX(), pos.getY(), pos.getZ()) instanceof ClientShip ship) {
            Vector3d tempVec = new Vector3d(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5);
            ship.getShipToWorld().transformPosition(tempVec, tempVec);
            return VectorConversionsMCKt.toMinecraft(tempVec);
        }
        return operation.call(pos);
    }
}
