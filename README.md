# Bounce Tales

Bounce Tales is a 2008 video game originally created by Rovio and licensed by Nokia that shipped with many S40 devices in the early 2010s. It was developed exclusively for the J2ME platform and uses certain features (DirectGraphics, DeviceControl) exclusive to Nokia's implementation. This repo aims to provide a legible and extensible decompilation of the game's classes and resources for preservation and... well... mainly fun.

# Intentions

This project *is*:

- A decompilation of all of the game's logic, compilable to reproduce the original behavior.
- Committed to fixing engine issues (such as high framerate bugs or render resolution changes) without affecting the outcome of physics and internal calculations.
- Focused on cleaning up the code to make it as clear as possible to the average reader.
- Packed with **optional** debugging features (such as a debug overlay or collision draw) for easier level navigation from a developer's perspective.

It is, however, *not*:

- A decompilation designed to compile into the exact same Java bytecode as the original game. 'Cause that would just be tedious. And boring.
- A behavior modification of any sorts. The game will play exactly as intended with gameplay, visuals and sounds unchanged.
- A port to another platform - although it does include a Jademula-based Windows compatibility layer.
- A series of tubes.

# Dumping game data

**Read this before building the game!**

As is the tradition with decompilations, you'll need a copy of Bounce Tales in order for projects to compile and run properly. Even though the game is free and Rovio probably doesn't care about its copyright anymore, it's more streamlined than distributing them through version control and shouldn't be a problem for most users as Bounce Tales is still widely available for download.

Most of the dumping process is already prepared for you, so all you'll need to provide is a JAR of Bounce Tales. Once you've got that ready, do the following:

1. Open the `BounceTools` project in NetBeans.
2. Run the `BounceComposer` run configuration.
3. Either drag and drop your BounceTales JAR onto the window or click the prompt to select it manually (the latter will only work on JDK 1.8 or if you have a JavaFX distribution linked).
4. Wait until the decomposing process finishes. If everything went properly, you should have a folder with editable resources in the `BounceTools/res_decomposed` folder. Now hit the `Compose resources!` button to generate the build-ready resource folder.
5. You're all done! Just remember to re-run Composer and rebuild all resources every time you want to commit their edits.

# Build instructions

Bounce Tales can be run either as a MIDlet on a Nokia J2ME device, or through Jademula as a Windows application.

## Nokia S40 platform

1. Download the [S40 5th Edition SDK](https://archive.org/download/nokia_sdks_n_dev_tools2/Series_40_5th_Edition_SDK_Feature_Pack_1_1_0.zip) and a compatible Java SE development kit (for example [this one](https://mirror.nforce.com/pub/drivers/java_jre/jre-1_5_0_22-windows-i586-p.exe)).
2. Install a version of the NetBeans IDE compatible with the Mobility plug-in. The project was made with NetBeans 8.2, but other versions may work as well.
3. Open the `BounceTales` folder in NetBeans.
4. Build the MIDlet with `Run > Build Project`.
5. The JAR and JAD should be in the `dist` subfolder within the project.

## Windows platform

1. Go to `BounceWin32/src` and create a directory junction from `bouncetales` to `BounceTales/src/bouncetales`. Unfortunately, we can't directly add the J2ME project as a dependency as it breaks error checking in older versions of NetBeans, and this step is necessary as Git can not preserve hardlinks.
2. Open `BounceWin32` in NetBeans (any version) and ensure `Jademula` is properly linked.
3. Run `bouncewin32.BounceWin32.java`. If Jademula returns a UnsatisfiedLinkError, make sure you are using a 64-bit JDK, or compile its DirectInput DLL for your platform and put it in the `lib` subfolder of the `BounceWin32` project.

## Other platforms

An Android version could be cool, but as of now this is all TBD.