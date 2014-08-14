/*
*  Copyright (c) 2005-2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.automation.engine.frameworkutils;

import com.vladium.emma.Command;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.automation.engine.frameworkutils.filters.CustomFileFilter;
import org.wso2.carbon.automation.engine.frameworkutils.filters.SuffixFilter;
import org.wso2.carbon.automation.engine.frameworkutils.filters.TypeFilter;
import org.wso2.carbon.utils.ArchiveManipulator;
import org.wso2.carbon.utils.FileManipulator;

import java.io.*;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This util class provides functionality for computing code coverage.
 */
public final class CodeCoverageUtils {
    private static final Log log = LogFactory.getLog(CodeCoverageUtils.class);
    private static ArrayList<String> direcArrayList = new ArrayList<String>();

    private CodeCoverageUtils() {
    }

    public static void init() {
        String emmaHome = System.getProperty("emma.home");
        if (emmaHome == null) {
            return;
        } else if (!emmaHome.endsWith(File.separator)) {
            emmaHome += File.separator;
        }
        try {
            if (System.getProperty("emma.properties") == null) {
                for (File file : new File(emmaHome).listFiles())
                    if (file.getName().startsWith("org.wso2.carbon.automation.engine")) {
                        ArchiveExtractorUtil.extractFile(file.getAbsolutePath(), emmaHome);
                    }
                System.setProperty("emma.properties",
                        new File(FrameworkPathUtil.getSystemResourceLocation()).getAbsolutePath()
                                + File.separator + "emma.properties");
            }
            if (System.getProperty("emma.report.html.out.file") == null) {
                System.setProperty("emma.report.html.out.file",
                        new File(emmaHome).getAbsolutePath() + File.separator + "coverage" +
                                File.separator + "index.html");
            }
            if (System.getProperty("emma.report.xml.out.file") == null) {
                System.setProperty("emma.report.xml.out.file",
                        new File(emmaHome).getAbsolutePath() + File.separator + "coverage" +
                                File.separator + "coverage.xml");
            }
            if (System.getProperty("emma.rt.control.port") == null) {
                System.setProperty("emma.rt.control.port", "44444");
            }
        } catch (IOException e) {
            log.error("Cannot initialize Emma", e);
        } catch (Exception e) {
            log.error("Cannot initialize Emma", e);
        }
    }

    public static void instrument(String carbonHome) {
        String workingDir = System.getProperty("user.dir");
        try {
            System.setProperty("user.dir", carbonHome);
            String emmaHome = System.getProperty("emma.home");
            if (emmaHome == null) {
                return;
            } else if (!emmaHome.endsWith(File.separator)) {
                emmaHome += File.separator;
            }
            String emmaJarName = null;
            for (File file : new File(emmaHome).listFiles()) {
                String fileName = file.getName();
                if (fileName.startsWith("emma") && fileName.endsWith(".jar")) {
                    emmaJarName = fileName;
                }
            }
            if (emmaJarName == null) {
                return;
            }


            direcArrayList.clear();
            searchDirectoryByName(carbonHome, direcArrayList, "lib");

            for (String aDirecArrayList : direcArrayList) {
                if (aDirecArrayList.contains("repository" + File.separator + "components" + File.separator + "lib")) {
                    FileUtils.copyFileToDirectory(new File(emmaHome + emmaJarName),
                            new File(aDirecArrayList));
                }
            }

            // Load the file patterns of the bundles to be instrumented
            instrumentSelectedFiles(carbonHome);
        } catch (IOException e) {
            log.error("Cannot instrument jars", e);
        } finally {
            System.setProperty("user.dir", workingDir);
        }
    }

