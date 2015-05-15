/*
*Copyright (c) 2015​, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.automation.engine.frameworkutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.plexus.util.FileUtils;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.MultiReportVisitor;
import org.jacoco.report.csv.CSVFormatter;
import org.jacoco.report.html.HTMLFormatter;
import org.jacoco.report.xml.XMLFormatter;

/**
 * Jacoco Report generator class. Provides HTML, CSV and XML report
 * creation.
 */
public class ReportGenerator {
    private static final Log log = LogFactory.getLog(ReportGenerator.class);
    public static final String OUTPUT_ENCODING = "UTF-8";
    private final File executionDataFile;
    private final File classesDirectory;
    private final File sourceDirectory;
    private final File reportDirectory;

    private ExecFileLoader execFileLoader;

    public ReportGenerator(File executionDataFile, File classesDirectory, File reportDirectory,
                           File sourceDirectory) {
        this.executionDataFile = executionDataFile;
        this.classesDirectory = classesDirectory;
        this.reportDirectory = reportDirectory;
        this.sourceDirectory = sourceDirectory;
    }

    /**
     * Create Jacoco Coverage file
     *
     * @throws IOException - Throws if coverage file generation fails
     */
    public void create() throws IOException {

        // Read the jacoco.exec file. Multiple data files could be merged
        // at this point
        loadExecutionData();

        // Run the structure analyzer on a single class folder to build up
        // the coverage model.
        final IBundleCoverage bundleCoverage = analyzeStructure();
        createReport(bundleCoverage);
    }

    private void createReport(final IBundleCoverage bundleCoverage)
            throws IOException {

        final IReportVisitor visitor = createVisitor(Locale.getDefault());

        // Initialize the report with all of the execution and session
        // information.
        visitor.visitInfo(execFileLoader.getSessionInfoStore().getInfos(),
                          execFileLoader.getExecutionDataStore().getContents());

        // Populate the report structure with the bundle coverage information.
        // Call visitGroup if you need groups in your report.
        visitor.visitBundle(bundleCoverage, new DirectorySourceFileLocator(sourceDirectory, OUTPUT_ENCODING, 4));
//        visitor.visitGroup("AS");

        // Signal end of structure information to allow report to write all
        // information out
        visitor.visitEnd();
    }


    IReportVisitor createVisitor(final Locale locale) throws IOException {
        final List<IReportVisitor> visitors = new ArrayList<IReportVisitor>();

        if (getOutputDirectoryFile().exists()){
            //delete coverage directory if it already exists. To avoid report generation
            // conflicts when two carbon servers are shutting down
            FileUtils.deleteDirectory(new File(getOutputDirectoryFile().getAbsolutePath()));
        }

        if (!getOutputDirectoryFile().mkdirs()) {
            throw new IOException("Failed to create coverage report directory - " + getOutputDirectoryFile());
        }

        final HTMLFormatter htmlFormatter = new HTMLFormatter();
        htmlFormatter.setOutputEncoding(OUTPUT_ENCODING);
        htmlFormatter.setLocale(locale);
        visitors.add(htmlFormatter.createVisitor(new FileMultiReportOutput(getOutputDirectoryFile())));

        final XMLFormatter xmlFormatter = new XMLFormatter();
        xmlFormatter.setOutputEncoding(OUTPUT_ENCODING);
        visitors.add(xmlFormatter.createVisitor(new FileOutputStream(new File(getOutputDirectoryFile(), "jacoco.xml"))));

        final CSVFormatter csvFormatter = new CSVFormatter();
        csvFormatter.setOutputEncoding(OUTPUT_ENCODING);
        visitors.add(csvFormatter.createVisitor(new FileOutputStream(new File(getOutputDirectoryFile(), "jacoco.csv"))));

        return new MultiReportVisitor(visitors);
    }


    private File getOutputDirectoryFile() {
        return this.reportDirectory;
    }

    private void loadExecutionData() throws IOException {
        execFileLoader = new ExecFileLoader();
        execFileLoader.load(executionDataFile);
    }

    private IBundleCoverage analyzeStructure() throws IOException {

        final CoverageBuilder coverageBuilder = new CoverageBuilder();
        final Analyzer analyzer = new Analyzer(execFileLoader.getExecutionDataStore(), coverageBuilder);

        final List<File> filesToAnalyze =
                FileUtils.getFiles(classesDirectory,
                                   CodeCoverageUtils.getInclusionJarsPattern(","),
                                   CodeCoverageUtils.getExclusionJarsPattern(","));

        for (final File file : filesToAnalyze) {
            analyzer.analyzeAll(file);
            log.info("File analyzed for coverage : " + file.getName());
        }
        return coverageBuilder.getBundle("Overall Coverage Summary");
    }


}

