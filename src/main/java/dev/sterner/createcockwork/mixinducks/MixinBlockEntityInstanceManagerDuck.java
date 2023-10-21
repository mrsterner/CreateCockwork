package dev.sterner.createcockwork.mixinducks;

import com.jozufozu.flywheel.api.MaterialManager;
import org.valkyrienskies.core.api.ships.ClientShip;

import java.util.WeakHashMap;

public interface MixinBlockEntityInstanceManagerDuck {

    WeakHashMap<ClientShip, MaterialManager> getShipMaterialManagers();

}
