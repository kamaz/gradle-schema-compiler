package org.xsimple.gradle.plugin.schema
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Ignore
import spock.lang.Specification

class SchemaCompilerPluginSpecification extends Specification {
    
    Project project = ProjectBuilder.builder().build() 
    
    def "plugin should create a compileSchemas task"() {
        given:
        project.plugins.apply(SchemaCompilerPlugin)
        
        expect: "plugin applied to the project"
        project.tasks.getByName('compileSchemas') != null
        project.tasks.getByName('compileSchemas').group == "Schema Compiler"
        project.tasks.getByName('compileSchemas').description == "Compiles xsd and dtd files to java code using xjc"
    }
    
    def "plugin should have sourceSets configured"() {
        given:
        project.plugins.apply(SchemaCompilerPlugin)
        
        expect:
        project.sourceSets.schema.java.srcDirs.contains(project.file('src/schema/java'))
        project.sourceSets.schema.resources.srcDirs.contains(project.file('src/schema/resources'))
    }

    def "plugin should add schema runtime classpath to java compile classpath"() {
        given:
        project.plugins.apply(SchemaCompilerPlugin)

        expect:
        project.sourceSets.schema.runtimeClasspath.each { runtimePath ->
            project.sourceSets.main.compileClasspath.contains(runtimePath)
        }
    }

    def "plugin should use project group for package name when target package has not been defined"() {
        given:
        project.copy {
            from SchemaCompilerTaskSpecification.class.getResource("/data/dtd/echo.dtd")
            into 'src/schema/resources/dtd'
        }

        when: "plugin applied"
        project.group = "group.name"
        project.plugins.apply(SchemaCompilerPlugin)
        

        then: "echo xsd task is configured"
        def echoDTDTask = project.tasks.getByName("echoDtdCompileSchema") as SchemaCompiler
        echoDTDTask.options.targetPackage == 'group.name.generated.echo.dtd'
    }

    @Ignore("Add support for overriding target package name")
    def "plugin should use target package when defined"() {
        given:
        project.copy {
            from SchemaCompilerTaskSpecification.class.getResource("/data/dtd/echo.dtd")
            into 'src/schema/resources/dtd'
        }

        when: "plugin applied"
        project.plugins.apply(SchemaCompilerPlugin)
        project.schemaCompiler.targetPackage = "target.package"


        then: "echo xsd task is configured"
        def echoDTDTask = project.tasks.getByName("echoDtdCompileSchema") as SchemaCompiler
        echoDTDTask.options.targetPackage == 'target.package.generated.echo.dtd'
    }


    def "plugin should use schema resource directory for dataDirs by default"() {
        given:
        project.copy {
            from SchemaCompilerTaskSpecification.class.getResource("/data/dtd/echo.dtd")
            into 'src/schema/resources/dtd'
        }
        project.copy {
            from SchemaCompilerTaskSpecification.class.getResource("/data/xsd/echo.xjb")
            from SchemaCompilerTaskSpecification.class.getResource("/data/xsd/echo.xsd")
            into 'src/schema/resources/xsd'
        }
        
        when: "plugin applied"
        project.plugins.apply(SchemaCompilerPlugin)

        then: "echo xsd task is configured"
        def echoXsdTask = project.tasks.getByName("echoXsdCompileSchema") as SchemaCompiler
        echoXsdTask.type == 'xsd'
        echoXsdTask.file == 'echo.xsd'
        project.sourceSets.schema.java.srcDirs.contains(echoXsdTask.srcDir)
        echoXsdTask.options.targetPackage == 'generated.echo.xsd'
        echoXsdTask.options.bindingFiles.files.contains(project.file('src/schema/resources/xsd/echo.xjb'))
        

        and: "echo dtd task is configured"
        def echoDtdTask = project.tasks.getByName("echoDtdCompileSchema") as SchemaCompiler
        echoDtdTask.type == 'dtd'
        echoDtdTask.file == 'echo.dtd'
        echoDtdTask.options.targetPackage == 'generated.echo.dtd'
        project.sourceSets.schema.java.srcDirs.contains(echoDtdTask.srcDir)
        
        and: "registered tasks depends on compileSchemas tasks"
        def compileSchemasTask = project.tasks.getByName('compileSchemas')
        compileSchemasTask.dependsOn.contains(echoXsdTask)
        compileSchemasTask.dependsOn.contains(echoDtdTask)
    }

}
