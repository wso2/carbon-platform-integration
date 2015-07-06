/*
*  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0!
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.automation.engine.frameworkutils;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.logging.Log;

import java.nio.file.Files;

import org.apache.commons.logging.LogFactory;
import org.codehaus.plexus.util.DirectoryScanner;
import org.jacoco.core.tools.ExecFileLoader;
import org.wso2.carbon.automation.engine.FrameworkConstants;
import org.wso2.carbon.automation.engine.exceptions.AutomationFrameworkException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.wso2.carbon.automation.engine.extensions.ExtensionConstants;

/**
 * This util class provides functionality for computing code coverage.
 */
public final class CodeCoverageUtils {
    private static final Log log = LogFactory.getLog(CodeCoverageUtils.class);
    private static final String DEFAULT_INCLUDES = "**";
    private static final String DEFAULT_EXCLUDES = "";

    private CodeCoverageUtils() {
    }

    public static ArrayList<String> searchDirectoryByName(String baseDir,
                                                          ArrayList<String> directoryLists,
                                                          String dirName) {
        File baseDirName = new File(baseDir);
        File[] fileArray = baseDirName.listFiles();
        if (fileArray != null) {
            for (int i = 0; i < fileArray.length; i++) {
                File name = fileArray[i];
                if (name.isDirectory()) {
                    if (name.toString().subSequence(name.toString().lastIndexOf("/") + 1,
                                                    name.toString().length()).equals(dirName)) {
                        directoryLists.add(name.getAbsolutePath());
                    }
                    searchDirectoryByName(fileArray[i].getAbsolutePath(), directoryLists, dirName);
                } else if (fileArray.length == i) {
                    return null;
                }
            }
        }
        return directoryLists;
    }


    /**
     * Method to get jacoco agent file location
     *
     * @return - jacoco agent file location
     * @throws FileNotFoundException - throws if jar file not found
     */
    public static String getJacocoAgentJarLocation() throws IOException {
        File jacocoHome = new File(System.getProperty("basedir") + File.separator + "target" + File.separator + "jacoco");
        String jacocoAgentFilePath = "";
        if (jacocoHome.exists()) {
            File[] files = jacocoHome.listFiles();
            if (files != null) {
                for (File fileName : files) {
                    if (fileName.getName().contains("org.jacoco.agent")) {
                        ArchiveExtractorUtil.extractFile(fileName.getAbsolutePath(), jacocoHome.getAbsolutePath() + File.separator + "agent");
                        File[] agentJars = new File(jacocoHome.getAbsolutePath() + File.separator + "agent").listFiles();
                        if (agentJars != null) {
                            for (File agentJar : agentJars) {
                                if (agentJar.getName().contains(FrameworkConstants.JACOCO_AGENT_JAR_NAME)) {
                                    jacocoAgentFilePath = agentJar.getAbsolutePath();
                                }
                            }
                        }
                    }
                }
            }

        } else {
            throw new FileNotFoundException("File cannot be found at " + jacocoHome.getAbsolutePath());
        }

        if (jacocoAgentFilePath.isEmpty()) {
            throw new FileNotFoundException("Jacoco Agent file path is empty and agent jar missing " +
                                            jacocoHome.getAbsolutePath());
        }

        return jacocoAgentFilePath;
    }

    /**
     * Generic method to insert new text into a given file. This method will use tmp file to hold
     * modified file content. Text provided as lineToBeInserted will be inserted to the file just after
     * lineToBeChecked
     *
     * @param inFile           - File to be modified
     * @param tmpFile          - Temporary file to hold modified file
     * @param lineToBeChecked  - File will be modified after this line
     * @param lineToBeInserted - New line to be inserted into the file
     * @throws IOException - Throws IO exception if file modification fails
     */
    public static void insertStringToFile(File inFile, File tmpFile, String lineToBeChecked,
                                          String lineToBeInserted) throws IOException {

        FileInputStream fis = new FileInputStream(inFile);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
        FileOutputStream fos;
        PrintWriter out = null;

        try {
            //create temporary out file to hold file content
            fos = new FileOutputStream(tmpFile);
            out = new PrintWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8), true);

