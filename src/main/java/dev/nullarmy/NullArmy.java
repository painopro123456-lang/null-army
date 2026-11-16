package dev.nullarmy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class NullArmy implements ModInitializer {
    @Override
    public void onInitialize() {
        PvpBotManager manager = PvpBotManager.INSTANCE;

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(Commands.literal("pvpbot")
                .then(Commands.literal("spawn")
                    .then(Commands.argument("username", net.minecraft.commands.arguments.StringArgumentType.word())
                        .executes(context -> manager.spawn(
                            context.getSource(),
                            net.minecraft.commands.arguments.StringArgumentType.getString(context, "username")
                        ))))
                .then(Commands.literal("kill")
                    .executes(context -> manager.kill(context.getSource())))
                .then(Commands.literal("stop")
                    .executes(context -> manager.stop(context.getSource())))
                .then(Commands.literal("target")
                    .then(Commands.literal("nearest")
                        .executes(context -> manager.targetNearest(context.getSource())))
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> manager.target(context.getSource(), EntityArgument.getPlayer(context, "player")))))
            )
        );

        ServerTickEvents.END_SERVER_TICK.register(manager::tick);
    }
}
