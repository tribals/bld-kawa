/*
 * Copyright 2023-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rife.bld.extension;

import static kawa.repl.compileFiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import gnu.expr.ModuleManager;
import rife.bld.BaseProject;
import rife.bld.operations.AbstractOperation;
import rife.tools.FileUtils;

/**
 * Compiles main Kawa sources in the relevant build directories.
 *
 * @author Anthony S.
 * @since 1.0
 */
public class CompileKawaOperation extends AbstractOperation<CompileKawaOperation> {
    /**
     * The Kawa file (.scm) pattern.
     */
    public static final Pattern KAWA_FILE_PATTERNS = Pattern.compile("^.*\\.(scm|sld)$");
    private static final Logger LOGGER = Logger.getLogger(CompileKawaOperation.class.getName());
    private final Collection<File> mainSourceDirectories_ = new ArrayList<>();
    private final Collection<File> mainSourceFiles_ = new ArrayList<>();
    private final Collection<File> testSourceDirectories_ = new ArrayList<>();
    private final Collection<File> testSourceFiles_ = new ArrayList<>();
    private File buildMainDirectory_;
    private File buildTestDirectory_;
    private BaseProject project_;


    /**
     * Returns the list of Kawa source file {{@code .scm and others}} contained in a given directory.
     *
     * @param directory the directory
     * @return the list of Kawa files
     */
    public static Collection<File> getKawaFileList(File directory) {
        if (directory == null) {
            return Collections.emptyList();
        } else if (!directory.exists()) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning("Directory not found: " + directory.getAbsolutePath());
            }
            return Collections.emptyList();
        } else {
            return FileUtils.getFileList(directory, KAWA_FILE_PATTERNS, null).stream()
                    .map(file -> new File(directory, file))
                    .toList();
        }
    }

    /**
     * Provides the main build destination directory.
     *
     * @param directory the directory to use for the main build destination
     * @return this operation instance
     */
    public CompileKawaOperation buildMainDirectory(File directory) {
        buildMainDirectory_ = directory;
        return this;
    }

    /**
     * Retrieves the main build destination directory.
     *
     * @return the main build destination
     */
    public File buildMainDirectory() {
        return buildMainDirectory_;
    }

    /**
     * Provides the test build destination directory.
     *
     * @param directory the directory to use for the test build destination
     * @return this operation instance
     */
    public CompileKawaOperation buildTestDirectory(File directory) {
        buildTestDirectory_ = directory;
        return this;
    }

    /**
     * Retrieves the test build destination directory.
     *
     * @return the test build destination
     */
    public File buildTestDirectory() {
        return buildTestDirectory_;
    }

    /**
     * Performs the compile operation.
     */
    @Override
    public void execute() throws IOException {
        if (project_ == null) {
            throw new IllegalArgumentException("A project must be specified.");
        }

        executeCreateBuildDirectories();
        executeBuildMainSources();
        // executeBuildTestSources();

        if (!silent()) {
            System.out.println("Kawa compilation finished successfully.");
        }
    }

    /**
     * Part of the {@link #execute execute} operation, builds the main sources.
     *
     * @throws IOException if an error occurs
     */
    protected void executeBuildMainSources() throws IOException {
        if (!silent()) {
            System.out.println("Compiling Kawa main sources.");
        }

        executeBuildSources(
                sources(mainSourceFiles(), mainSourceDirectories()),
                buildMainDirectory()
            );
    }

    /**
     * Part of the {@link #execute execute} operation, build sources to a given destination.
     *
     * @param sources     the source files to compile
     * @param destination the destination directory
     * @throws IOException if an error occurs
     */
    protected void executeBuildSources(
        Collection<File> sources,
        File destination
    ) throws IOException {
        if (sources.isEmpty() || destination == null) {
            return;
        }

        var args = new ArrayList<String>();

        // destination
        var manager = ModuleManager.getInstance();
        manager.setCompilationDirectory(destination.getAbsolutePath());

        // sources
        sources.forEach(f -> args.add(f.getAbsolutePath()));

        compileFiles(args.toArray(new String[args.size()]), 0, args.size());
    }

    /**
     * Part of the {@link #execute execute} operation, builds the test sources.
     *
     * @throws IOException if an error occurs
     */
    @SuppressWarnings("PMD.SystemPrintln")
    protected void executeBuildTestSources()
            throws IOException {
        if (!silent()) {
            System.out.println("Compiling Kotlin test sources.");
        }
        executeBuildSources(
                sources(testSourceFiles(), testSourceDirectories()),
                buildTestDirectory());
    }

    /**
     * Part of the {@link #execute execute} operation, creates the build directories.
     *
     * @throws IOException if an error occurs
     */
    protected void executeCreateBuildDirectories() throws IOException {
        if (buildMainDirectory() != null && !buildMainDirectory().exists() && !buildMainDirectory().mkdirs()) {
            throw new IOException("Could not created build main directory: " + buildMainDirectory().getAbsolutePath());
        }
        if (buildTestDirectory() != null && !buildTestDirectory().exists() && !buildTestDirectory().mkdirs()) {
            throw new IOException("Could not created build test directory: " + buildTestDirectory().getAbsolutePath());
        }
    }

    /**
     * Configures a compile operation from a {@link BaseProject}.
     * <p>
     * Sets the following from the project:
     * <ul>
     *     <li>{@link #buildMainDirectory() buildMainDirectory}</li>
     *     <li>{@link #buildTestDirectory() buildTestDirectory}</li>
     *     <li>{@link #mainSourceFiles() mainSourceFiles} to the {@code kotlin} directory in
     *     {@link BaseProject#srcMainDirectory() srcMainDirectory}</li>
     *     <li>{@link #testSourceFiles() testSourceFile} to the {@code kotlin} directory in
     *     {@link BaseProject#srcTestDirectory() srcTestDirectory}</li>
     * </ul>
     *
     * @param project the project to configure the compile operation from
     * @return this operation instance
     */
    public CompileKawaOperation fromProject(BaseProject project) {
        project_ = project;
        return buildMainDirectory(project.buildMainDirectory())
                .buildTestDirectory(project.buildTestDirectory())
                .mainSourceFiles(getKawaFileList(new File(project.srcMainDirectory(), "kawa")))
                .testSourceFiles(getKawaFileList(new File(project.srcTestDirectory(), "kawa")));
    }

    /**
     * Provides main source directories that should be compiled.
     *
     * @param directories one or more main source directories
     * @return this operation instance
     */
    public CompileKawaOperation mainSourceDirectories(File... directories) {
        mainSourceDirectories_.addAll(List.of(directories));
        return this;
    }

    /**
     * Provides a list of main source directories that should be compiled.
     *
     * @param directories a list of main source directories
     * @return this operation instance
     */
    public CompileKawaOperation mainSourceDirectories(Collection<File> directories) {
        mainSourceDirectories_.addAll(directories);
        return this;
    }

    /**
     * Retrieves the list of main source directories that should be compiled.
     *
     * @return the list of main source directories to compile
     */
    public Collection<File> mainSourceDirectories() {
        return mainSourceDirectories_;
    }

    /**
     * Provides main files that should be compiled.
     *
     * @param files one or more main files
     * @return this operation instance
     */
    public CompileKawaOperation mainSourceFiles(File... files) {
        mainSourceFiles_.addAll(Arrays.asList(files));
        return this;
    }

    /**
     * Provides a list of main files that should be compiled.
     *
     * @param files a list of main files
     * @return this operation instance
     */
    public CompileKawaOperation mainSourceFiles(Collection<File> files) {
        mainSourceFiles_.addAll(files);
        return this;
    }

    /**
     * Retrieves the list of main files that should be compiled.
     *
     * @return the list of main files to compile
     */
    public Collection<File> mainSourceFiles() {
        return mainSourceFiles_;
    }

    // Combine Kawa sources
    private Collection<File> sources(Collection<File> files, Collection<File> directories) {
        var sources = new ArrayList<>(files);
        for (var directory : directories) {
            sources.addAll(getKawaFileList(directory));
        }

        return sources;
    }

    /**
     * Provides test source directories that should be compiled.
     *
     * @param directories one or more test source directories
     * @return this operation instance
     */
    public CompileKawaOperation testSourceDirectories(File... directories) {
        testSourceDirectories_.addAll(List.of(directories));
        return this;
    }

    /**
     * Provides a list of test source directories that should be compiled.
     *
     * @param directories a list of test source directories
     * @return this operation instance
     */
    public CompileKawaOperation testSourceDirectories(Collection<File> directories) {
        testSourceDirectories_.addAll(directories);
        return this;
    }

    /**
     * Retrieves the list of test source directories that should be compiled.
     *
     * @return the list of test source directories to compile
     */
    public Collection<File> testSourceDirectories() {
        return testSourceDirectories_;
    }

    /**
     * Provides test files that should be compiled.
     *
     * @param files one or more test files
     * @return this operation instance
     */
    public CompileKawaOperation testSourceFiles(File... files) {
        testSourceFiles_.addAll(Arrays.asList(files));
        return this;
    }

    /**
     * Provides a list of test files that should be compiled.
     *
     * @param files a list of test files
     * @return this operation instance
     */
    public CompileKawaOperation testSourceFiles(Collection<File> files) {
        testSourceFiles_.addAll(files);
        return this;
    }

    /**
     * Retrieves the list of test files that should be compiled.
     *
     * @return the list of test files to compile
     */
    public Collection<File> testSourceFiles() {
        return testSourceFiles_;
    }
}
