PowerCraft-API
===============

Provides the buffer between Minecraft/Forge and powercraft to make updating much
easier- even when the API comes along!


Install instructions (for eclipse)
--------------------
Prerequisites:

a directory furthermore called /GitHub/ is already set up that contains the directories GitHub/API and GitHub/Modules

Instructions:

1. Download Minecraft Forge from files.minecraftforge.net (the src version)

2. unpack it into an empty directory (furthermore called "/minecraft/")

3. run "./gradlew setupDevWorkspace" from /minecraft/

4. when finished run "./gradlew eclipse" from /minecraft/

5. create a new directory anywhere you want - that'll be your workspace (furthermore called "/workspace/")

6. go into that workspace directory and create another one - this'll be the project name of the PowerCraft-project (furthermore called "/PowerCraft/")

7. copy EclipseConfiguration.zip from /GitHub/API/ repo into /PowerCraft/ and unzip it.

8. now open Eclipse using /workspace/ as the workspace.

9. start to create a new project

10.uncheck "use default location" and select /minecraft/as project location. If the name isn't filled in automatically give it any name. (furthermore called "projMinecraft")

11.finish the creation of the project(click on "finish")

12.now start again to create a project. Use the name of /PowerCraft/ as projects name. Eclipse should realize that there already is a project dir.

13.click finish. (this project is furthermore called "projPowerCraft")

14.right click on projPowerCraft and click on build path->configure Build path...

15.enter the tab "Projects". Remove the already existing one and add projMinecraft. Apply now (just apply don't click ok!!)

16.click on the small triangle left of the tab "resource" on the left side. click on "linked resources"

17.select the upper tab "Path Variables" and edit "GIT_LOC". Make it pointing to /GitHub/

18.apply and "ok" every dialog now until you're in the main window of eclipse.

19.select projPowerCraft make a right click on it and click "refresh" (F5)

20.Congrats you've set up your PowerCraft workspace!!!