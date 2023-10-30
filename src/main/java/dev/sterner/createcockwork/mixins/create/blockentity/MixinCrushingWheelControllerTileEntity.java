package dev.sterner.createcockwork.mixins.create.blockentity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlock;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.primitives.AABBd;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

@Mixin(CrushingWheelControllerBlockEntity.class)
public abstract class MixinCrushingWheelControllerTileEntity {

    @Shadow
    public float crushingspeed;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"))
    private List<Entity> redirectBounds(Level instance, Entity entity, AABB area, Predicate<Entity> predicate) {
        if (instance != null) {
            area = VSGameUtilsKt.transformAabbToWorld(instance, area);
            return instance.getEntities(entity, area, predicate);
        }
        return new ArrayList<Entity>();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;intersects(Lnet/minecraft/world/phys/AABB;)Z"))
    private boolean redirectIntersects(AABB instance, AABB other) {
        Level level = ((CrushingWheelControllerBlockEntity) (Object) this).getLevel();
        if (level != null) {
            Iterator<Ship> ships = VSGameUtilsKt.getShipsIntersecting(level, instance).iterator();
            if (ships.hasNext()) {
                AABBd result = new AABBd();
                VectorConversionsMCKt.toJOML(instance).transform(ships.next().getTransform().getWorldToShip(), result);
                instance = VectorConversionsMCKt.toMinecraft(result);
            }
        }
        return instance.intersects(other);
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private void redirectSetDeltaMovement(Entity instance, Vec3 motion, Operation<Void> operation) {

        BlockPos worldPosition = ((CrushingWheelControllerBlockEntity) (Object) this).getBlockPos();
        Direction facing = ((CrushingWheelControllerBlockEntity) (Object) this)
                .getBlockState().getValue(CrushingWheelControllerBlock.FACING);
        Vec3 transformedPos = getTransformedPosition(instance);
        double xMotion = ((worldPosition.getX() + .5) - transformedPos.x) / 2;
        double zMotion = ((worldPosition.getZ() + .5) - transformedPos.z) / 2;

        if (instance.isShiftKeyDown())
            xMotion = zMotion = 0;
        double movement = Math.max(-(this.crushingspeed * 4) / 4f, -.5f) * -facing.getAxisDirection().getStep();

        Vec3 result = new Vec3(
                        facing.getAxis() == Direction.Axis.X ? movement : xMotion,
                        facing.getAxis() == Direction.Axis.Y ? movement : 0f,
                        facing.getAxis() == Direction.Axis.Z ? movement : zMotion
        );

        Ship ship = VSGameUtilsKt.getShipManagingPos(instance.level(), ((CrushingWheelControllerBlockEntity) (Object) this).getBlockPos());
        if (ship != null) {
            Vector3d tempVec = new Vector3d();
            ship.getTransform().getShipToWorld().transformDirection(result.x, result.y, result.z, tempVec);
            result = VectorConversionsMCKt.toMinecraft(tempVec);
            instance.setDeltaMovement(result);
        }
        operation.call(instance, motion);
    }
/*
    @WrapOperation(method = "spawnParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private void wrapOpSpawnParticleX(Level instance, ParticleOptions particleOptions, double x, double y, double z, double a, double b, double c, Operation<Void> operation){
        Ship ship = VSGameUtilsKt.getShipManagingPos(instance, ((CrushingWheelControllerBlockEntity) (Object) this).getBlockPos());
        if (ship != null) {
            Vector3d tempVec = new Vector3d();
            ship.getTransform().getWorldToShip().transformPosition(x, y, z, tempVec);
            Vec3 result = VectorConversionsMCKt.toMinecraft(tempVec);
            RandomSource r = instance.random;
            instance.addParticle(particleOptions, result.x() + r.nextFloat(), result.y() + r.nextFloat(),
                    result.z() + r.nextFloat(), 0, 0, 0);
        } else {
            operation.call(instance, particleOptions, x, y, x, a, b, c);
        }
    }

 */

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getX()D"))
    private double redirectEntityGetX(Entity instance, Operation<Double> operation) {
        Vec3 result = instance.position();
        Ship ship = VSGameUtilsKt.getShipManagingPos(instance.level(), ((CrushingWheelControllerBlockEntity) (Object) this).getBlockPos());
        if (ship != null) {
            Vector3d tempVec = new Vector3d();
            ship.getTransform().getWorldToShip().transformPosition(result.x, result.y, result.z, tempVec);
            result = VectorConversionsMCKt.toMinecraft(tempVec);
            return result.x;
        }
        return operation.call(instance);
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getY()D"))
    private double redirectEntityGetY(Entity instance, Operation<Double> operation) {
        Vec3 result = instance.position();
        Ship ship = VSGameUtilsKt.getShipManagingPos(instance.level(), ((CrushingWheelControllerBlockEntity) (Object) this).getBlockPos());
        if (ship != null) {
            Vector3d tempVec = new Vector3d();
            ship.getTransform().getWorldToShip().transformPosition(result.x, result.y, result.z, tempVec);
            result = VectorConversionsMCKt.toMinecraft(tempVec);
            return result.y;
        }
        return operation.call(instance);
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getZ()D"))
    private double redirectEntityGetZ(Entity instance, Operation<Double> operation) {
        Vec3 result = instance.position();
        Ship ship = VSGameUtilsKt.getShipManagingPos(instance.level(), ((CrushingWheelControllerBlockEntity) (Object) this).getBlockPos());
        if (ship != null) {
            Vector3d tempVec = new Vector3d();
            ship.getTransform().getWorldToShip().transformPosition(result.x, result.y, result.z, tempVec);
            result = VectorConversionsMCKt.toMinecraft(tempVec);
            return result.z;
        }
        return operation.call(instance);
    }

    @Unique
    private Vec3 getTransformedPosition(Entity instance) {
        Vec3 result = instance.position();
        Ship ship = VSGameUtilsKt.getShipManagingPos(instance.level(), ((CrushingWheelControllerBlockEntity) (Object) this).getBlockPos());
        if (ship != null) {
            Vector3d tempVec = new Vector3d();
            ship.getTransform().getWorldToShip().transformPosition(result.x, result.y, result.z, tempVec);
            result = VectorConversionsMCKt.toMinecraft(tempVec);
        }
        return result;
    }
}
