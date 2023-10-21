package dev.sterner.createcockwork.mixinducks.feature.command;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import org.valkyrienskies.core.api.world.ShipWorld;

import java.util.UUID;

public interface VSCommandSource {

    ShipWorld getShipWorld();

    void sendVSMessage(final Component component, final UUID uUID);

    default void sendVSMessage(final Component component) {
        sendVSMessage(component, Util.NIL_UUID);
    }
}
