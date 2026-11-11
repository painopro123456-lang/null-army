# Null Army

Client-only Fabric mod for Minecraft 26.2.

## Default army
Running `/null spawn` creates **15 Null soldiers and 2 Null commanders** (17 units total). The default kit is Sword.

## Minecraft kits
- **Elytra** — elytra and firework rockets for aerial movement.
- **Mace** — mace and wind charges for smash attacks.
- **Sword** — sword, shield, and armor.
- **Archer** — bow, arrows, and a sword as backup.
- **Assault** — full combat armor, sword, bow, and shield.

## Commands
- `/null spawn` — spawn the default 15 + 2 army.
- `/null spawn <soldiers> <commanders>` — choose a custom composition.
- `/null kit` — show the selected kit.
- `/null kit <elytra|mace|sword|archer|assault>` — choose the kit assigned to the next army you spawn.
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
