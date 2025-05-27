PlayerMount

is an engaging Minecraft plugin that introduces a unique and fun mechanic: players can mount other players! Whether for mischief, fast transportation, or pure chaotic fun, this plugin adds a new layer of interaction between players. With support for health boss bars, sneak dismounting, and mounting permissions, it enhances both casual and PvP gameplay in exciting ways.

✨ Features
🔄 Player-on-Player Mounting
Players can right-click on other players to ride them — with permission. Mounting is only allowed if the target player has enabled it through a simple command.

🔧 Commands
/playermount <true|false>
Toggle whether other players can ride you.

/playermountbossbar <true|false>
Toggle the visibility of a dynamic boss bar showing your mount's health while riding them.

🎛️ Smart Behavior
Players being mounted can eject the rider by looking up and sneaking 3 times.

Riders are automatically removed if the mount disconnects or ejects them.

Boss bars update live with the mount’s health.

Built-in protection prevents riders from damaging the player they are riding.

📊 Visual Feedback
💖 Health Boss Bar
When mounting another player (with boss bar enabled), a dynamic boss bar displays their current health. It updates every 10 ticks and disappears when the ride ends.

📣 Custom Messages
"Now riding <player>"

"<player> is now riding you"

"Look up and sneak 3 more times to dismount rider"

"Dismounted rider!" / "You were dismounted by <player>"

⚙️ Installation
Download the PlayerMountPlugin-1.0.jar file.

Place it into your server’s /plugins directory.

Restart the server or reload your plugins.

Check the console for the message: PlayerMount enabled!

🎮 Gameplay
Use /playermount true to allow other players to mount you.

Right-click a player to attempt mounting them.

The mount can eject you by looking up and sneaking three times.

Combine with boss bar display for strategic or comedic use in PvP, events, or roleplay!

🔐 Safety & Compatibility
Compatible with Minecraft 1.21 (Spigot/Paper).

Does not interfere with traditional mounts like horses or pigs.

Mounting and boss bar settings are saved per player in config.yml.

Rider-to-mount PvP damage is disabled by default.

Version: 1.0

API Version: Spigot 1.21

Author: Gabrieli2806

License: MIT