    private static void instrumentSelectedFiles(String carbonHome) throws IOException {
        log.info("Instrumentation of jar files in progress ...");
        File instrumentationTxt = System.getProperty("instr.file") != null ?
                new File(System.getProperty("instr.file")) :
                new File(System.getProperty("basedir") + File.separator + "src" +
                        File.separator + "test" + File.separator +
                        "resources" + File.separator + "instrumentation.txt");
        List<String> filePatterns = new ArrayList<String>();
        if (instrumentationTxt.exists()) {
            RandomAccessFile rf = new RandomAccessFile(instrumentationTxt, "r");
            try {
                String line;
                while ((line = rf.readLine()) != null) {
                    filePatterns.add(line);
                }
            } finally {
                rf.close();
            }
        }


        // Instrument the bundles which match the specified patterns in <code>filePatterns</code>
        File plugins = null;

        direcArrayList.clear();
        searchDirectoryByName(carbonHome, direcArrayList, "plugins");

        for (String aDirecArrayList : direcArrayList) {
            if (aDirecArrayList.contains("repository" + File.separator + "components" + File.separator + "plugins")) {
                plugins = new File(aDirecArrayList);
            }
        }

        //instrument the jars at plugins directory first (otherwise emma will complain about state versions of class files
        int instrumentedFileCount = 0;
        for (File file : plugins.listFiles()) {
            if (file.isFile()) {
                if (filePatterns.isEmpty()) { // If file patterns are not specified, instrument all files
                    instrument(file);
                    instrumentedFileCount++;
                } else {
                    for (String filePattern : filePatterns) {
                        if (file.getName().startsWith(filePattern)) {
                            instrument(file);
                            instrumentedFileCount++;
                        }
                    }
                }
            }
        }

        log.info("Instrumented " + instrumentedFileCount + " file(s) in plugins directory");


        direcArrayList.clear();

        //instrument the jar files in patches directory

        File patchesDir = null;

        searchDirectoryByName(carbonHome, direcArrayList, "patches");

        for (String aDirecArrayList : direcArrayList) {
            if (aDirecArrayList.contains("repository" + File.separator + "components" + File.separator + "patches")) {
                patchesDir = new File(aDirecArrayList);
            }
        }

        int instrumentedPatchFileCount = 0;
        File[] patchFiles = patchesDir.listFiles();
        Map<Integer, String> patchesDirTreeMap = new TreeMap<Integer, String>();
        if (patchFiles != null) {
            for (File patchFile : patchFiles) {
                Pattern pattern = Pattern.compile("-?\\d+"); //get the number from patch directory name
                Matcher matcher = pattern.matcher(patchFile.getName());
                if (matcher.find()) {
                    patchesDirTreeMap.put(Integer.parseInt(matcher.group()), patchFile.getName());
                }
            }
            //patches directory names are sorted by patch number before instrumentation - this is because emma
            //records last instrumented file data in its metadata, so any duplicate file instrumentation will not cause
            //problems.
            for (Map.Entry entry : patchesDirTreeMap.entrySet()) {
                String filePathOfJarFiles = patchesDir.getAbsolutePath() + File.separator + entry.getValue();
                File name = new File(filePathOfJarFiles);
                log.info("Instrumenting jar files at - " + name.getName());
                Collection patchesCollection = FileUtils.listFiles(name, null, true);
                for (Object jarFile : patchesCollection) {
                    File jarFileName = new File(jarFile.toString());
                    if (jarFileName.exists()) {
                        for (String filePattern : filePatterns) {
                            if (jarFileName.getName().startsWith(filePattern)) {
                                instrument(jarFileName);
                                instrumentedPatchFileCount++;
                                break;
                            }
                        }
                    }
                }
            }
            log.info("Instrumented " + instrumentedPatchFileCount + " file(s) in patches directory");
        }
    }

    private static void printMap(Map<Integer, String> map) {
        for (Map.Entry entry : map.entrySet()) {
            System.out.println("Key : " + entry.getKey() + " Value : "
                    + entry.getValue());
        }
    }

    private static void instrument(File file) throws IOException {
        addEmmaDynamicImportPackage(file.getAbsolutePath());
        doEmmaInstrumentation(file);
        if (log.isDebugEnabled()) {
            log.debug("Instrumented " + file.getAbsolutePath());
        }
    }

    private synchronized static void addEmmaDynamicImportPackage(String jarFilePath)
            throws IOException {
        if (!jarFilePath.endsWith(".jar")) {
            throw new IllegalArgumentException("Jar file should have the extension .jar. " +
                    jarFilePath + " is invalid");
        }
        JarFile jarFile = new JarFile(jarFilePath);
        Manifest manifest = jarFile.getManifest();
        if (manifest == null) {
            throw new IllegalArgumentException(jarFilePath + " does not contain a MANIFEST.MF file");
        }
        String fileSeparator = (File.separatorChar == '\\') ? "\\" : File.separator;
        String jarFileName = jarFilePath;
        if (jarFilePath.lastIndexOf(fileSeparator) != -1) {
            jarFileName = jarFilePath.substring(jarFilePath.lastIndexOf(fileSeparator) + 1);
        }
        ArchiveManipulator archiveManipulator = null;
        String tempExtractedDir = null;
        try {
            archiveManipulator = new ArchiveManipulator();
            tempExtractedDir = System.getProperty("basedir") + File.separator + "target" +
                    File.separator + jarFileName.substring(0, jarFileName.lastIndexOf('.'));
            ArchiveExtractorUtil.extractFile(jarFilePath, tempExtractedDir);
        } catch (Exception e) {
            log.error("Could not extract the file", e);
        } finally {
            jarFile.close();
        }
        String dynamicImports = manifest.getMainAttributes().getValue("DynamicImport-Package");
        if (dynamicImports != null) {
            manifest.getMainAttributes().putValue("DynamicImport-Package",
                    dynamicImports + ",com.vladium.*");
        } else {
            manifest.getMainAttributes().putValue("DynamicImport-Package", "com.vladium.*");
        }
        File newManifest = new File(tempExtractedDir + File.separator + "META-INF" +
                File.separator + "MANIFEST.MF");
        FileOutputStream manifestOut = null;
        try {
            manifestOut = new FileOutputStream(newManifest);
            manifest.write(manifestOut);
        } catch (IOException e) {
            log.error("Could not write content to new MANIFEST.MF file", e);
        } finally {
            if (manifestOut != null) {
                manifestOut.close();
            }
        }
        archiveManipulator.archiveDir(jarFilePath, tempExtractedDir);
        FileManipulator.deleteDir(tempExtractedDir);
    }

