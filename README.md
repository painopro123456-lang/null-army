# Null Army

Client-only Fabric mod for Minecraft 26.2.

## Default army
Running `/null spawn` creates **15 Null soldiers and 2 Null commanders** (17 units total). The default kit is **Sword**.

## Selectable Minecraft kits
There is one kit command with three equipment choices:
- **Sword** — sword, shield, and armor.
- **Mace** — mace and wind charges.
- **Elytra Mace** — elytra, firework rockets, and a mace for aerial smash attacks.

Choose a kit before spawning. The selected choice is assigned to every unit in the next army.

## Commands
- `/null spawn` — spawn the default 15 + 2 army.
- `/null spawn <soldiers> <commanders>` — choose a custom composition.
- `/null kit` — show the current kit and available choices.
- `/null kit sword` — use the sword loadout.
- `/null kit mace` — use the mace loadout.
- `/null kit elytra_mace` — use the elytra + mace loadout.
- `/null target <player|nearest|clear>` — set or clear the army's target.
- `/null goto <x> <y> <z>` — order the army to move to a position.
- `/null follow` — follow the local player.
- `/null defend` — hold a defensive posture around the current position.
- `/null attack` — enter attack-order state against the selected target.
- `/null stop` — halt movement and hold position.
- `/null formation <line|column|wedge>` — choose the formation layout.
- `/null status` — show army composition and current orders.
- `/null dismiss` — dismiss all locally tracked units.

The current foundation stores the selected kit on each locally simulated unit and shows it in the HUD. Full 3D Null soldiers, visible armor/weapons, actual equipment behavior, combat, and pathfinding are not implemented yet.

## Important client-only limitation
This project is deliberately client-only. Its commands and army simulation run on the local client. In multiplayer, other players and the server do not receive these units or orders. The client cannot make them deal authoritative damage, change blocks, or control server-owned entities.

## Build
Requires JDK 25. Run:

```sh
gradle build
```

The mod JAR is written to `build/libs/`.
