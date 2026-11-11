# Null Army

Client-only Fabric mod for Minecraft 26.2.

## Default army
Running `/null spawn` creates **15 Null soldiers and 2 Null commanders** (17 units total). They appear as locally rendered custom Null soldier models. They start unequipped unless you have selected an army kit. The default kit is **Sword**.

## Selectable Minecraft kits
Choose one kit before spawning. The selected choice is assigned to every unit in the next army:
- **Sword** — netherite sword, shield, and netherite armor.
- **Mace** — mace, wind charges, and netherite armor.
- **Elytra Mace** — elytra, firework rockets, mace, and protective armor pieces.

## Commands
- `/null spawn` — spawn the default 15 + 2 army.
- `/null spawn <soldiers> <commanders>` — choose a custom composition.
- `/null kit` — show the current kit and available choices.
- `/null kit sword` — use the sword loadout.
- `/null kit mace` — use the mace loadout.
- `/null kit elytra_mace` — use the elytra + mace loadout.
- `/null target <player>` — select one specific loaded player. `/null target nearest` explicitly selects the nearest loaded, alive player who is not whitelisted. `/null target clear` clears the selection.
- `/null goto <x> <y> <z>` — order the army to move to a position.
- `/null follow` — follow the local player.
- `/null defend` — hold position.
- `/null attack` — approach the selected target and swing when close.
- `/null stop` — halt movement and hold position.
- `/null formation <line|column|wedge>` — choose the formation layout.
- `/null status` — show army composition and current orders.
- `/null dismiss` — remove all locally spawned army units.

## Important client-only limitation
All units are client-side visual entities. They are visible only to the local player and are not spawned on the server. Movement uses local entity movement with collision handling, but it is not full Minecraft mob pathfinding. Attack orders make units approach and swing at the selected loaded player; they **do not deal damage**. Real damage and shared visibility require a server-side implementation, which is intentionally deferred. Elytra and rockets are equipped visually; autonomous flight and mace-smash combat are not implemented. Multiplayer players and servers do not receive these units or orders, and the mod cannot change blocks or control server-owned entities.

## Build
Requires JDK 25. Run:

```sh
gradle build
```

The mod JAR is written to `build/libs/`.
