package dev.sterner.createcockwork.mixins.create;


import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin({BlockBreakingKineticBlockEntity.class})
public abstract class MixinBlockBreakingKineticTileEntity {
    public MixinBlockBreakingKineticTileEntity() {
    }

    @Shadow
    protected abstract BlockPos getBreakingPos();

    @Redirect(
            method = {"tick"},
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/kinetics/base/BlockBreakingKineticBlockEntity;getBreakingPos()Lnet/minecraft/core/BlockPos;"
            ),
            remap = false
    )
    private BlockPos getBreakingBlockPos(BlockBreakingKineticBlockEntity self) {
        BlockPos orig = this.getBreakingPos();
        Ship ship = VSGameUtilsKt.getShipManagingPos(self.getLevel(), self.getBlockPos());
        Vec3 origin;
        Vec3 target;
        if (ship != null) {
            origin = VectorConversionsMCKt.toMinecraft(ship.getShipToWorld().transformPosition(VectorConversionsMCKt.toJOMLD(self.getBlockPos()).add(0.5, 0.5, 0.5)));
            target = VectorConversionsMCKt.toMinecraft(ship.getShipToWorld().transformPosition(VectorConversionsMCKt.toJOMLD(orig).add(0.5, 0.5, 0.5)));
        } else {
            origin = Vec3.atCenterOf(self.getBlockPos());
            target = Vec3.atCenterOf(orig);
        }

        Vec3 diff = target.subtract(origin);
        BlockHitResult result = self.getLevel().clip(new ClipContext(origin.add(diff.scale(0.4)), target.add(diff.scale(0.2)), Block.COLLIDER, Fluid.NONE, null));
        return result.getType() == Type.MISS ? orig : result.getBlockPos();
    }
}