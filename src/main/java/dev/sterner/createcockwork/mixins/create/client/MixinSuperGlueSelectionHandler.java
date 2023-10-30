package dev.sterner.createcockwork.mixins.create.client;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.contraptions.glue.SuperGlueSelectionHandler;
import com.simibubi.create.foundation.utility.RaycastHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.primitives.AABBic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.Iterator;

@Mixin({SuperGlueSelectionHandler.class})
public abstract class MixinSuperGlueSelectionHandler {
    @Unique
    private Vec3 newTarget;

    public MixinSuperGlueSelectionHandler() {
    }

    @WrapOperation(
            method = {"tick"},
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/utility/RaycastHelper;getTraceOrigin(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/phys/Vec3;"
            ),
            remap = false
    )
    private Vec3 redirectGetTraceOrigin(Player playerIn, Operation<Vec3> operation) {
        Minecraft mc = Minecraft.getInstance();
        double range = playerIn.getAttribute(ForgeMod.ENTITY_REACH.get()).getValue() + 1.0;
        Vec3 origin = RaycastHelper.getTraceOrigin(playerIn);
        Vec3 target = RaycastHelper.getTraceTarget(playerIn, range, origin);
        AABB searchAABB = (new AABB(origin, target)).inflate(0.25, 2.0, 0.25);
        Iterator<Ship> ships = VSGameUtilsKt.getShipsIntersecting(playerIn.level(), searchAABB).iterator();
        if (ships.hasNext()) {
            Ship ship = ships.next();
            Matrix4d world2Ship = (Matrix4d) ship.getTransform().getWorldToShip();
            AABBic shAABBi = ship.getShipAABB();
            AABB shipAABB = new AABB(shAABBi.minX(), shAABBi.minY(), shAABBi.minZ(), shAABBi.maxX(), shAABBi.maxY(), shAABBi.maxZ());
            origin = VectorConversionsMCKt.toMinecraft(world2Ship.transformPosition(VectorConversionsMCKt.toJOML(origin)));
            target = VectorConversionsMCKt.toMinecraft(world2Ship.transformPosition(VectorConversionsMCKt.toJOML(target)));
            Quaterniond tempQuat = new Quaterniond();
            if (playerIn.getVehicle() != null && playerIn.getVehicle().getBoundingBox().intersects(shipAABB.inflate(20.0))) {
                ship.getTransform().getWorldToShip().getNormalizedRotation(tempQuat);
                tempQuat.invert();
                Vector3d offset = VectorConversionsMCKt.toJOML(target.subtract(origin));
                tempQuat.transform(offset);
                target = origin.add(VectorConversionsMCKt.toMinecraft(offset));
            }
            this.newTarget = target;
            return origin;
        }

        return operation.call(playerIn);
    }

    @Redirect(
            method = {"tick"},
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/utility/RaycastHelper;getTraceTarget(Lnet/minecraft/world/entity/player/Player;DLnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"
            ),
            remap = false
    )
    private Vec3 redirectGetTraceTarget(Player playerIn, double range, Vec3 origin) {
        return this.newTarget;
    }
}