package dev.nullarmy.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;

public final class NullArmyController {
    public static final int DEFAULT_SOLDIERS = 15;
    public static final int DEFAULT_COMMANDERS = 2;
    private static final int MAX_UNITS = 64;
    private static final List<ArmyUnit> UNITS = new ArrayList<>();
    private static ArmyOrder order = ArmyOrder.HOLD;
    private static Formation formation = Formation.WEDGE;
    private static Kit selectedKit = Kit.SWORD;
    private static Kit armyKit = Kit.NONE;
    private static final Set<String> WHITELIST = new HashSet<>();
    private static String target = "";
    private static double destinationX, destinationY, destinationZ;
    private static boolean hasDestination;
    private static ClientLevel activeLevel;
    private static int nextEntityId = -20000;
    private static int tickCounter;

    private NullArmyController() {}

    public static void spawnDefault() { spawn(DEFAULT_SOLDIERS, DEFAULT_COMMANDERS); }

    public static void spawn(int soldiers, int commanders) {
        Minecraft client = Minecraft.getInstance();
        dismissVisuals();
        int safeSoldiers = Math.max(0, Math.min(soldiers, MAX_UNITS));
        int safeCommanders = Math.max(0, Math.min(commanders, MAX_UNITS - safeSoldiers));
        for (int i = 0; i < safeCommanders; i++) UNITS.add(new ArmyUnit(UnitRole.COMMANDER, i, armyKit));
        for (int i = 0; i < safeSoldiers; i++) UNITS.add(new ArmyUnit(UnitRole.SOLDIER, i, armyKit));
        order = ArmyOrder.FOLLOW;
        target = "";
        hasDestination = false;
        tickCounter = 0;
        if (client.level != null && client.player != null) {
            activeLevel = client.level;
            for (int i = 0; i < UNITS.size(); i++) {
                ArmyUnit unit = UNITS.get(i);
                double[] offset = formationOffset(i);
                unit.x = client.player.getX() + offset[0];
                unit.y = client.player.getY();
                unit.z = client.player.getZ() + offset[1];
                unit.initialized = true;
                createVisual(client.level, unit, i);
            }
        }
    }

    private static void createVisual(ClientLevel level, ArmyUnit unit, int index) {
        NullSoldierEntity visual = NullSoldierEntityType.TYPE.create(level, EntitySpawnReason.COMMAND);
        if (visual == null) return;
        visual.setNoAi(true);
        visual.setInvulnerable(true);
        visual.setSilent(true);
        visual.setCustomName(Component.literal(unit.role == UnitRole.COMMANDER
            ? "Null Commander " + (unit.number + 1) : "Null Soldier " + (unit.number + 1)));
        visual.setCustomNameVisible(true);
        if (unit.kit != Kit.NONE) equip(visual, unit.kit);
        visual.setPos(unit.x, unit.y, unit.z);
        visual.setId(nextEntityId--);
        level.addEntity(visual);
        unit.visual = visual;
    }

