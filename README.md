# Storyteller ![Github Action](https://github.com/OnapleRPG/Storyteller/actions/workflows/gradle.yml/badge.svg) ![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=com.onaple%3AStoryteller&metric=reliability_rating) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![sponge version](https://img.shields.io/badge/sponge-7.2.0-blue.svg)](https://www.spongepowered.org/) 

Storyteller is a Sponge Minecraft plugin designed to implement a quest system. Write stories with objectives and add active lore to your world.  
Set the *text*, the *color*, the *pages*, and attach your book to several *game events*. Books support *buttons* with callback to design more complex and interactive dialogs.  

## Installation

This plugin needs a sponge server 1.12. Download our [latest release](https://github.com/OnapleRPG/Storyteller/releases) and copy it into your server's `mods/` folder. Then restart your server.  
Check that a `/config/storyteller.conf` file and `/config/storyteller` folder exist to verify if default configuration was properly copied, and you should notice server logs mentionning the plugin loaded.  

## Getting started

The plugin uses its configuration to create a book interaction with some entity. Using the default generated config, spawn a villager named "*Steve the villager*" to try out the config. You should have a book interface open up when interacting with him.  
You can use it with commands as well, on yourself or other players.  

## Commands

* `/dialog read|r <index> [player] ` : Open a configured dialog to a given player or the command executor, *index* being the dialog identifier.
_Permission : `storyteller.command.dialog`_
* `/dialog info|i <index>` : Get informations about the dialog with the corresponding id, *index* being the dialog identifier.  
_Permission : `storyteller.command.dialog`_
* `/dialog trigger|t <npcName> [player]` : Load the current player dialog for the npc according to conditions, *npcName* being the npc name, and a given player or the command executor.  
_Permission : `storyteller.command.dialog`_
* `/reload-storyteller` : Reload the storyteller configuration files.  
_Permission : `storyteller.command.reload`_
* `/dialog objective|o get <player>` : Returns associated player objectives in chat.  
_Permission : `storyteller.command.objectives`_
* `/dialog objective|o set <player> <objective> <value>` : Set the current player objective value for a given objective to a given number.  
_Permission : `storyteller.command.objectives`_

## Configuration

### Global config

In the *storyteller.conf* file, there is the global plugin configuration. you can change :
* **interaction**: allow entities to trigger the storyteller dialog system, if appliable. If `true`, entities can trigger dialogs when their name matches a trigger; else you must use commands to trigger dialogs.
* **interactibleentity**: List of entity types that can trigger dialogs. If empty, all entity are enabled.

### Dialogs
The dialogs need to be configured first, using JSON files located in the *config/storyteller/* folder. [The default configuration](./src/main/resources/assets/storyteller/example.conf) includes a basic quest that illustrates the system and works as is.   
* The **id** number/string is used to reference a dialog from an other one or from a command call. When multiple dialogs match the conditions, the first id in alphabetic order will be chosen.
* The optional **trigger** defines a list of entity names that will trigger the dialog when the player right click them, if the global settings allow it.
* The optional **objective** field is used to add prerequisites on custom variables
* The optional **items** field is an item requirement with string matching the item name (or its id, if itemizer is present) and the amount required.
* The **pages** is an array containing one or many pages to display within the dialog
    * The **text** is the one going to be displayed to the player on this page.
    * The **buttons** is a list of clickable text buttons that cantrigger some event
        * The **text** will be the clickable text. You can use minecraft color codes, like &2
        * The optional array **actions** lists the actions the button will execute. Must match one of the available actions, available below.
  
Here is the list of the available button actions and their arguments (separated with spaces) :  
- **OPEN_DIALOG** : Open the dialog with the given *id* (given as argument)
- **EXECUTE_COMMAND** : Execute the *command* given as argument
- **GIVE_ITEM** : Give an *item* to the player. Fill with an item type or itemizer id, followed by the *amount* you want to give
- **REMOVE_ITEM** : Same as above, but removing the *item* from the player's inventory, with an *amount*
- **TELEPORT** : Teleport to the given *location*. Use X Y Z separated with space
- **SET_OBJECTIVE** : Edit an *objective*. You can use the name you want and the operators *=*, *+=*, *-=* (ex : dialog_count+=1)
- **START_KILL_COUNT** : Start a counter for a given NPC and a given monster. Takes a monster name or type as argument (case sensitive).
- **STOP_KILL_COUNT** : Stop a counter for cleaning. Takes a monster name as argument, and use the NPC name.
- **CREATE_INSTANCE** : Create an instance from a world and teleport the player into it. Takes the world to copy as first argument, then three numbers for X, Y and Z position to teleport the player to (*Only if EpicBoundaries plugin is available*).
- **APPARATE** : Teleport the player to another world. Takes the world name as first argument, then the X, Y and Z position (*Only if EpicBoundaries plugin is available*)

>*All "objective" and "kill_count" variables are linked to the player name and entity name: you will have the same progression with another entity with the same name*  
