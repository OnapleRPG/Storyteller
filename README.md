
# Storyteller   [![Build Status](https://travis-ci.org/OnapleRPG/Storyteller.svg?branch=master)](https://travis-ci.org/OnapleRPG/Storyteller) ![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=storyteller&metric=alert_status)  [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
## Introduction  
Storyteller is a Sponge plugin designed to send messages to players through the book interface. Write stories and 
ambiance your world with fully configurable books. Set the *text*, the *color*, the *pages*, and attach the book to 
several *game events*. Books support *buttons* with callback to design more complex and interactive dialogs.
Note that it replaces the default villager's behavior.

### Functionnalities
* Callable by Commands 
* No code, only configuration
* Attachable to villagers
* Clickable Text

## Get started
The plugin is designed to work with Sponge API 7.0.0 (Minecraft 1.12). To install it, just drag and drop the jar file 
into the mods folder on your server. A default configuration will be created in the *config/storyteller* folder when you first launch the server with the plugin.

## Commands
* **/dialog *x* [*player*]** : Open a configured dialog to a given player or the command executor, *x* being the dialog identifier.  
Permission : *storyteller.command.read*
* **/reload-storyteller** : Reload the storyteller configuration files.  
Permission : *storyteller.command.reload*
* **/get-objectives *player*** : Returns storyteller associated player objectives in chat.  
Permission : *storyteller.command.objectives*
* **/set-objective *player* *objective* *value*** : Set the current player objective value for a given objective to a given number.  
Permission : *storyteller.command.objectives*

## Configuration

###Global
In the *storyteller.conf* file, there is the global plugin configuration. you can change :
* the *interaction* is if entities are interactible. if it's `true`, entities can trigger dialogs else you must use command to trigger dialogs
* A list of intractable entities. if it's empty all entity are enabled.
###Dialogs
The dialogs need to be configured first, using JSON files located in the *config/storyteller/* folder, like the following example.  
* The **id** number is used to reference a dialog from an other one or from a command call
* The **trigger** defines a list of villager names that will trigger the dialog when the player right click them
* The optional **objective** field is used to map custom objectives that act as variables being edited as a result of some dialog action
* The optional **items** field is a string matching an item name (or its id if itemizer is present) and the amount required.
* The **pages** contains the one or many pages a dialog contains
    * The **text** is the one going to be displayed to the player on this page.
    * The **buttons** is a list of clickable text buttons that are going to trigger some event
        * The **text** is what will be displayed and clickable
        * The array **actions** list the actions the button will execute. Must match one of the available actions, being : [OPEN_DIALOG|TELEPORT|GIVE_ITEM|REMOVE_ITEM|SET_OBJECTIVE]
```
1 = {
    trigger: ["Steve the villager"]
    objective: "steve_asking_wood==0"
    pages: [
        {
            text: "Hello ! Would you kindly bring me some wood please ?"
            buttons: [
                {
                    text : "&0> I will bring you wood !"
                    actions : ["SET_OBJECTIVE steve_asking_wood=1"]
                }
            ]
      }
    ]
}

2 = {
    trigger: ["Steve the villager"]
    objective : "steve_asking_wood==1"
    items: "minecraft:log 5"
    pages: [
        {
            text : "This wood looks beautiful on you !"
            buttons: [
            {
                text : "&2Here, take my wood"
                actions : ["SET_OBJECTIVE steve_asking_wood=2", "REMOVE_ITEM minecraft:log 5", "START_KILL_COUNT Zombie"]
            }
            ]
        }
    ]
}

3 = {
    trigger: ["Steve the villager"]
    objective : "steve_asking_wood==2"
    killcount: "Zombie>=5"
    pages: [
        {
            text : "I'm grateful for the wood, but I'd also like you to kill some zombies.",
            buttons: [
                {
                    text : "&4> I killed some &6zombies &4!",
                    actions : ["SET_OBJECTIVE steve_asking_wood=3"]
                }
            ]
        }
    ]
}

4 = {
    trigger: ["Steve the villager"]
    objective: "steve_asking_wood==2"
    pages: [
        {
            text: "I'm grateful for the wood, but I'd also like you to kill 5 zombies."
            buttons: [
                {
                    text: "> Alright then"
                }
            ]
        }
    ]
}
```
Here is the list of the available button actions and there arguments (separated with spaces) :  
- **OPEN_DIALOG** : Open the dialog with the given *id* (given as argument)
- **EXECUTE_COMMAND** : Execute the *command* given as argument
- **GIVE_ITEM** : Give an item to the player. Fill with an item name or itemizer id, followed by a space and the amount you want to give
- **REMOVE_ITEM** : Same as above, but removing from the player's inventory
- **TELEPORT** : Teleport to the given *location*. Use X Y Z separated with space
- **SET_OBJECTIVE** : Edit an *objective*. You can use the name you want and the operators *=*, *+=*, *-=* (ex : dialog_count+=1)
- **START_KILL_COUNT** : Start a counter for a given NPC and a given monster. Takes a monster name or type as argument.
- **STOP_KILL_COUNT** : Stop a counter for cleaning. Takes a monster name as argument, and use the NPC name (trigger).
- **CREATE_INSTANCE** : Create an instance from a world and teleport the player into it. Takes the world to copy as first argument, then three numbers for X, Y and Z position to teleport the player to (*Only if EpicBoundaries plugin is available*).
- **APPARATE** : Teleport the player to another world. Takes the world name as first argument, then the X, Y and Z position (*Only if EpicBoundaries plugin is available*)