package dev.sterner.createcockwork.mixins.create.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.IControlContraption;
import dev.sterner.createcockwork.mixinducks.mod_compat.create.IMixinControlledContraptionEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ControlledContraptionEntity.class)
public abstract class MixinControlledContraptionEntity extends AbstractContraptionEntity implements IMixinControlledContraptionEntity {
    @Shadow
    protected abstract IControlContraption getController();

    public MixinControlledContraptionEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    //Region start - fix equals -0 != 0
    private Vec3 flatten(Vec3 vec3) {
        if (vec3.x == -0)
            vec3 = new Vec3(0, vec3.y, vec3.z);
        if (vec3.y == -0)
            vec3 = new Vec3(vec3.x, 0, vec3.z);
        if (vec3.z == -0)
            vec3 = new Vec3(vec3.x, vec3.y, 0);
        return vec3;
    }

    @WrapOperation(method = "shouldActorTrigger", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;equals(Ljava/lang/Object;)Z"))//TODO wrapop doesnt do anything here
    private boolean redirectEquals(Vec3 instance, Object vec3, Operation<Boolean> operation) {
        Vec3 other = (Vec3) vec3;
        other = flatten(other);
        instance = flatten(instance);
        return instance.equals(other);
    }

    //Region end

    @Override
    public IControlContraption grabController() {
        return getController();
    }
}
