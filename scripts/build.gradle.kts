import org.tribot.gradle.plugin.TribotPlugin

subprojects {
    apply<TribotPlugin>()
}

subprojects {
    tasks {
        getAt("repoPackage").dependsOn(assemble)
        classes {
            finalizedBy(getAt("repoPackage"))
        }
    }
}