    private static void equip(NullSoldierEntity mob, Kit kit) {
        mob.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
        mob.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS));
        mob.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS));
        if (kit == Kit.ELYTRA_MACE) {
            mob.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.ELYTRA));
            mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.MACE));
            mob.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.FIREWORK_ROCKET, 64));
        } else {
            mob.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
            mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(
                kit == Kit.MACE ? Items.MACE : Items.NETHERITE_SWORD));
            if (kit == Kit.SWORD) mob.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
            else mob.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.WIND_CHARGE, 16));
        }
    }

    private static void dismissVisuals() {
        for (ArmyUnit unit : UNITS) {
            if (unit.visual != null) unit.visual.discard();
        }
        UNITS.clear();
    }

    public static void dismiss() {
        dismissVisuals();
        target = "";
        hasDestination = false;
        order = ArmyOrder.HOLD;
    }
    public static void setTarget(String value) { target = value == null ? "" : value; }

    /** Selects the nearest currently loaded, alive, non-owner, non-whitelisted player. */
    public static String selectNearestTarget() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null) return "";
        Player nearest = client.level.players().stream()
            .filter(player -> player != client.player && player.isAlive())
            .filter(player -> !isWhitelisted(player.getName().getString()))
            .min(java.util.Comparator.comparingDouble(client.player::distanceToSqr))
            .orElse(null);
        if (nearest == null) return "";
        target = nearest.getName().getString();
        return target;
    }

    public static boolean issueAttackOrder() {
        Minecraft client = Minecraft.getInstance();
        if (target.isBlank() || isWhitelisted(target) || client.level == null || findTarget(client) == null) return false;
        order = ArmyOrder.ATTACK;
        return true;
    }

    public static boolean isWhitelisted(String name) {
        Minecraft client = Minecraft.getInstance();
        if (name == null) return false;
        if (client.player != null && client.player.getName().getString().equalsIgnoreCase(name)) return true;
        return WHITELIST.stream().anyMatch(entry -> entry.equalsIgnoreCase(name));
    }

    public static void addWhitelist(String name) {
        if (name != null && !name.isBlank()) WHITELIST.add(name);
        if (!target.isBlank() && isWhitelisted(target)) {
            target = "";
            if (order == ArmyOrder.ATTACK) order = ArmyOrder.STOP;
        }
    }

    public static boolean removeWhitelist(String name) {
        Minecraft client = Minecraft.getInstance();
        if (name == null || (client.player != null && client.player.getName().getString().equalsIgnoreCase(name))) return false;
        return WHITELIST.removeIf(entry -> entry.equalsIgnoreCase(name));
    }

    public static String whitelistStatus() {
        Set<String> names = new HashSet<>(WHITELIST);
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) names.add(client.player.getName().getString() + " (owner)");
        return names.isEmpty() ? "owner only" : String.join(", ", names);
    }

    public static boolean givePlayerKit(Kit kit) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || kit == null || kit == Kit.NONE) return false;
        selectedKit = kit;
        var player = client.player;
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS));
        player.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS));
        if (kit == Kit.ELYTRA_MACE) {
            player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.ELYTRA));
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.MACE));
            player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.FIREWORK_ROCKET, 64));
        } else {
            player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(kit == Kit.MACE ? Items.MACE : Items.NETHERITE_SWORD));
            player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(kit == Kit.SWORD ? Items.SHIELD : Items.WIND_CHARGE, kit == Kit.SWORD ? 1 : 16));
        }
        return true;
    }

    public static void setArmyKit(Kit kit) {
        armyKit = kit;
        for (ArmyUnit unit : UNITS) {
            unit.kit = kit;
            if (unit.visual != null) equip(unit.visual, kit);
        }
    }
    public static void goTo(double x, double y, double z) {
        destinationX = x; destinationY = y; destinationZ = z; hasDestination = true; order = ArmyOrder.MOVE;
    }
    public static void setOrder(ArmyOrder next) {
        order = next;
        if (next == ArmyOrder.FOLLOW) hasDestination = false;
    }
    public static void setFormation(Formation next) { formation = next; }
    public static void setKit(Kit next) { selectedKit = next; }
    public static Kit selectedKit() { return selectedKit; }
    public static String armyKitName() { return armyKit.commandName(); }
    public static int soldierCount() { return (int) UNITS.stream().filter(u -> u.role == UnitRole.SOLDIER).count(); }
    public static int commanderCount() { return (int) UNITS.stream().filter(u -> u.role == UnitRole.COMMANDER).count(); }
    public static int totalCount() { return UNITS.size(); }

    public static String orderLabel() {
        return "ORDER: " + order.name().toLowerCase() + "   FORMATION: " + formation.name().toLowerCase()
            + (target.isBlank() ? "" : "   TARGET: " + target);
    }
    public static String status() {
        return "Army: " + commanderCount() + " commanders, " + soldierCount()
            + " soldiers | Order: " + order.name().toLowerCase()
            + " | Formation: " + formation.name().toLowerCase()
            + " | Army kit: " + armyKit.commandName() + " | Player kit: " + selectedKit.commandName()
            + " | Target: " + (target.isBlank() ? "none" : target);
    }
    public static List<ArmyUnit> unitsView() { return Collections.unmodifiableList(UNITS); }

    public static void tick(Minecraft client) {
        if (client.level == null || client.player == null) return;
        if (activeLevel != client.level) {
            UNITS.clear();
            activeLevel = client.level;
            target = "";
            hasDestination = false;
            order = ArmyOrder.HOLD;
            return;
        }
        if (UNITS.isEmpty()) return;
        tickCounter++;
        LivingEntity targetEntity = order == ArmyOrder.ATTACK ? findTarget(client) : null;
        if (order == ArmyOrder.ATTACK && targetEntity == null) { order = ArmyOrder.STOP; return; }
        double px = client.player.getX(), py = client.player.getY(), pz = client.player.getZ();
        for (int i = 0; i < UNITS.size(); i++) {
            ArmyUnit unit = UNITS.get(i);
            if (unit.visual == null || unit.visual.isRemoved()) continue;
            double[] offset = formationOffset(i);
            double tx = px + offset[0], ty = py, tz = pz + offset[1];
            if (order == ArmyOrder.MOVE && hasDestination) {
                tx = destinationX + offset[0]; ty = destinationY; tz = destinationZ + offset[1];
            } else if (order == ArmyOrder.ATTACK) {
                if (targetEntity == null) continue;
                tx = targetEntity.getX();
                ty = targetEntity.getY();
                tz = targetEntity.getZ();
                double distanceToTarget = unit.visual.distanceTo(targetEntity);
                if (distanceToTarget < 2.6) {
                    face(unit.visual, targetEntity.getX(), targetEntity.getZ());
                    if (tickCounter % 12 == 0) unit.visual.swing(InteractionHand.MAIN_HAND);
                    continue;
                }
            } else if (order == ArmyOrder.HOLD || order == ArmyOrder.DEFEND || order == ArmyOrder.STOP) {
                continue;
            }

            double dx = tx - unit.visual.getX();
            double dz = tz - unit.visual.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);
            if (distance > 0.15) {
                double step = Math.min(0.13, distance);
                double mx = dx / distance * step;
                double mz = dz / distance * step;
                face(unit.visual, tx, tz);
                unit.visual.move(MoverType.SELF, new Vec3(mx, 0.0, mz));
            }
            unit.x = unit.visual.getX();
            unit.y = unit.visual.getY();
            unit.z = unit.visual.getZ();
        }
    }

    private static LivingEntity findTarget(Minecraft client) {
        if (target.isBlank() || isWhitelisted(target) || client.level == null || client.player == null) return null;
        // Search every player currently loaded in this client world, not just a 128-block bubble around the owner.
        return client.level.players().stream()
            .filter(player -> player != client.player && player.isAlive())
            .filter(player -> player.getName().getString().equalsIgnoreCase(target))
            .filter(player -> !isWhitelisted(player.getName().getString()))
            .findFirst().orElse(null);
    }

    private static boolean isArmyVisual(Entity entity) {
        for (ArmyUnit unit : UNITS) if (unit.visual == entity) return true;
        return false;
    }

    private static void face(NullSoldierEntity mob, double x, double z) {
        double dx = x - mob.getX();
        double dz = z - mob.getZ();
        if (Math.abs(dx) + Math.abs(dz) < 0.001) return;
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        mob.setYRot(yaw);
        mob.setYBodyRot(yaw);
        mob.setYHeadRot(yaw);
    }

    private static double[] formationOffset(int index) {
        int row = index / 3, column = index % 3;
        return switch (formation) {
            case LINE -> new double[] {(column - 1) * 2.0, row * 2.0};
            case COLUMN -> new double[] {((index % 2) * 1.5) - 0.75, row * 2.0};
            case WEDGE -> new double[] {(column - 1) * (row + 1) * 0.9, row * 1.8};
        };
    }

    public enum UnitRole { SOLDIER, COMMANDER }
    public enum ArmyOrder { FOLLOW, MOVE, DEFEND, ATTACK, STOP, HOLD }
    public enum Formation { LINE, COLUMN, WEDGE }
    public enum Kit {
        NONE("none"), SWORD("sword"), MACE("mace"), ELYTRA_MACE("elytra_mace"), ARCHER("archer"), ASSAULT("assault");
        private final String commandName;
        Kit(String commandName) { this.commandName = commandName; }
        public String commandName() { return commandName; }
    }

    public static final class ArmyUnit {
        private final UnitRole role;
        private final int number;
        private Kit kit;
        private double x, y, z;
        private boolean initialized;
        private NullSoldierEntity visual;
        private ArmyUnit(UnitRole role, int number, Kit kit) { this.role = role; this.number = number; this.kit = kit; }
        public UnitRole role() { return role; }
        public int number() { return number; }
        public Kit kit() { return kit; }
        public double x() { return x; }
        public double y() { return y; }
        public double z() { return z; }
    }
}
