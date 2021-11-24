package org.tribot.gradle.plugin

import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import java.io.File

class TribotPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply("java")

        project.tasks.create("repoPackage") { task ->
            task.group = "tribot"
            task.doLast {

                val projectDir = project.projectDir
                val dirsToPackage = mutableListOf(projectDir.resolve("src"))

                fun getDependenciesRecursive(config: Configuration?, dirs: MutableList<File>): Unit? =
                        config?.dependencies
                                ?.mapNotNull { it as? DefaultProjectDependency }
                                ?.forEach { d ->
                                    dirs += d.dependencyProject.projectDir.resolve("src")
                                    getDependenciesRecursive(d.dependencyProject.configurations.asMap["implementation"], dirs)
                                }

                getDependenciesRecursive(project.configurations.asMap["implementation"], dirsToPackage)

                val zipFile = project.buildDir
                        .resolve("repo-deploy")
                        .also { it.mkdirs() }
                        .resolve("${project.name}.zip")

                if (zipFile.exists())
                    zipFile.delete()

                ZipFile(zipFile).also { zip ->
                    dirsToPackage.filter { it.exists() }.distinctBy { it.canonicalPath }.forEach { srcDir ->
                        srcDir.listFiles()?.forEach {
                            if (it.isFile)
                                zip.addFile(it, ZipParameters().apply { isOverrideExistingFilesInZip = false })
                            else
                                zip.addFolder(it, ZipParameters().apply { isOverrideExistingFilesInZip = false })
                        }
                    }
                }
            }
        }

        project.tasks.create("runTribotWithDebugger") { task ->
            task.group = "tribot"

            task.doLast {
                val splash = TribotSplash()
                splash.ensureUpdated()
                splash.filePath
                ProcessBuilder(
                        System.getProperty("java.home") + File.separator + "bin" + File.separator + "java",
                        "-jar",
                        splash.filePath,
                        "--remote-debugger",
                        "--debug")
                        .redirectErrorStream(true)
                        .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                        .start()
                        .waitFor()
            }
        }
    }
}