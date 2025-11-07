## Copilot instructions for NoChatOrderFix

This repository is a small Minecraft plugin (Java 17, Maven) whose single purpose is to work around
the `multiplayer.disconnect.out_of_order:chat` issue by removing internal signature/timestamp structures
from incoming chat packets using ProtocolLib.

Quick-start (what humans do):
- Build: `mvn package` (Java 17 required). Result: `target/NoChatOrderFix-<version>.jar`.
- Deploy: drop the produced JAR into a Spigot/Paper server `plugins/` directory and ensure `ProtocolLib` is installed on the server.

Key files and where to look:
- `src/main/java/dev/pinkycore/nochatorder/NoChatOrderFix.java` — main plugin class. Contains the ProtocolLib listener and the packet-cleaning logic.
- `src/main/resources/plugin.yml` — plugin metadata (main class, api-version, depend: [ProtocolLib]).
- `pom.xml` — Maven coordinates and provided dependencies (Spigot API & ProtocolLib). The plugin is built for Java 17.

Big-picture architecture and intent:
- Single-class plugin: `NoChatOrderFix` registers a ProtocolLib `PacketAdapter` at HIGHEST priority.
- On incoming chat packets it iterates `event.getPacket().getStructures()` and writes `null` via `writeSafely` to remove signatures/timestamps.
- The code intentionally avoids modifying chat text/formatting — only internal structures are cleared.

Important patterns an AI agent should follow when editing:
- When changing packet handling, prefer modifying `resolveChatPackets()` to add/remove targeted `PacketType.Play.Client.*` entries and keep the `isSupported()` checks.
- Preserve the try/catch in `onPacketReceiving` — plugin must never throw unhandled exceptions during packet processing.
- Respect `plugin.yml` fields (do not change `main:` without updating the class path); `depend: [ProtocolLib]` ensures load order on the server.

Developer workflows and debugging tips:
- Build locally with: `mvn -DskipTests package` if you want faster builds.
- To test, copy the built JAR into a local Spigot/Paper server `plugins/` folder alongside ProtocolLib and start the server.
- Logging: the plugin uses `getLogger()` (info/warning/level.SEVERE). Add more fine-grained logs in `onEnable` and `onPacketReceiving` if you need to debug packet contents.

Project-specific caveats and gotchas:
- The plugin relies on ProtocolLib packet types being present and `type.isSupported()` — on some Minecraft versions packet names change; keep `resolveChatPackets()` defensive.
- `spigot-api` and `ProtocolLib` are declared with `<scope>provided</scope>` in `pom.xml`. That means they are not bundled into the JAR and must be present on the server.
- `api-version: 1.19` in `plugin.yml` indicates target server compatibility. When backporting/upgrading, re-check supported packet types and ProtocolLib compatibility.

Where to edit for common tasks (examples):
- To change which packets are cleaned: edit `resolveChatPackets()` in `NoChatOrderFix.java`.
- To tweak cleaning behavior (e.g., selectively keep some structures): edit the loop in `onPacketReceiving` — be careful to retain the `writeSafely` pattern.

Integration points:
- ProtocolLib: `ProtocolLibrary.getProtocolManager()` and `PacketAdapter` (see imports in `NoChatOrderFix.java`).
- Spigot/Paper server: plugin lifecycle methods `onEnable()` are used; runtime behavior depends on server and ProtocolLib versions.

If you add changes, verify:
- Build passes locally with Maven (Java 17).
- The plugin loads in a test server and does not throw exceptions related to packet handling.

If anything here seems incomplete or you want me to expand a section (examples, tests, CI automation), tell me which area to expand and I'll update this file.
