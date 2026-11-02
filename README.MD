# **ServerTools**

**Note: Starting from version 26.3, the license has been updated. Older versions remain under their original license.**

[中文](https://github.com/An-Mao/Servers/blob/main/README_ZH.MD)

## Dependencies

This mod has [Cores](https://github.com/An-Mao/Cores) built-in.

## Overview

A Minecraft server-side mod.

## Miscellaneous

For version 2.0.0 and above, you can set your language using the `/lang` command (e.g., `/lang zh_cn`). `zh_cn` refers to the language file name available on the server. If the corresponding language file does not exist, the server's default language will be used.

## Features

* **Auto Entity Clearing**
* Item drops (cleared according to configuration)
* Mobs/Entities (cleared according to configuration)
* Abnormal entities (determined based on entity status)
* **Trash Can:** Cleared item drops can be stored in the trash can for retrieval.

* **Authentication System**
* **Password Login:** A login GUI/menu will automatically pop up when a player joins the game. The password entered for the first time will be set as the account password for future logins.
* **Welcome Messages:** Custom messages sent to players upon every join, first-time join, or daily first join.
* **Login Rewards:** Claimable rewards configurable via the config file.

* **Basic Teleportation (TPA)**
* **Player Blacklist / Whitelist**
* Players on the blacklist cannot join the server. The list can be inverted to act as a whitelist, allowing only listed players to join.

* **Economy & Shops**
* **Player Shop:** Players can list items and entities for sale.
* **Admin Shop:** System shop for selling items.

* **Banned Items**
* Intercepts and prevents banned item creation at the `ItemStack` level.

* **Other Utilities**
* **Enchantment Extraction:** Extract enchantments from items onto books via anvil.
