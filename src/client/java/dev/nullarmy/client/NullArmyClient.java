package dev.nullarmy.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

public final class NullArmyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess) -> NullArmyCommands.register(dispatcher)
        );
        ClientTickEvents.END_CLIENT_TICK.register(NullArmyController::tick);
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("null_army", "army_hud"), NullArmyHud::render);
    }
}
