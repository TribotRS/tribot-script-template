package org.tribot.gradle.plugin

import java.io.File

fun getTribotDirectory(): File {
    var directory: File?
    val userHome = System.getProperty("user.home")
    val osName = System.getProperty("os.name").toLowerCase()
    if (osName.contains("win")) {
        val appData = System.getenv("APPDATA")
        if (appData == null || appData.length < 1) {
            directory = File(userHome, ".tribot" + File.separatorChar)
        } else {
            directory = File(appData, ".tribot" + File.separatorChar)
        }
    } else if (osName.contains("solaris") || osName.contains("linux") || osName.contains("sunos") ||
            osName.contains("unix")
    ) {
        directory = File(userHome, ".tribot" + File.separatorChar)
    } else if (osName.contains("mac")) {
        directory = File(
                userHome,
                "Library" + File.separatorChar + "Application Support" + File.separatorChar +
                        "tribot"
        )
    } else {
        directory = File(userHome, "tribot" + File.separatorChar)
    }

    if (!directory.exists() && !directory.mkdirs()) {
        directory = File("data")
        println(
                ("Couldn't create separate application data directory. Using application data directory" + " as: " +
                        directory.absolutePath)
        )
    }
    return directory
}