package org.xsimple.gradle.plugin.schema
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class SchemaCompiler extends DefaultTask {

    String type
    String file
    File dataDir
    File srcDir
    XjcOptionsBuilder options = new XjcOptionsBuilder(project)

    @TaskAction
    def compileSchema() {
        setSchemaTypeBasedOnType()
        
        options.sourceDirectory = getSrcDir()
        options.schemaFile = project.file("${getDataDir()}/${type}/${getFile()}")
        
        project.file(options.sourceDirectory).mkdirs()
           
        def command = ['xjc', options.buildOptions()].flatten()
        
        logger.debug "Calling command line ${command} under ${project.projectDir}"
        
        project.exec {
            workingDir project.projectDir
            commandLine command
        }
    }

    private setSchemaTypeBasedOnType() {
        switch (type) {
            case 'dtd':
                options.dtd
                break
            case 'wsdl':
                options.wsdl
                break
        }
    }

}
