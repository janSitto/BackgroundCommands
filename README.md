# BackgroundCommands
Background Commands (BGCommands) is a mod made for minecraft 1.12.2 that runs commands in the background. 
It was made because of the lack of resource packs in 1.12.2, since i needed something very specific for my personal modpack i made this.
it creates a bgcommands.cfg under config and you can declare the following:
Setup commands, will run only when a level/world is loaded;
Background commands, will run every X amount of ticks;
Tick interval, after how many ticks will the background commands be ran;
Server logging, determines whether the mod events will be logged in the minecraft server (not to confuse with in-game chat).

Recommendation: Put "gamerule sendCommandFeedback false" in the list of setup commands, so the in-game chat wont be polluted.
