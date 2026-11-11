package dev.nullarmy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class NullArmyHud {
    private NullArmyHud() {}

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || NullArmyController.totalCount() == 0) return;

        int left = 10;
        int top = 10;
        int right = 190;
        int bottom = 99;

        graphics.fill(left, top, right, bottom, 0xB90A0D13);
        graphics.fill(left, top, left + 2, bottom, 0xFF9B111E);
        graphics.fill(left + 2, top, right, top + 1, 0xFF343B48);

        graphics.text(minecraft.font, "NULL ARMY", 18, 16, 0xFFF2F4F8, true);
        graphics.text(minecraft.font,
            NullArmyController.commanderCount() + " COMMANDERS  /  "
                + NullArmyController.soldierCount() + " SOLDIERS",
            18, 29, 0xFFB7C0CE, true);
        graphics.text(minecraft.font, NullArmyController.orderLabel(),
            18, 43, 0xFFFFFFFF, true);
        graphics.text(minecraft.font, "PLAYER KIT: " + NullArmyController.selectedKit().commandName(),
            18, 56, 0xFF91C8FF, true);
        graphics.text(minecraft.font, "ARMY KIT: " + NullArmyController.armyKitName(),
            18, 68, 0xFFFFC857, true);

        int index = 0;
        for (NullArmyController.ArmyUnit unit : NullArmyController.unitsView()) {
            int x = 18 + (index % 14) * 11;
            int y = 83 + (index / 14) * 7;
            int color = unit.role() == NullArmyController.UnitRole.COMMANDER
                ? 0xFFFFC857 : 0xFFB9C2D0;
            graphics.fill(x, y, x + 6, y + 5, color);
            index++;
        }
    }
}
