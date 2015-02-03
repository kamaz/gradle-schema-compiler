package org.xsimple.gradle.plugin.schema

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.FileCollection

/**
 * Based on http://docs.oracle.com/javase/6/docs/technotes/tools/share/xjc.html
 *      
 * @author kamil
 * @version 06/05/2013
 * @since 1.0
 *
 */
class XjcOptionsBuilder {

    // Base options 
    public static final String W3C_XML_SCHEMA = '-xmlschema'
    
    File schemaFile
    /**
     * -nv         :  do not perform strict validation of the input schema(s)
     */
    boolean strictValidation = false
    /**
     * -extension         :  allow vendor extensions - do not strictly follow the
     */
    boolean vendorExtension = false
    /**
     * -b <file/dir>      :  specify external bindings files (each <file> must have its own -b) 
     * If a directory is given, **\/*.xjb is searched  
     */
    FileCollection bindingFiles
    /** 
     * -d <dir>           :  generated files will go into this directory
     */
    File sourceDirectory
    /**
     * -p <pkg>           :  specifies the target package
     */
    String targetPackage
    
    /**
     * Specify the HTTP/HTTPS proxy. The format is [user[:password]@]proxyHost[:proxyPort]. The old -host and -port 
     * are still supported by the RI for backwards compatibility, but they have been deprecated. Note that the password 
     * specified with this option is an argument that is visible to other users who use the top command, 
     * for example. For greater security, use -httpproxyfile, below.
     * <p>
     * Adds the flag -httpproxy <httpProxy> 
     */
    String httpProxy
    
    /**
     * Specify the HTTP/HTTPS proxy using a file. Same format as above, but the password specified 
     * in the file is not visible to other users.
     * <p>
     * Adds the flag -httpproxyfile <httpProxyFile> 
     */
    File httpProxyFile
    
    /**
     *      -classpath <arg>   :  specify where to find user class files
     */
    FileCollection classpath
    /**
     *      -catalog <file>    :  specify catalog files to resolve external entity references
     *      support TR9401, XCatalog, and OASIS XML Catalog format.
     */
    File catalog
    /**
     *      -readOnly          :  generated files will be in read-only mode
     */
    boolean readOnly = false
    /**
     *      -npa               :  suppress generation of package level annotations (**\/package-info.java)
     */
    boolean suppressPackageAnnotationsGeneration
    /**
     *      -no-header         :  suppress generation of a file header with timestamp
     */
    boolean suppressHeaderGeneration
    /**
     *      -target 2.0        :  behave like XJC 2.0 and generate code that doesnt use any 2.1 features.
     */
    String target
    /**
     *      -xmlschema         :  treat input as W3C XML Schema (default)
     *      -relaxng           :  treat input as RELAX NG (experimental,unsupported)
     *      -relaxng-compact   :  treat input as RELAX NG compact syntax (experimental,unsupported)
     *      -dtd               :  treat input as XML DTD (experimental,unsupported)
     *      -wsdl              :  treat input as WSDL and compile schemas inside it (experimental,unsupported)
     */
    private String schemaType  = W3C_XML_SCHEMA
    
    /**
     *      -verbose           :  be extra verbose
     */
    boolean verbose
    /**
     *      -quiet             :  suppress compiler output
     */
    boolean quite

    // Extensions 
    /**
     *      -Xlocator          :  enable source location support for generated code
     */
    boolean locator
    /**
     *      -Xsync-methods     :  generate accessor methods with the 'synchronized' keyword
     */
    boolean synchronizedMethods
    /**
     *      -mark-generated    :  mark the generated code as @javax.annotation.Generated
     */
    boolean markGenerated = true
    /**
     *      -episode <FILE>    :  generate the episode file for separate compilation
     */
    File episode
    Project project

    XjcOptionsBuilder(project) {
        this.project = project
        bindingFiles = project.files()
        classpath = project.files()
    }
    

    def buildOptions() {
        if (schemaFile == null)
            throw new GradleException("Schema file $schemaFile is missing")
        
        def arguments = []
        if (strictValidation) arguments << '-nv'
        if (vendorExtension) arguments << '-extension'
        if (sourceDirectory) arguments << '-d' << sourceDirectory
        if (targetPackage) arguments << '-p' << targetPackage
        if (httpProxy) arguments << '-httpproxy' << httpProxy
        if (httpProxyFile) arguments << '-httpproxyfile' << httpProxyFile
        if (!classpath?.isEmpty()) arguments << '-classpath' << classpath.asPath
        if (catalog) arguments << '-catalog' << catalog
        if (readOnly) arguments << '-readOnly'
        if (suppressPackageAnnotationsGeneration) arguments << '-npa'
        if (suppressHeaderGeneration) arguments << '-no-header'
        if (verbose) arguments << '-verbose'
        if (quite) arguments << '-quite'
        if (locator) arguments << '-XLocator'
        if (synchronizedMethods) arguments << '-Xsync-methods'
        if (markGenerated) arguments << '-mark-generated'
        if (target) arguments << '-target' << target
        if (episode) arguments << '-episode' << episode
        arguments << schemaType
        arguments << schemaFile
        bindingFiles.each { file ->
            arguments << '-b' << file
        }
        arguments
    }

    def getXmlSchema() {
        schemaType = W3C_XML_SCHEMA
    }

    def getDtd() {
        schemaType = '-dtd'
    }

    def getRelaxng() {
        schemaType = '-relaxng'
    }

    def getRelaxngCompact() {
        schemaType = '-relaxng-compact'
    }

    def getWsdl() {
        schemaType = '-wsdl'
    }
}
