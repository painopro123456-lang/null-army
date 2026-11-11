package dev.nullarmy.client;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public final class NullArmyCommands {
    private NullArmyCommands() {}

    public static void register(com.mojang.brigadier.CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("null")
            .then(ClientCommands.literal("spawn")
                .executes(context -> {
                    NullArmyController.spawnDefault();
                    return reply(context, "Spawned the default army: 15 Null soldiers + 2 commanders.");
                })
                .then(ClientCommands.argument("soldiers", IntegerArgumentType.integer(0, 64))
                    .then(ClientCommands.argument("commanders", IntegerArgumentType.integer(0, 64))
                        .executes(context -> {
                            int soldiers = IntegerArgumentType.getInteger(context, "soldiers");
                            int commanders = IntegerArgumentType.getInteger(context, "commanders");
                            if (soldiers + commanders > 64) return reply(context, "Maximum local army size is 64 units.");
                            NullArmyController.spawn(soldiers, commanders);
                            return reply(context, "Created " + soldiers + " soldiers and " + commanders + " commanders.");
                        }))))
            .then(ClientCommands.literal("set").then(ClientCommands.literal("kit")
                .then(ClientCommands.argument("playerKit", StringArgumentType.word()).executes(c -> {
                    NullArmyController.Kit kit = parseKit(StringArgumentType.getString(c, "playerKit"));
                    if (kit == null || kit == NullArmyController.Kit.NONE || kit == NullArmyController.Kit.ARCHER || kit == NullArmyController.Kit.ASSAULT)
                        return reply(c, "Choose sword, mace, or elytra_mace.");
                    if (!NullArmyController.givePlayerKit(kit)) return reply(c, "You must be in a world to equip a kit.");
                    return reply(c, "Applied your " + kit.commandName() + " kit locally. A multiplayer server may overwrite client-only equipment.");
                }))))
            .then(ClientCommands.literal("army").then(ClientCommands.literal("kit")
                .then(ClientCommands.argument("armyKit", StringArgumentType.word()).executes(c -> {
                    NullArmyController.Kit kit = parseKit(StringArgumentType.getString(c, "armyKit"));
                    if (kit == null || kit == NullArmyController.Kit.NONE || kit == NullArmyController.Kit.ARCHER || kit == NullArmyController.Kit.ASSAULT)
                        return reply(c, "Choose sword, mace, or elytra_mace.");
                    NullArmyController.setArmyKit(kit);
                    return reply(c, "Equipped the Null army with " + kit.commandName() + ".");
                }))))
            .then(ClientCommands.literal("whitelist")
                .then(ClientCommands.literal("add").then(ClientCommands.argument("name", StringArgumentType.word()).executes(c -> {
                    String name = StringArgumentType.getString(c, "name");
                    NullArmyController.addWhitelist(name);
                    return reply(c, name + " is protected.");
                })))
                .then(ClientCommands.literal("remove").then(ClientCommands.argument("name", StringArgumentType.word()).executes(c -> {
                    String name = StringArgumentType.getString(c, "name");
                    if (!NullArmyController.removeWhitelist(name)) return reply(c, "Cannot remove owner protection or player is not listed.");
                    return reply(c, name + " removed from whitelist.");
                })))
                .then(ClientCommands.literal("list").executes(c -> reply(c, "Protected players: " + NullArmyController.whitelistStatus()))))
            .then(ClientCommands.literal("kit")
                .executes(context -> reply(context, "Current kit: " + NullArmyController.selectedKit().commandName()
                    + ". Use /null set kit <kit> to equip yourself."))
                .then(ClientCommands.literal("sword").executes(context -> kit(context, NullArmyController.Kit.SWORD)))
                .then(ClientCommands.literal("mace").executes(context -> kit(context, NullArmyController.Kit.MACE)))
                .then(ClientCommands.literal("elytra_mace").executes(context -> kit(context, NullArmyController.Kit.ELYTRA_MACE))))
            .then(ClientCommands.literal("target")
                .then(ClientCommands.argument("who", StringArgumentType.word()).executes(context -> {
                    String who = StringArgumentType.getString(context, "who");
                    if (who.equalsIgnoreCase("clear")) {
                        NullArmyController.setTarget("");
                        return reply(context, "Target cleared.");
                    }
                    if (NullArmyController.isWhitelisted(who)) return reply(context, who + " is whitelisted and cannot be targeted.");
                    NullArmyController.setTarget(who);
                    return reply(context, "Target selected: " + who + ". Use /null attack to issue the order.");
                })))
            .then(ClientCommands.literal("goto")
                .then(ClientCommands.argument("x", DoubleArgumentType.doubleArg())
                    .then(ClientCommands.argument("y", DoubleArgumentType.doubleArg())
                        .then(ClientCommands.argument("z", DoubleArgumentType.doubleArg()).executes(context -> {
                            double x = DoubleArgumentType.getDouble(context, "x");
                            double y = DoubleArgumentType.getDouble(context, "y");
                            double z = DoubleArgumentType.getDouble(context, "z");
                            NullArmyController.goTo(x, y, z);
                            return reply(context, "Army movement order set to " + x + " " + y + " " + z + ".");
                        })))))
            .then(ClientCommands.literal("follow").executes(context -> {
                NullArmyController.setOrder(NullArmyController.ArmyOrder.FOLLOW);
                return reply(context, "Army ordered to follow you.");
            }))
            .then(ClientCommands.literal("defend").executes(context -> {
                NullArmyController.setOrder(NullArmyController.ArmyOrder.DEFEND);
                return reply(context, "Army ordered to defend its position.");
            }))
            .then(ClientCommands.literal("attack").executes(context -> {
                if (!NullArmyController.issueAttackOrder()) return reply(context, "Select a loaded, non-whitelisted player first with /null target <player>.");
                return reply(context, "Attack order issued; tracking the player's live position.");
            }))
            .then(ClientCommands.literal("stop").executes(context -> {
                NullArmyController.setOrder(NullArmyController.ArmyOrder.STOP);
                return reply(context, "Army halted.");
            }))
            .then(ClientCommands.literal("formation")
                .then(ClientCommands.literal("line").executes(context -> formation(context, NullArmyController.Formation.LINE)))
                .then(ClientCommands.literal("column").executes(context -> formation(context, NullArmyController.Formation.COLUMN)))
                .then(ClientCommands.literal("wedge").executes(context -> formation(context, NullArmyController.Formation.WEDGE))))
            .then(ClientCommands.literal("status").executes(context -> reply(context, NullArmyController.status())))
            .then(ClientCommands.literal("help").executes(context -> reply(context,
                "spawn | set kit <kit> (player) | army kit <kit> | whitelist <add|remove|list> | target <player|clear> | goto <x> <y> <z> | follow | defend | attack | stop | formation <line|column|wedge> | status | dismiss")))
            .then(ClientCommands.literal("dismiss").executes(context -> {
                NullArmyController.dismiss();
                return reply(context, "All locally tracked Null units dismissed.");
            }))
        );
    }

    private static NullArmyController.Kit parseKit(String name) {
        for (NullArmyController.Kit kit : NullArmyController.Kit.values()) {
            if (kit.commandName().equalsIgnoreCase(name)) return kit;
        }
        return null;
    }

    private static int kit(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> context, NullArmyController.Kit kit) {
        if (!NullArmyController.givePlayerKit(kit)) return reply(context, "You must be in a world to equip a kit.");
        return reply(context, "Applied your " + kit.commandName() + " kit locally. A multiplayer server may overwrite client-only equipment.");
    }
    private static int formation(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> context, NullArmyController.Formation formation) {
        NullArmyController.setFormation(formation);
        return reply(context, "Formation changed to " + formation.name().toLowerCase() + ".");
    }
    private static int reply(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> context, String message) {
        context.getSource().sendFeedback(Component.literal("[Null Army] " + message));
        return 1;
    }
}
