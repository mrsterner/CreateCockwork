package dev.sterner.createcockwork.mixins.create;


import com.simibubi.create.content.kinetics.millstone.MillstoneBlock;
import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import com.simibubi.create.content.processing.basin.BasinBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {MillstoneBlock.class, BasinBlock.class, AbstractChuteBlock.class})
public class MixinBlocks {
    public MixinBlocks() {
    }
/*TODO
    @Redirect(
            method = "updateEntityAfterFallOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;position()Lnet/minecraft/world/phys/Vec3;")
    )
    protected BlockPos redirectBlockPositiond() {

    }

    @Redirect(
            method = {"updateEntityAfterFallOn"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;blockPosition()Lnet/minecraft/core/BlockPos;"
            )
    )
    protected BlockPos redirectBlockPosition(Entity entity) {
        Iterator<Ship> ships = VSGameUtilsKt.getShipsIntersecting(entity.level(), entity.getBoundingBox()).iterator();
        if (ships.hasNext()) {
            Vector3d pos = ((Ship)ships.next()).getWorldToShip().transformPosition(VectorConversionsMCKt.toJOML(entity.position()));
            return new BlockPos((int)Math.floor(pos.x), (int)Math.floor(pos.y), (int)Math.floor(pos.z));
        } else {
            return entity.blockPosition();
        }
    }

    @Redirect(
            method = {"updateEntityAfterFallOn"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;position()Lnet/minecraft/world/phys/Vec3;"
            ),
            require = 0,
            remap = false
    )
    Vec3 redirectPosition(Entity entity) {
        Iterator<Ship> ships = VSGameUtilsKt.getShipsIntersecting(entity.level(), entity.getBoundingBox()).iterator();
        return ships.hasNext() ? VectorConversionsMCKt.toMinecraft(((Ship)ships.next()).getWorldToShip().transformPosition(VectorConversionsMCKt.toJOML(entity.m_20182_()))) : entity.m_20182_();
    }

 */
}
