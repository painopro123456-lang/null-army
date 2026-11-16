# Null Army — PvP Bot

A server-side Fabric mod for Minecraft 26.2. This project replaces the former client-only 17-unit army with one server-authoritative PvP practice bot.

## Requirements
- Minecraft Java 26.2
- Fabric Loader 0.19.3+
- Fabric API 0.161.0+26.2
- Java 25
- Install on the server. For LAN/singleplayer, the integrated server loads it automatically.

## Commands
- `/pvpbot spawn <username>` — spawn the single bot using the supplied display name.
- `/pvpbot kill` — remove the bot.
- `/pvpbot target <player>` — make the bot attack a specific online player.
- `/pvpbot target nearest` — target the nearest eligible real player.
- `/pvpbot stop` — stop the bot's combat AI without removing it.

Only one bot can exist at once. The bot is a server-side fake player entity, so nearby players receive its entity spawn and movement through normal server tracking.

## Identity note
The supplied username is a display/profile name for this fake entity. It does not log in as, authenticate as, or grant access to the real Minecraft account with that username. The initial implementation uses an offline UUID and does not fetch or copy account skins.

## Build
Run `gradle build` with Java 25. The mod JAR is written to `build/libs/`.

## Status
This is the clean replacement baseline. The fake-player lifecycle and command layer are implemented first; combat movement and attack behavior are deliberately kept conservative until verified against a live 26.2 server. Do not treat an untested build as a finished combat bot.