    private static void doEmmaInstrumentation(File file) {
        String emmaFilters = System.getProperty("filters.file");
        if (emmaFilters == null) {
            emmaFilters = System.getProperty("basedir") + File.separator + "src" +
                    File.separator + "test" + File.separator +
                    "resources" + File.separator + "filters.txt";
        } else {
            if (!new File(emmaFilters).exists()) {
                log.warn("Emma filters file " + emmaFilters + " does not exist");
            }
        }
        File emmaFiltersFile = new File(emmaFilters);
        Command cmd = emmaFiltersFile.exists() ?
                Command.create("instr", "emmarun",
                        new String[]{"-m", "overwrite",
                                "-ip", file.getAbsolutePath(),
                                "-ix", "@" + emmaFiltersFile.getAbsolutePath()}) :
                Command.create("instr", "emmarun",
                        new String[]{"-m", "overwrite",
                                "-ip", file.getAbsolutePath()});
        cmd.run();
    }

    public static void generateReports(
            List<File> carbonHomeDirs) {       //-r html -in coverage.em,coverage.ec
        log.info("Generating code coverage report ...");
        String emmaHome = System.getProperty("emma.home");
        if (emmaHome == null) {
            log.error("Emma home not set properly");
            return;
        }
        String baseDir = System.getProperty("basedir");
        List<File> emmaEmFiles2 = getAllCoverageEmFiles(new File(baseDir));
        //If coverage em file not found, go one level up and search coverage em in all subdirectories
        if (emmaEmFiles2.size() == 0) {
            emmaEmFiles2 =
                    getAllCoverageEmFiles(new File(baseDir.substring(0, baseDir.lastIndexOf(File.separator))));
        }
        try {
            Thread.sleep(15000); //wait for coverage data dump
        } catch (InterruptedException e) {
            log.info("Report generation fails");
        }
        // find all coverage.ec files, and generate the report
        List<File> coverageDataFiles = new ArrayList<File>();
        for (File carbonHome : carbonHomeDirs) {
            if (carbonHome != null) {
                File[] coverageFiles = getCoverageDataFiles(carbonHome.getAbsolutePath());
                Collections.addAll(coverageDataFiles, coverageFiles);
            }
        }
//        Collection<File> ecFiles = FileUtils.listFiles(new File(basedir), new String[]{"ec"}, true);
        StringBuilder ecFilesString = new StringBuilder();
        StringBuilder emFilesString = new StringBuilder();
        for (File ecFile : coverageDataFiles) {
            if (ecFile != null) {
                log.info("Including Coverage EC file -" + ecFile.getAbsolutePath());
                ecFilesString.append(ecFile.getAbsolutePath()).append(",");
            }
        }
        for (File emFile : emmaEmFiles2) {
            if (emFile != null) {
                log.info("Including coverage EM file -" + emFile.getAbsolutePath());
                emFilesString.append(emFile.getAbsolutePath()).append(",");
            }
        }
        Command cmd = Command.create("report", "emmarun",
                new String[]{"-r", "xml,html", "-in",
                        emFilesString + "," + ecFilesString});
        cmd.run();
        log.info("Emma report generation completed");
    }


    private static File[] getCoverageDataFiles(String carbonHome) {

        List<File> files = (List<File>) FileUtils.listFiles(new File(carbonHome),
                new String[]{"ec"}, true);

        File[] coverageFiles = new File[files.size()];

        Iterator<File> itFileList = files.iterator();
        int count = 0;

        while (itFileList.hasNext()) {
            File filePath = itFileList.next();
            coverageFiles[count] = filePath;
            count++;
        }
        return coverageFiles;
    }

    public static boolean renameCoverageDataFile(String carbonHome) {
        //get all .ec fies and then find coverage.ec, after that rename those files
        File[] coverageDataFiles = getCoverageDataFiles(carbonHome);
        for (File coverageDatafile : coverageDataFiles) {
            if (coverageDatafile.getName().equals("coverage.ec")) {
                return coverageDatafile.renameTo(new File(carbonHome + File.separator + "coverage" +
                        System.currentTimeMillis() + ".ec"));
            }
        }
        return false;
    }

    public static List<File> getAllCoverageEmFiles(File directory) {
        if (directory.exists()) {
            return CustomFileFilter.getFilesRecursive(directory,
                    new SuffixFilter(TypeFilter.FILE, ".em"));
        }
        return null;
    }

    public static ArrayList<String> searchDirectoryByName(String baseDir, ArrayList<String> directoryLists, String dirName) {
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
}

