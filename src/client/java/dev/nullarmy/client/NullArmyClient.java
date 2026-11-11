package dev.nullarmy.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class NullArmyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess) -> NullArmyCommands.register(dispatcher)
        );
        ClientTickEvents.END_CLIENT_TICK.register(NullArmyController::tick);
    }
}
