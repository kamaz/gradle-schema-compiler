package org.xsimple.gradle.plugin.schema
import org.gradle.api.Project
import org.gradle.api.file.FileCollection

class SchemaCompilerExtension {
    
    def project
    FileCollection dataDirs
    def type = 'xsd'
    def targetPackage
    def file

    SchemaCompilerExtension(Project project) {
        this.project = project
        dataDirs = project.files()
    }
    
}
