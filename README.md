# NoChatOrderFix  
**Author:** PinkyCore  
**Compatibility:** Paper / Spigot / Purpur 1.19 – 1.20+  
**Dependency:** [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)  
**Version:** 1.1  

---

## Description

Starting with **Minecraft 1.19**, Mojang introduced the **signed chat message system** and message ordering verification.  
While intended to prevent tampering, this system causes issues on hybrid servers or those running **ViaVersion**, **Floodgate**, or **Geyser**, often disconnecting players with:

`multiplayer.disconnect.out_of_order:chat`

`sent out-of-order chat: ...`

**NoChatOrderFix** prevents these disconnections by intercepting client chat packets **before** the server validates their digital signatures.  
It then safely **resends the chat or command** directly through the Bukkit API, ensuring all players can communicate normally — regardless of whether they’re premium or cracked.

---

## Features

✅ **Fixes out-of-order chat disconnections**  
✅ **Works with any chat formatter plugin**, including:  
- LPC (LuckPerms Chat)  
- EssentialsChat  
- CMI Chat  
- VentureChat  
- ZelChat  
- Or any other formatter  

✅ **Supports hybrid servers** (Premium / Non-premium / Floodgate / Geyser)  
✅ **Invisible to players** — chat behaves normally  
✅ **No configuration required**  
✅ **Lightweight and safe** — does not alter formatting or interfere with other plugins  

---

## How it Works

- Intercepts incoming chat packets using **ProtocolLib**  
- Supports the following packet types automatically:
  - `CHAT` (1.19–1.19.2)
  - `CHAT_COMMAND`
  - `CHAT_COMMAND_SIGNED`
- Cancels the original signed message packet  
- Resends the text directly through:
  `java`
  `player.chat(message);`
  `player.performCommand(command);`

  This bypasses the signed chat validation while preserving all chat formatting handled by other plugins.

---

### Installation

- Download and install ProtocolLib
- Place NoChatOrderFix.jar inside your /plugins directory
- Restart the server
- Done — no more “out_of_order” disconnects!

---

### Example

Before:

``[WARN]: Player sent out-of-order chat: 'asd': 1763468038 > 1763466853``

``[INFO]: Multiplayer disconnect: out_of_order:chat``


After:

``[NoChatOrderFix] Enabled - forwarding chat directly to the server.``


**Players can chat and run commands normally — no disconnections, no warnings.**

---

### Recommended For

- Networks using **ViaVersion**, **Geyser**, or **Floodgate**
- Hybrid servers (Premium + Non-premium)
- Paper/Spigot backends with Velocity or BungeeCord proxies

---

### Technical Details

- Language: Java 17
- Framework: Spigot / Paper API + ProtocolLib
- Main Class: dev.pinkycore.nochatorder.NoChatOrderFix
- API Version: 1.19
- License: MIT

---

### Credits

Developed by **PinkyCore**

Thanks to the Minecraft community for reporting chat desync issues introduced in 1.19.

If you use this plugin, consider leaving feedback or sharing it with other network owners!
