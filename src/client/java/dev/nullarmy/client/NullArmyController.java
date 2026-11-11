package dev.nullarmy.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;

public final class NullArmyController {
    public static final int DEFAULT_SOLDIERS = 15;
    public static final int DEFAULT_COMMANDERS = 2;
    private static final int MAX_UNITS = 64;

    private static final List<ArmyUnit> UNITS = new ArrayList<>();
    private static ArmyOrder order = ArmyOrder.HOLD;
    private static Formation formation = Formation.WEDGE;
    private static Kit selectedKit = Kit.STANDARD;
    private static String target = "";
    private static double destinationX;
    private static double destinationY;
    private static double destinationZ;
    private static boolean hasDestination;

    private NullArmyController() {}

    public static void spawnDefault() {
        spawn(DEFAULT_SOLDIERS, DEFAULT_COMMANDERS);
    }

    public static void spawn(int soldiers, int commanders) {
        int safeSoldiers = Math.max(0, Math.min(soldiers, MAX_UNITS));
        int safeCommanders = Math.max(0, Math.min(commanders, MAX_UNITS - safeSoldiers));
        UNITS.clear();
        for (int i = 0; i < safeCommanders; i++) {
            UNITS.add(new ArmyUnit(UnitRole.COMMANDER, i, selectedKit));
        }
        for (int i = 0; i < safeSoldiers; i++) {
            UNITS.add(new ArmyUnit(UnitRole.SOLDIER, i, selectedKit));
        }
        order = ArmyOrder.FOLLOW;
        target = "";
        hasDestination = false;
    }

    public static void dismiss() {
        UNITS.clear();
        target = "";
        hasDestination = false;
        order = ArmyOrder.HOLD;
    }

    public static void setTarget(String value) {
        target = value == null ? "" : value;
        order = target.isBlank() ? ArmyOrder.HOLD : ArmyOrder.ATTACK;
    }

    public static void goTo(double x, double y, double z) {
        destinationX = x;
        destinationY = y;
        destinationZ = z;
        hasDestination = true;
        order = ArmyOrder.MOVE;
    }

    public static void setOrder(ArmyOrder next) {
        order = next;
        if (next == ArmyOrder.FOLLOW) hasDestination = false;
    }

    public static void setFormation(Formation next) {
        formation = next;
    }

    public static void setKit(Kit next) {
        selectedKit = next;
    }

    public static Kit selectedKit() {
        return selectedKit;
    }

    public static int soldierCount() {
        return (int) UNITS.stream().filter(unit -> unit.role == UnitRole.SOLDIER).count();
    }

    public static int commanderCount() {
        return (int) UNITS.stream().filter(unit -> unit.role == UnitRole.COMMANDER).count();
    }

    public static int totalCount() {
        return UNITS.size();
    }

    public static String orderLabel() {
        return "ORDER: " + order.name().toLowerCase()
            + "   FORMATION: " + formation.name().toLowerCase()
            + (target.isBlank() ? "" : "   TARGET: " + target);
    }

    public static String status() {
        return "Army: " + commanderCount() + " commanders, " + soldierCount()
            + " soldiers | Order: " + order.name().toLowerCase()
            + " | Formation: " + formation.name().toLowerCase()
            + " | Kit: " + selectedKit.name().toLowerCase()
            + " | Target: " + (target.isBlank() ? "none" : target);
    }

    public static List<ArmyUnit> unitsView() {
        return Collections.unmodifiableList(UNITS);
    }

    public static void tick(Minecraft client) {
        if (UNITS.isEmpty() || client.player == null) return;

        double px = client.player.getX();
        double py = client.player.getY();
        double pz = client.player.getZ();

        for (int i = 0; i < UNITS.size(); i++) {
            ArmyUnit unit = UNITS.get(i);
            double[] offset = formationOffset(i);
            double tx = px + offset[0];
            double ty = py;
            double tz = pz + offset[1];

            if (order == ArmyOrder.MOVE && hasDestination) {
                tx = destinationX + offset[0];
                ty = destinationY;
                tz = destinationZ + offset[1];
            } else if (order == ArmyOrder.HOLD || order == ArmyOrder.DEFEND
                    || order == ArmyOrder.STOP || order == ArmyOrder.ATTACK) {
                if (!unit.initialized) {
                    unit.x = px + offset[0];
                    unit.y = py;
                    unit.z = pz + offset[1];
                    unit.initialized = true;
                }
                continue;
            }

            if (!unit.initialized) {
                unit.x = tx;
                unit.y = ty;
                unit.z = tz;
                unit.initialized = true;
            } else {
                double dx = tx - unit.x;
                double dy = ty - unit.y;
                double dz = tz - unit.z;
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double step = Math.min(0.12, distance);
                if (distance > 0.01) {
                    unit.x += dx / distance * step;
                    unit.y += dy / distance * step;
                    unit.z += dz / distance * step;
                }
            }
        }
    }

    private static double[] formationOffset(int index) {
        int row = index / 3;
        int column = index % 3;
        return switch (formation) {
            case LINE -> new double[] {(column - 1) * 2.0, row * 2.0};
            case COLUMN -> new double[] {((index % 2) * 1.5) - 0.75, row * 2.0};
            case WEDGE -> new double[] {(column - 1) * (row + 1) * 0.9, row * 1.8};
        };
    }

    public enum UnitRole { SOLDIER, COMMANDER }
    public enum ArmyOrder { FOLLOW, MOVE, DEFEND, ATTACK, STOP, HOLD }
    public enum Formation { LINE, COLUMN, WEDGE }
    public enum Kit { STANDARD, ASSAULT, SCOUT, HEAVY, ARCHER, MEDIC }

    public static final class ArmyUnit {
        private final UnitRole role;
        private final int number;
        private final Kit kit;
        private double x, y, z;
        private boolean initialized;

        private ArmyUnit(UnitRole role, int number, Kit kit) {
            this.role = role;
            this.number = number;
            this.kit = kit;
        }

        public UnitRole role() { return role; }
        public int number() { return number; }
        public Kit kit() { return kit; }
        public double x() { return x; }
        public double y() { return y; }
        public double z() { return z; }
    }
}
