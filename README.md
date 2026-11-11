# Null Army

Client-only Fabric mod for Minecraft 26.2.

## Default army
Running `/null spawn` creates **15 Null soldiers and 2 Null commanders** (17 units total).

## Commands
- `/null spawn` — spawn the default 15 + 2 army.
- `/null spawn <soldiers> <commanders>` — choose a custom composition.
- `/null kit` — show the selected kit.
- `/null kit <standard|assault|scout|heavy|archer|medic>` — choose the kit assigned to the next army you spawn. `/null spawn` remains the quick default deploy command.
- `/null target <player|nearest|clear>` — set or clear the army's target.
- `/null goto <x> <y> <z>` — order the army to move to a position.
- `/null follow` — follow the local player.
- `/null defend` — hold a defensive posture around the current position.
- `/null attack` — enter attack-order state against the selected target.
- `/null stop` — halt movement and hold position.
- `/null formation <line|column|wedge>` — choose the formation layout.
- `/null status` — show army composition and current orders.
- `/null dismiss` — dismiss all locally tracked units.

The in-game HUD shows the current unit count, order, formation, selected kit, and a 17-unit roster marker display. Kit selection is stored on each locally simulated unit, ready for the equipment/model rendering layer. This initial milestone does not yet render full 3D soldier models, visibly equip armor/weapons, or implement combat/pathfinding.

## Important client-only limitation
This project is deliberately client-only. Its commands and army simulation run on the local client. In multiplayer, other players and the server do not receive these units or orders. The client cannot make them deal authoritative damage, change blocks, or control server-owned entities. This first foundation implements the command/state/tick simulation; visible custom Null soldier models and animations are a separate rendering layer.

## Build
Requires JDK 25 and Gradle 9.5.1. Run:

```sh
gradle build
```

The mod JAR is written to `build/libs/`.
