PowerCraft-API
===============

Provides the buffer between Minecraft/Forge and powercraft to make updating much
easier- even when the API comes along!



Install instructions (for eclipse)
--------------------
1. Download Minecraft Forge from files.minecraftforge.net (the src version)
2. unpack it into an empty directory
3. run ./gradlew setupDevWorkspace from this directory
4. run ./gradlew eclipse
5. open eclipse and switch the workspace to the eclipse folder 
6. create in the eclipse folder a folder called Powercraft, and extract EclipseConfiguration.zip into it
7. create in eclipse a new Java Project called Powercraft
8. clone this and the Modules Repro into an empty directory
9. change the GIT_LOC var (in eclipse) to your git repro folder
10. You're done!
