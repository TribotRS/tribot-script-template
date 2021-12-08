# TRiBot scripting gradle template

## Adding a new script
1) Add a new directory under `scripts`
2) Open `settings.gradle.kts` under the root directory
3) Add an include for your new script like: `include("scripts:my-script-name")`
4) Add a `src` directory inside your new script directory
5) Add a `scripts` directory inside your new script src directory
6) Refresh gradle within your IDE

## Adding a new library
1) Add a new directory under `libraries`
2) Open `settings.gradle.kts` under the root directory
3) Add an include for your new library like: `include("libraries:my-library-name")`
4) Add a `src` directory inside your new library directory
5) Add a `scripts` directory inside your new library src directory
6) Refresh gradle within your IDE
7) Add the new library dependency to any script in the build.gradle.kts file within your script module. Add this inside
the dependency block: `implementation(project(":libraries:my-library-name"))`

## Other dependencies
If you are planning on running your script on the TRiBot repository, you cannot add custom dependencies to your 
script that are not included in TRiBot. 

If you are running locally, add jars to your .tribot/thirdparty folder and 
this (and TRiBot) will pick them up automatically. Perform a gradle refresh in your IDE after adding.

## Features
* Compile scripts for TRiBot using the "build" gradle task
* Delete your compiled scripts using the "clean" gradle task
* Pack your scripts into a zip to upload to the repository using repoPackage (or use repoCopy to package them all)
* (IntelliJ only) Debug your scripts through a remote debug config named "Debug TRiBot". This will launch tribot and
attach a remote debugger so that you can step through your scripts.
* Update repo scripts

## Repository Updating
### Update your script on the TRiBot Repository:
1) Put the script's repository ID in the script's corresponding gradle.properties file with the key repoId. Ex. 
   `repoID=1000` . This can take a comma separated list of ids, if you have multiple variants.
2) Run the `repoUpdate` task in your script gradle project. (or run `repoUpdateAll` in the root project to update 
   every script)