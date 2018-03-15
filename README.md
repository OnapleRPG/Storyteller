# Storyteller  
## Introduction  
Storyteller is a Sponge plugin designed to send messages to players through the book interface. Write stories and 
ambiance your world with fully configurable books. Set the *text*, the *color*, the *pages*, and attach the book to 
several *game events*. Books support *buttons* with callback to design more complex and interactive dialogs.

### Functionnalities
* Callable by Commands 
* No code, only configuration
* Attachable to villagers
* Clickable Text

## Get started
The plugin is designed to work with Sponge API 7.0.0 (Minecraft 1.12). To install it, just drag and drop the jar file 
into the mods folder on your server. 

## Configuration
The dialogs need to be configured first, like the following example.  
* The **id** is used to reference a dialog from an other one or from a command call
* The **trigger** defines a list of villager names that will trigger the dialog when the player right click them
* The optional **objective** field is used to map custom objectives that act as variables being edited as a result of some dialog action
* The optional **items** field is a string matching an item name (or its id if itemizer is present) and the amount required.
* The **pages** contains the one or many pages a dialog contains
    * The **text** is the one going to be displayed to the player on this page
    * The **buttons** is a list of clickable text buttons that are going to trigger some event
        * The **text** is what will be displayed and clickable
        * The **color** defines the color of the button
        * The array **actions** list the actions the button will execute
            * The **name** must match one of the available actions, being : [OPEN_DIALOG|TELEPORT|GIVE_ITEM|REMOVE_ITEM|SET_OBJECTIVE]
            * The **arg** depends on the action
```
dialogs = [
   	{
   		id = 1,
   		trigger = ["Steve the villager"],
		objective: "steve_asking_wood==0",
   		pages = [
   			{
   				text: "Hello ! Would you kindly bring me some wood please ?",
   				buttons: [
   				    {
                        text : "> I will bring you wood !",
                        color: "BLACK",
                        actions : [
                            {
                                name: "SET_OBJECTIVE",
                                arg: "steve_asking_wood=1"
                            }
                        ]
                    }
   				]
   			}
   		]
   	},
	{
   		id = 2,
   		trigger = ["Steve the villager"],
   		objective : "steve_asking_wood==1",
   		items: "minecraft:log 5",
   		pages = [
   			{
   				text : "This wood looks beautiful on you !",
   				buttons: [
   					{
   						text : "Here, take my wood",
   						color: "BLUE",
   						actions : [
   						    {
   						        name: "SET_OBJECTIVE",
   						        arg: "steve_asking_wood=2"
   						    },
   							{
   								name: "REMOVE_ITEM",
   								arg: "minecraft:log 5"
   							},
   							{
   							    name: "START_KILL_COUNT",
   							    arg:"Zombie"
   							}
   						]
   					}
   				]
   			}
   		]
   	},
     {
         id = 3,
         trigger = ["Steve the villager"],
         objective : "steve_asking_wood==2",
         killcount: "Zombie>=5",
         pages = [
             {
                 text : "I'm grateful for the wood, but I'd also like you to kill some zombies.",
                 buttons: [
                     {
                         text : "> I killed some zombies !",
                         color: "RED",
                         actions : [
                            {
                                name: "SET_OBJECTIVE",
                                arg: "steve_asking_wood=3"
                            }
                         ]
                     }
                 ]
             }
         ]
     },
	{
   		id = 4,
   		trigger = ["Steve the villager"],
   		objective : "steve_asking_wood==2",
   		pages = [
   			{
   				text : "I'm grateful for the wood, but I'd also like you to kill 5 zombies.",
   				buttons: [
   					{
   						text : "> Alright then",
   						color: "BLACK"
   					}
   				]
   			}
   		]
   	}
]
```
Here is the list of the available button actions and there arguments :  
- **OPEN_DIALOG** : Open the dialog with the given *id* (given as argument)
- **EXECUTE_COMMAND** : Execute the *command* given as argument
- **GIVE_ITEM** : Give an item to the player. Fill with an item name or itemizer id, followed by a space and the amount you want to give
- **REMOVE_ITEM** : Same as above, but removing from the player's inventory
- **TELEPORT** : Teleport to the given *location*. Use X Y Z separated with space
- **SET_OBJECTIVE** : Edit an *objective*. You can use the name you want and the operators *=*, *+=*, *-=* (ex : dialog_count+=1)
- **START_KILL_COUNT** : Start a counter for a given NPC and a given monster. Takes a monster name or type as argument.
- **STOP_KILL_COUNT** : Stop a counter for cleaning. Takes a monster name as argument, and use the NPC name (trigger).
