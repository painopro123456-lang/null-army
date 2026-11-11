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
                            if (soldiers + commanders > 64) {
                                return reply(context, "Maximum local army size is 64 units.");
                            }
                            NullArmyController.spawn(soldiers, commanders);
                            return reply(context, "Created " + soldiers + " soldiers and " + commanders + " commanders.");
                        }))))
            .then(ClientCommands.literal("kit")
                .executes(context -> reply(context, "Selected kit: " + NullArmyController.selectedKit().name().toLowerCase()
                    + ". Choose: standard, assault, scout, heavy, archer, medic. Kit applies to the next spawn."))
                .then(ClientCommands.literal("standard").executes(context -> kit(context, NullArmyController.Kit.STANDARD)))
                .then(ClientCommands.literal("assault").executes(context -> kit(context, NullArmyController.Kit.ASSAULT)))
                .then(ClientCommands.literal("scout").executes(context -> kit(context, NullArmyController.Kit.SCOUT)))
                .then(ClientCommands.literal("heavy").executes(context -> kit(context, NullArmyController.Kit.HEAVY)))
                .then(ClientCommands.literal("archer").executes(context -> kit(context, NullArmyController.Kit.ARCHER)))
                .then(ClientCommands.literal("medic").executes(context -> kit(context, NullArmyController.Kit.MEDIC))))
            .then(ClientCommands.literal("target")
                .then(ClientCommands.argument("who", StringArgumentType.word())
                    .executes(context -> {
                        String who = StringArgumentType.getString(context, "who");
                        if (who.equalsIgnoreCase("clear")) {
                            NullArmyController.setTarget("");
                            return reply(context, "Target cleared.");
                        }
                        NullArmyController.setTarget(who);
                        return reply(context, "Attack target set to: " + who + ".");
                    })))
            .then(ClientCommands.literal("goto")
                .then(ClientCommands.argument("x", DoubleArgumentType.doubleArg())
                    .then(ClientCommands.argument("y", DoubleArgumentType.doubleArg())
                        .then(ClientCommands.argument("z", DoubleArgumentType.doubleArg())
                            .executes(context -> {
                                double x = DoubleArgumentType.getDouble(context, "x");
                                double y = DoubleArgumentType.getDouble(context, "y");
                                double z = DoubleArgumentType.getDouble(context, "z");
                                NullArmyController.goTo(x, y, z);
                                return reply(context, "Army movement order set to " + x + " " + y + " " + z + ".");
                            })))))
            .then(ClientCommands.literal("follow")
                .executes(context -> {
                    NullArmyController.setOrder(NullArmyController.ArmyOrder.FOLLOW);
                    return reply(context, "Army ordered to follow you.");
                }))
            .then(ClientCommands.literal("defend")
                .executes(context -> {
                    NullArmyController.setOrder(NullArmyController.ArmyOrder.DEFEND);
                    return reply(context, "Army ordered to defend its position.");
                }))
            .then(ClientCommands.literal("attack")
                .executes(context -> {
                    NullArmyController.setOrder(NullArmyController.ArmyOrder.ATTACK);
                    return reply(context, "Army attack order issued against the selected target.");
                }))
            .then(ClientCommands.literal("stop")
                .executes(context -> {
                    NullArmyController.setOrder(NullArmyController.ArmyOrder.STOP);
                    return reply(context, "Army halted.");
                }))
            .then(ClientCommands.literal("formation")
                .then(ClientCommands.literal("line").executes(context -> formation(context, NullArmyController.Formation.LINE)))
                .then(ClientCommands.literal("column").executes(context -> formation(context, NullArmyController.Formation.COLUMN)))
                .then(ClientCommands.literal("wedge").executes(context -> formation(context, NullArmyController.Formation.WEDGE))))
            .then(ClientCommands.literal("status")
                .executes(context -> reply(context, NullArmyController.status())))
            .then(ClientCommands.literal("help")
                .executes(context -> reply(context,
                    "spawn | spawn <soldiers> <commanders> | target <player|nearest|clear> | goto <x> <y> <z> | follow | defend | attack | stop | kit <standard|assault|scout|heavy|archer|medic> | formation <line|column|wedge> | status | dismiss")))
            .then(ClientCommands.literal("dismiss")
                .executes(context -> {
                    NullArmyController.dismiss();
                    return reply(context, "All locally tracked Null units dismissed.");
                }))
        );
    }

    private static int kit(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> context,
                           NullArmyController.Kit kit) {
        NullArmyController.setKit(kit);
        return reply(context, "Kit set to " + kit.name().toLowerCase() + ". Use /null spawn to deploy the army with this kit.");
    }

    private static int formation(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> context,
                                 NullArmyController.Formation formation) {
        NullArmyController.setFormation(formation);
        return reply(context, "Formation changed to " + formation.name().toLowerCase() + ".");
    }

    private static int reply(com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> context, String message) {
        context.getSource().sendFeedback(Component.literal("[Null Army] " + message));
        return 1;
    }
}