            String thisLine = "";
            while ((thisLine = in.readLine()) != null) {
                if (thisLine.contains(lineToBeChecked)) {
                    out.println(lineToBeInserted);
                }
                out.println(thisLine);
            }

            //Need to close streams explicitly otherwise file move will not work on windows
            in.close();
            out.close();

            Files.move(tmpFile.toPath(), inFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            if (tmpFile.exists()) {
                throw new IOException("Failed to move file " + tmpFile.getAbsolutePath() + " as " +
                                      inFile.getAbsolutePath());
            }

            log.info("File " + inFile.getName() + " has been modified and inserted new line after " + lineToBeChecked);
            log.info("New line inserted into : " + inFile.getName());
            log.info("Content of the line inserted : " + lineToBeInserted);

        } finally {
            if (tmpFile.exists()) {
                if (!tmpFile.delete()) {
                    log.warn("Failed to delete temporary file - " + tmpFile.getAbsolutePath());
                }
            }

            if (out != null) {
                out.flush();
                out.close();
            }
            in.close();
        }
    }


    /**
     * Insert Jacoco agent configuration into server startup bat file. Specific to windows
     *
     * @param inFile           - File to be modified
     * @param tmpFile          - Temporary file to hold modified file
     * @param lineToBeChecked  - File will be modified after this line
     * @param lineToBeInserted - New line to be inserted into the file
     * @throws IOException - Throws IO exception if file modification fails
     */
    public static void insertJacocoAgentToStartupBat(File inFile, File tmpFile,
                                                     String lineToBeChecked,
                                                     String lineToBeInserted) throws IOException {

        FileInputStream fis = new FileInputStream(inFile);
        BufferedReader in = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
        FileOutputStream fos;
        PrintWriter out = null;

        try {
            //create temporary out file to hold file content
            fos = new FileOutputStream(tmpFile);
            out = new PrintWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8), true);

            String thisLine = "";
            while ((thisLine = in.readLine()) != null) {
                if (thisLine.contains(lineToBeChecked)) {
                    out.println(thisLine + " " + lineToBeInserted);
                } else {
                    out.println(thisLine);
                }
            }

            //Need to close streams explicitly otherwise file move will not work on windows
            in.close();
            out.close();

            Files.move(tmpFile.toPath(), inFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            if (tmpFile.exists()) {
                throw new IOException("Failed to move file " + tmpFile.getAbsolutePath() + " as " +
                                      inFile.getAbsolutePath());
            }

            log.info("File " + inFile.getName() + " has been modified and inserted new line after " + lineToBeChecked);
            log.info("New line inserted into : " + inFile.getName());
            log.info("Content of the line inserted : " + lineToBeInserted);

        } finally {
            if (tmpFile.exists()) {
                if (!tmpFile.delete()) {
                    log.warn("Failed to delete temporary file - " + tmpFile.getAbsolutePath());
                }
            }

            if (out != null) {
                out.flush();
                out.close();
            }
            in.close();
        }
    }

    /**
     * This method introduce to keep backward compatibility with older coverage generation configuration
     * files such as instrumentation.txt and filter.txt. This will replace "-" and "+" signs at
     * the beginning of jar list.
     *
     * @param patternList - Jar patten
     * @return - jar list with replaced pattern
     * @throws IOException - Throws if character replacement doesn't work
     */
    public static List<String> getInstrumentationJarList(File patternList) throws IOException {
        List<String> filePatterns = new ArrayList<String>();
        if (patternList.exists()) {
            RandomAccessFile rf = new RandomAccessFile(patternList, "r");
            try {
                String line;
                while ((line = rf.readLine()) != null) {
                    //if line ends with underscore or hyphen then replace with *
                    if (line.endsWith("_") || line.endsWith("-")) {
                        filePatterns.add(line.replace(line.substring(line.length() - 1), "*"));
                        //if line start with plus or hyphen then replace with *
                    } else if (line.startsWith("-") || line.startsWith("+")) {
                        filePatterns.add(line.replaceFirst(line.substring(0, 1), "*"));
                    } else {
                        filePatterns.add(line);
                    }
                }
            } finally {
                rf.close();
            }
        }
        return filePatterns;
    }

    public static String buildStringArrayOfJarList(List<String> jarList, String delimiter)
            throws IOException {

        StringBuilder jarListOfPatternsBuffer = new StringBuilder();
        for (String jarPattern : jarList) {
            if (!jarList.get(jarList.size() - 1).equals(jarPattern)) { //without delimiter for last entry
                jarListOfPatternsBuffer.append(jarPattern).append(delimiter);
            } else {
                jarListOfPatternsBuffer.append(jarPattern);
            }
        }
        return jarListOfPatternsBuffer.toString();
    }

    /**
     * Generates jar list to be include from coverage report generation
     *
     * @param delimiter - delimiter to be used with jar list
     * @return - String of jar list
     * @throws IOException - Throws if jar inclusion list cannot be generated
     */
    public static String getInclusionJarsPattern(String delimiter) throws IOException {
        log.info("Building jar list for Jacoco coverage inclusion...");
        File instrumentationTxt =
                System.getProperty("instr.file") != null ?
                new File(System.getProperty("instr.file")) :
                new File(System.getProperty("basedir") + File.separator +
                         "src" + File.separator + "test" + File.separator +
                         "resources" + File.separator + "instrumentation.txt");
        if (instrumentationTxt.exists()) {
            return buildStringArrayOfJarList(getInstrumentationJarList(instrumentationTxt), delimiter);
        } else {
            log.warn("Jacoco Instrumentation file " + System.getProperty("instr.file") + " does not exist");
        }
        return DEFAULT_INCLUDES;
    }

    /**
     * Generates jar list to be exclude from coverage report generation
     *
     * @param delimiter - delimiter to be used with jar list
     * @return - String of jar list
     * @throws IOException - Throws if jar exclusion list cannot be generated
     */
    public static String getExclusionJarsPattern(String delimiter) throws IOException {
        log.info("Building jar list for Jacoco coverage exclusion...");
        String jacocoFilters = System.getProperty("filters.file");
        if (jacocoFilters == null) {
            jacocoFilters = System.getProperty("basedir") + File.separator + "src" +
                            File.separator + "test" + File.separator +
                            "resources" + File.separator + "filters.txt";
        } else {
            if (!new File(jacocoFilters).exists()) {
                log.warn("Jacoco filters file " + jacocoFilters + " does not exist");
            }
        }

        if (new File(jacocoFilters).exists()) {
            return buildStringArrayOfJarList(getInstrumentationJarList(new File(jacocoFilters)), delimiter);

        }
        return DEFAULT_EXCLUDES;
    }

    /**
     * Get report creation location
     */
    public static String getJacocoReportDirectory() {
        String jacocoReportDir = System.getProperty("report.dir");
        if (jacocoReportDir == null) {
            jacocoReportDir = FrameworkPathUtil.getCoverageDirPath();
        }

        return jacocoReportDir;
    }

    /**
     * Merge coverage data files
     *
     * @param dataFilePath - path to coverage data file
     * @throws AutomationFrameworkException - Throws if coverage data files cannot be merged.
     */
    public static void executeMerge(String dataFilePath) throws AutomationFrameworkException {
        final ExecFileLoader loader = new ExecFileLoader();
        load(loader, dataFilePath);
        save(loader);
    }

    /**
     * Method will load all coverage data files
     *
     * @param loader       - ExceFileLoader
     * @param dataFilePath - Coverage data file patch
     * @throws AutomationFrameworkException - throws if coverage data files cannot be loaded.
     */
    private static void load(final ExecFileLoader loader, String dataFilePath)
            throws AutomationFrameworkException {

        Collection<File> fileSetsCollection = getJacocoDataFiles(dataFilePath);

        //if no files found
        if (fileSetsCollection.size() == 0) {
            throw new AutomationFrameworkException("Couldn't find coverage data files at " +
                                                   FrameworkPathUtil.getJacocoCoverageHome());
        }

        for (File inputFile : fileSetsCollection) {

            if (inputFile.isDirectory()) {
                continue;
            }
            try {
                log.info("Loading execution data file " + inputFile.getAbsolutePath());
                loader.load(inputFile);
            } catch (IOException e) {
                throw new AutomationFrameworkException("Unable to read " + inputFile.getAbsolutePath(), e);
            }
        }
    }

    /**
     * Merge all coverage data files and save it as single coverage data file.
     *
     * @param loader - coverage data file loader
     * @throws AutomationFrameworkException - Throws if coverage data files cannot be created
     */
    private static void save(final ExecFileLoader loader) throws AutomationFrameworkException {
        File destinationFile = new File(FrameworkPathUtil.getCoverageMergeFilePath());

        if (loader.getExecutionDataStore().getContents().isEmpty()) {
            log.warn("Execution data is empty skipping coverage generation");
            return;
        }

        log.info("Writing merged execution data to " + destinationFile.getAbsolutePath());

        try {
            loader.save(destinationFile, false);
        } catch (IOException e) {
            throw new AutomationFrameworkException("Unable to write merged file " +
                                                   destinationFile.getAbsolutePath(), e);
        }
    }

    /**
     * Method to get all coverage data files matches with the extension *.exec
     *
     * @param filePath - Path to check for coverage data files
     * @return - File collection of coverage data files
     */
    private static Collection<File> getJacocoDataFiles(String filePath) {
        return FileUtils.listFiles(
                new File(filePath),
                new RegexFileFilter("[^s]+(.(?i)(exec))$"),
                DirectoryFileFilter.DIRECTORY
        );
    }

    /**
     * Method to scan given directory for include and exclude patterns.
     *
     * @param jarExtractedDir - Patch to check for given include/exclude pattern
     * @param includes        - Include pattern array
     * @param excludes        - Exclude class pattern array
     * @return - Included files
     * @throws IOException - Throws if given directory patch cannot be found.
     */
    public static String[] scanDirectory(String jarExtractedDir, String[] includes,
                                         String[] excludes) throws IOException {
        DirectoryScanner ds = new DirectoryScanner();

        ds.setIncludes(includes);
        ds.setExcludes(excludes);
        ds.setBasedir(new File(jarExtractedDir));
        ds.setCaseSensitive(true);

        ds.scan();
        return ds.getIncludedFiles();
    }

    /**
     * Extract jar files given at jar file path
     *
     * @param jarFilePath - Jar file patch
     * @return - Jar file extracted directory.
     * @throws IOException - Throws if jar extraction fails
     */
    public synchronized static String extractJarFile(String jarFilePath)
            throws IOException {

        if (!jarFilePath.endsWith(".jar")) {
            throw new IllegalArgumentException("Jar file should have the extension .jar. " +
                                               jarFilePath + " is invalid");
        }
        JarFile jarFile = new JarFile(jarFilePath);

        String fileSeparator = (File.separatorChar == '\\') ? "\\" : File.separator;
        String jarFileName = jarFilePath;

        if (jarFilePath.lastIndexOf(fileSeparator) != -1) {
            jarFileName = jarFilePath.substring(jarFilePath.lastIndexOf(fileSeparator) + 1);
        }

        String tempExtractedDir = null;
        try {
            tempExtractedDir = FrameworkPathUtil.getJarExtractedFilePath() + File.separator +
                               jarFileName.substring(0, jarFileName.lastIndexOf('.'));

            ArchiveExtractorUtil.extractFile(jarFilePath, tempExtractedDir);
        } catch (IOException e) {
            log.warn("Could not extract the file " + jarFileName);
        } finally {
            jarFile.close();
        }
        return tempExtractedDir;
    }


    public static String[] getMatches(String[] classFiles, String[] regexArray) {
        List<String> matches = null;
        for (String regex : regexArray) {
            Pattern p = Pattern.compile(regex);
            matches = new ArrayList<String>();
            for (String s : classFiles) {
                if (!p.matcher(s).matches()) {
                    matches.add(s);
                }
            }
        }
        return (String[]) matches.toArray();
    }
}
