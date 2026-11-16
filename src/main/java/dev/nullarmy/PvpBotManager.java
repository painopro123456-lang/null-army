package dev.nullarmy;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class PvpBotManager {
    public static final PvpBotManager INSTANCE = new PvpBotManager();

    private FakePlayer bot;
    private String targetName;
    private boolean combatEnabled = true;
    private int attackCooldown;

    private PvpBotManager() {}

    public int spawn(CommandSourceStack source, String username) {
        if (bot != null && bot.isAlive()) {
            source.sendFailure(Component.literal("A PvP bot already exists. Use /pvpbot kill first."));
            return 0;
        }
        if (!username.matches("[A-Za-z0-9_]{1,16}")) {
            source.sendFailure(Component.literal("Username must be 1–16 Minecraft username characters."));
            return 0;
        }

        ServerPlayer owner = source.getPlayer();
        ServerLevel level = source.getLevel();
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
        GameProfile profile = new GameProfile(uuid, username);
        FakePlayer created = FakePlayer.get(level, profile);
        created.setPos(owner.getX() + 1.5, owner.getY(), owner.getZ());
        created.setYRot(owner.getYRot());
        created.setXRot(owner.getXRot());

        // Register as a world player so the server's normal entity tracking can publish it.
        level.addNewPlayer(created);
        bot = created;
        targetName = null;
        combatEnabled = true;
        attackCooldown = 0;

        source.sendSuccess(() -> Component.literal("Spawned PvP bot '" + username + "'. Nearby players can see it."), true);
        return 1;
    }

    public int kill(CommandSourceStack source) {
        if (bot == null) {
            source.sendFailure(Component.literal("There is no PvP bot to remove."));
            return 0;
        }
        removeBot();
        source.sendSuccess(() -> Component.literal("PvP bot removed."), true);
        return 1;
    }

    public int stop(CommandSourceStack source) {
        if (bot == null) {
            source.sendFailure(Component.literal("There is no PvP bot."));
            return 0;
        }
        combatEnabled = false;
        bot.setDeltaMovement(Vec3.ZERO);
        source.sendSuccess(() -> Component.literal("PvP bot AI stopped."), true);
        return 1;
    }

    public int target(CommandSourceStack source, ServerPlayer target) {
        if (bot == null) {
            source.sendFailure(Component.literal("Spawn a bot first."));
            return 0;
        }
        if (target == bot) {
            source.sendFailure(Component.literal("The bot cannot target itself."));
            return 0;
        }
        targetName = target.getGameProfile().getName();
        combatEnabled = true;
        source.sendSuccess(() -> Component.literal("PvP bot target set to " + targetName + "."), true);
        return 1;
    }

    public int targetNearest(CommandSourceStack source) {
        if (bot == null) {
            source.sendFailure(Component.literal("Spawn a bot first."));
            return 0;
        }
        targetName = null;
        combatEnabled = true;
        source.sendSuccess(() -> Component.literal("PvP bot will attack the nearest real player."), true);
        return 1;
    }

    public void tick(MinecraftServer server) {
        if (bot == null) return;
        if (!bot.isAlive() || bot.isRemoved()) {
            removeBot();
            return;
        }
        if (!combatEnabled) return;

        ServerPlayer target = findTarget(server);
        if (target == null || target.level() != bot.level()) {
            bot.setDeltaMovement(Vec3.ZERO);
            return;
        }

        Vec3 delta = target.position().subtract(bot.position());
        double distance = delta.length();
        if (distance > 2.8) {
            Vec3 direction = new Vec3(delta.x, 0, delta.z);
            if (direction.lengthSqr() > 0.0001) {
                Vec3 forward = direction.normalize();
                // Small alternating strafe makes the bot less stationary while closing distance.
                double strafeSign = (bot.tickCount / 12 % 2 == 0) ? 1.0 : -1.0;
                Vec3 strafe = new Vec3(-forward.z, 0, forward.x).scale(0.22 * strafeSign);
                bot.setDeltaMovement(forward.scale(0.23).add(strafe).add(0, bot.getDeltaMovement().y, 0));
                bot.setYRot((float) (Math.toDegrees(Math.atan2(-forward.x, forward.z))));
            } else {
                bot.setDeltaMovement(Vec3.ZERO);
            }
        } else {
            bot.setDeltaMovement(new Vec3(0, bot.getDeltaMovement().y, 0));
            if (attackCooldown-- <= 0) {
                bot.attack(target);
                attackCooldown = 12;
            }
        }
    }

    private ServerPlayer findTarget(MinecraftServer server) {
        if (targetName != null) {
            ServerPlayer selected = server.getPlayerList().getPlayerByName(targetName);
            return selected == null || selected == bot || !selected.isAlive() ? null : selected;
        }
        ServerPlayer nearest = null;
        double bestDistance = Double.MAX_VALUE;
        for (ServerPlayer candidate : server.getPlayerList().getPlayers()) {
            if (candidate == bot || !candidate.isAlive() || candidate.level() != bot.level()) continue;
            double distance = candidate.distanceToSqr(bot);
            if (distance < bestDistance) {
                bestDistance = distance;
                nearest = candidate;
            }
        }
        return nearest;
    }

    private void removeBot() {
        if (bot != null) {
            ServerLevel level = (ServerLevel) bot.level();
            level.removePlayerImmediately(bot, RemovalReason.DISCARDED);
            bot = null;
        }
        targetName = null;
        combatEnabled = false;
    }
}
