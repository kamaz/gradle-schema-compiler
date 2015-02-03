package org.xsimple.gradle.plugin.schema

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class SchemaCompilerTaskSpecification extends Specification {
    
    Project project = ProjectBuilder.builder().build() 
    
    def "should create task"() {
        when: "plugin applied to the project"
        project.tasks.create('compileSchema', SchemaCompiler)
        
        then: "add plugin to the project"
        project.tasks.getByName('compileSchema')
    }

    def "should compile xsd schema"() {
        given:
        def compileSchema = project.tasks.create('compileSchema', SchemaCompiler)
        compileSchema.dataDir = project.file('data')
        compileSchema.type = 'xsd'
        compileSchema.file = 'echo.xsd'
        compileSchema.srcDir = project.file('src')
        compileSchema.options.targetPackage = 'org.xsimple.generated.echo'

        project.copy {
            from SchemaCompilerTaskSpecification.class.getResource("/data/xsd/echo.xsd")
            into 'data/xsd' 
        }
        when:
        compileSchema.compileSchema()

        then:
        project.file('data/xsd/echo.xsd').exists()
        project.file('src/org/xsimple/generated/echo/Echos.java').exists()
        project.file('src/org/xsimple/generated/echo/ObjectFactory.java').exists()
    }

    def "should compile dtd schema"() {
        given:
        def compileSchema = project.tasks.create('compileSchema', SchemaCompiler)
        compileSchema.dataDir = project.file('data')
        compileSchema.type = 'dtd'
        compileSchema.file = 'echo.dtd'
        // replace  with sourceSet ?
        compileSchema.srcDir = project.file('src')
        compileSchema.options.targetPackage = 'org.xsimple.generated.echo'

        project.copy {
            from SchemaCompilerTaskSpecification.class.getResource("/data/dtd/echo.dtd")
            into 'data/dtd'
        }
        when:
        compileSchema.compileSchema()

        then:
        project.file('data/dtd/echo.dtd').exists()
        project.file('src/org/xsimple/generated/echo/Echo.java').exists()
        project.file('src/org/xsimple/generated/echo/Echos.java').exists()
        project.file('src/org/xsimple/generated/echo/ObjectFactory.java').exists()
    }

    def "should compile xsd schema into custom source directory"() {
        given:
        def sourceDirectory = project.file('src')
        def compileSchema = project.tasks.create('compileSchema', SchemaCompiler)
        compileSchema.dataDir = project.file('data')
        compileSchema.type = 'xsd'
        compileSchema.file = 'echo.xsd'
        compileSchema.srcDir = sourceDirectory
        compileSchema.options.targetPackage = 'org.xsimple.generated.echo'

        project.copy {
            from SchemaCompilerTaskSpecification.class.getResource("/data/xsd/echo.xsd")
            into 'data/xsd'
        }
        when:
        compileSchema.compileSchema()

        then:
        project.file('data/xsd/echo.xsd').exists()
        project.file("${sourceDirectory}/org/xsimple/generated/echo/Echos.java").exists()
        project.file("${sourceDirectory}/org/xsimple/generated/echo/ObjectFactory.java").exists()
    }


}
