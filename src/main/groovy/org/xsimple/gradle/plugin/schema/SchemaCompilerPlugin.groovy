package org.xsimple.gradle.plugin.schema

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.JavaPlugin

import static org.apache.commons.io.FilenameUtils.getBaseName
import static org.apache.commons.io.FilenameUtils.getExtension

class SchemaCompilerPlugin implements Plugin<Project> {

    public static final String SCHEMA_COMPILER_GROUP_NAME = "Schema Compiler"
    public static final String SCHEMA_COMPILER_EXTENSION_NAME = 'schemaCompiler'
    Logger logger
    def project
    def compileSchemasTask
    SchemaCompilerExtension extension
    def supportedTypes = ['xsd', 'dtd', 'wsdl']

    @Override
    void apply(Project project) {
        this.project = project
        this.logger = project.logger

        configureJavaPlugin()
        createExtension()
        registerWhenTaskAdded()
        createSchemeCompilerTask()
        createTasksFromDataFiles()
    }

    def createTasksFromDataFiles() {
        // todo: allow configuring multiple srcDirs
        project.sourceSets.schema.resources.srcDirs.each { dataDir ->
            dataDir?.list(
                    { d, f -> new File(d, f).isDirectory() && supportedTypes.contains(f.toLowerCase()) }
                            as FilenameFilter
            )?.toList().each { type ->
                logger.debug "Found ${type} schema type under data directory ${dataDir}"
                if (supportedTypes.contains(type)) {
                    project.file("${dataDir}/${type}").listFiles(
                            { d, f -> new File(d, f).isFile() && supportedTypes.contains(getExtension(f)) }
                                    as FilenameFilter
                    ).toList().each { schemaFile ->
                        def schema = getBaseName(schemaFile.name)
                        def taskName = "${getBaseName(schema)}${type.capitalize()}CompileSchema"
                        logger.debug "Found ${schema} schema file for which I am creating task ${taskName}"
                        def schemaTask = project.task("${taskName}", type: SchemaCompiler) { SchemaCompiler task ->
                            group = SCHEMA_COMPILER_GROUP_NAME
                            description = "Compiles ${schemaFile.name} file"
                            task.file = schemaFile.name
                            task.type = type
                            task.options.targetPackage += ".${schema}.${type}"
                            if (project.file("${dataDir}/${type}/${schema}.xjb").exists()) {
                                task.options.bindingFiles += project.files("${dataDir}/${type}/${schema}.xjb")
                            }
                        }
                        compileSchemasTask.dependsOn += schemaTask
                    }
                }
            }
        }
    }


    def createSchemeCompilerTask() {
        compileSchemasTask = project.tasks.create('compileSchemas')
        compileSchemasTask.group = SCHEMA_COMPILER_GROUP_NAME
        compileSchemasTask.description = "Compiles xsd and dtd files to java code using xjc"
        project.tasks.compileJava.dependsOn compileSchemasTask

    }

    def registerWhenTaskAdded() {
        project.tasks.withType(SchemaCompiler).whenTaskAdded { SchemaCompiler task ->
            task.conventionMapping.with {
                // todo: rethink extension as at the moment doesn't add anything
                file = { extension.file }
                srcDir = { project.sourceSets.schema.java.srcDirs.iterator()[0] }
                dataDir = { project.sourceSets.schema.resources.srcDirs.iterator()[0] }
            }
            task.type = extension.type
            task.options = options
            task.options.targetPackage = extension.targetPackage ?: project.group ? project.group + '.generated' : 'generated'
        }
    }

    def createExtension() {
        extension = project.extensions.create(SCHEMA_COMPILER_EXTENSION_NAME, SchemaCompilerExtension, project)
    }

    def configureJavaPlugin() {
        project.plugins.apply(JavaPlugin)

        project.sourceSets {
            schema {
                java {
                    srcDir 'src/schema/java'
                }
                resources {
                    srcDir 'src/schema/resources'
                }
            }
        }

        project.sourceSets.main.compileClasspath += project.sourceSets.schema.runtimeClasspath
        project.sourceSets.test.compileClasspath += project.sourceSets.schema.runtimeClasspath
    }
}
