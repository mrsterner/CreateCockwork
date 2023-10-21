package dev.sterner.createcockwork.mixins.create;


import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.valkyrienskies.core.api.ships.ContraptionWingProvider;

@Mixin({AbstractContraptionEntity.class})
public abstract class MixinAbstractContraptionEntity implements ContraptionWingProvider {
    @Unique
    private int wingGroupId = -1;

    public MixinAbstractContraptionEntity() {
    }

    public int getWingGroupId() {
        return this.wingGroupId;
    }

    public void setWingGroupId(int wingGroupId) {
        this.wingGroupId = wingGroupId;
    }
/*TODO conversion kt class
    @Inject(
            method = {"tick"},
            at = {@At("HEAD")}
    )
    private void postTick(CallbackInfo ci) {
        AbstractContraptionEntity thisAsAbstractContraptionEntity = (AbstractContraptionEntity)AbstractContraptionEntity.class.cast(this);
        Level level = thisAsAbstractContraptionEntity.level();
        if (this.wingGroupId != -1 && level instanceof ServerLevel serverLevel) {
            LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos(serverLevel, VectorConversionsMCKt.toJOML(thisAsAbstractContraptionEntity.position()));
            ((WingManager)ship.getAttachment(WingManager.class)).setWingGroupTransform(this.wingGroupId, this.computeContraptionWingTransform());
        }

    }

    public @NotNull Matrix4dc computeContraptionWingTransform() {
        AbstractContraptionEntity thisAsAbstractContraptionEntity = (AbstractContraptionEntity)AbstractContraptionEntity.class.cast(this);
        Matrix3d rotationMatrix = CreateConversionsKt.toJOML(thisAsAbstractContraptionEntity.getRotationState().asMatrix());
        Vector3d pos = VectorConversionsMCKt.toJOML(thisAsAbstractContraptionEntity.position());
        return (new Matrix4d(rotationMatrix)).setTranslation(pos);
    }

 */
}
