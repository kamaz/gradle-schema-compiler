package org.xsimple.gradle.plugin.schema

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class XjcOptionsSpecification extends Specification {

    
    Project project = ProjectBuilder.builder().build()
    XjcOptionsBuilder optionsBuilder = new XjcOptionsBuilder(project)

    def "should build options"() {
        given:
        def schemaFile = new File("test.xsd")
        def bindingFile = project.files("file")
        def bindingFiles = project.files("file1", "file2")
        def bindingFilesIterator = bindingFiles.files.iterator()
        def sourceDirectory = new File("src")
        def proxyFile = new File("proxy.file")
        def classpath = project.files("file1", "file2")
        def catalogFile = new File("catalog.file")
        def episodeFile = new File("episode.file")
        optionsBuilder.markGenerated = false
        def options

        when: "only with file"
        optionsBuilder.schemaFile = schemaFile
        options = optionsBuilder.buildOptions()

        then:
        options == ["-xmlschema", schemaFile]

        when: "strict validation is enabled"
        optionsBuilder.strictValidation = true
        options = optionsBuilder.buildOptions()

        then:
        options == ["-nv", "-xmlschema", schemaFile]

        when: "vendor extension is enabled"
        optionsBuilder.vendorExtension = true
        options = optionsBuilder.buildOptions()

        then:
        options == ["-nv", "-extension", "-xmlschema",  schemaFile]

        when: "binding file is specified"
        optionsBuilder.bindingFiles = bindingFile
        options = optionsBuilder.buildOptions()

        then:
        options == ["-nv", "-extension", "-xmlschema",  schemaFile, "-b", bindingFile.files.iterator().next()]

        when: "binding files are specified"
        optionsBuilder.bindingFiles = bindingFiles
        options = optionsBuilder.buildOptions()

        then:
        options == ["-nv", "-extension", "-xmlschema",  schemaFile, "-b", bindingFilesIterator.next(), "-b", bindingFilesIterator.next()]

        when: "source directory is specified"
        optionsBuilder.bindingFiles = null
        optionsBuilder.sourceDirectory = sourceDirectory
        options = optionsBuilder.buildOptions()

        then:
        options == ["-nv", "-extension", "-d", sourceDirectory, "-xmlschema", schemaFile]

        when: "package is specified"
        optionsBuilder.bindingFiles = null
        optionsBuilder.targetPackage = "target.package"
        optionsBuilder.strictValidation = false
        optionsBuilder.vendorExtension = false
        options = optionsBuilder.buildOptions()

        then:
        options == ["-d", sourceDirectory, "-p", "target.package","-xmlschema",  schemaFile]

        when: "http proxy is specfied"
        optionsBuilder.sourceDirectory = null
        optionsBuilder.targetPackage = null
        optionsBuilder.httpProxy = "proxyValue"
        options = optionsBuilder.buildOptions()

        then:
        options == ["-httpproxy", "proxyValue","-xmlschema",  schemaFile]

        when: "http proxy file is specfied"
        optionsBuilder.httpProxy = null
        optionsBuilder.httpProxyFile = proxyFile
        options = optionsBuilder.buildOptions()

        then:
        options == ["-httpproxyfile", proxyFile,"-xmlschema",  schemaFile]

        when: "classpath is specfied"
        optionsBuilder.httpProxyFile = null
        optionsBuilder.classpath += classpath
        options = optionsBuilder.buildOptions()

        then:
        options == ["-classpath", classpath.asPath,"-xmlschema",  schemaFile]
        
        when: "catalog file is specfied"
        optionsBuilder.classpath -= classpath
        optionsBuilder.catalog = catalogFile
        options = optionsBuilder.buildOptions()

        then:
        options == ["-catalog", catalogFile, "-xmlschema", schemaFile]
        
        when: "other flag options are specified"
        optionsBuilder.catalog = null
        optionsBuilder.readOnly = true
        optionsBuilder.suppressPackageAnnotationsGeneration = true
        optionsBuilder.suppressHeaderGeneration = true
        optionsBuilder.verbose = true
        optionsBuilder.quite = true
        optionsBuilder.locator = true
        optionsBuilder.synchronizedMethods = true
        optionsBuilder.markGenerated = true
        options = optionsBuilder.buildOptions()
        
        then:
        options == ["-readOnly", "-npa", "-no-header", "-verbose", "-quite", "-XLocator", "-Xsync-methods", "-mark-generated", "-xmlschema", schemaFile]

        when: "other flag options are specified"
        optionsBuilder.readOnly = false
        optionsBuilder.suppressPackageAnnotationsGeneration = false
        optionsBuilder.suppressHeaderGeneration = false
        optionsBuilder.verbose = false
        optionsBuilder.quite = false
        optionsBuilder.locator = false
        optionsBuilder.synchronizedMethods = false
        optionsBuilder.markGenerated = false
        optionsBuilder.episode = episodeFile
        options = optionsBuilder.buildOptions()

        then:
        options == ["-episode", episodeFile, "-xmlschema", schemaFile]

        when: "catalog file is specfied"
        optionsBuilder.episode = null
        optionsBuilder.target = "2.0"
        options = optionsBuilder.buildOptions()

        then:
        options == ["-target", "2.0", "-xmlschema", schemaFile]
        
        when: "catalog file is specfied"
        optionsBuilder.episode = null
        optionsBuilder.target = "2.0"
        options = optionsBuilder.buildOptions()

        then:
        options == ["-target", "2.0", "-xmlschema", schemaFile]

        when: "dtd schema type is specfied"
        optionsBuilder.target = null
        optionsBuilder.dtd
        options = optionsBuilder.buildOptions()

        then:
        options == ["-dtd", schemaFile]

        when: "default schema type is specfied"
        optionsBuilder.xmlSchema
        options = optionsBuilder.buildOptions()

        then:
        options == ["-xmlschema", schemaFile]
        
        when: "default schema type is specfied"
        optionsBuilder.xmlSchema
        options = optionsBuilder.buildOptions()

        then:
        options == ["-xmlschema", schemaFile]

        when: "default schema type is specfied"
        optionsBuilder.relaxng
        options = optionsBuilder.buildOptions()

        then:
        options == ["-relaxng", schemaFile]

        when: "default schema type is specfied"
        optionsBuilder.relaxngCompact
        options = optionsBuilder.buildOptions()

        then:
        options == ["-relaxng-compact", schemaFile]

        when: "default schema type is specfied"
        optionsBuilder.wsdl
        options = optionsBuilder.buildOptions()

        then:
        options == ["-wsdl", schemaFile]
    }

    def "should throw exception"() {

        when: "schema file is missing"
        optionsBuilder.buildOptions()

        then:
        thrown(GradleException)
    }


}
