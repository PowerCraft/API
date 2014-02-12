PowerCraft-API
===============

Provides the buffer between Minecraft/Forge and powercraft to make updating much
easier- even when the API comes along!


Install instructions (for eclipse)
--------------------
Prerequisites/This has to be done once even if you create several projects:

1. a directory (furthermore called /GitHub/) that contains the directories GitHub/API and GitHub/Modules is already set up

2. Open eclipse and go to the menu item "window" and click on preferences.

3. click on the small triangle next to the tab "General" then the one next ot the tab "Workspace" and last click on the tab "Linked Resources"

4. Now click on add in the lower part and add the Variable "GIT_LOC". Make it pointing to /GitHub/

5. apply and "ok" every dialog now until you're in the main window of eclipse.

6. close eclipse now and follow the follwing instructions.

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

10. uncheck "use default location" and select /minecraft/as project location. If the name isn't filled in automatically give it any name. (furthermore called "projMinecraft")

11. finish the creation of the project(click on "finish")

12. now start again to create a project. Use the name of /PowerCraft/ as projects name. Eclipse should realize that there already is a project dir.

13. click finish. (this project is furthermore called "projPowerCraft")

14. right click on projPowerCraft and click on build path->configure Build path...

15. enter the tab "Projects" and add projMinecraft. Apply now until you're back in the main window.

20. select projPowerCraft make a right click on it and click "refresh" (F5)

21. Congrats you've set up your PowerCraft workspace!!!