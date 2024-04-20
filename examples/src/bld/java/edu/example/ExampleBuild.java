package edu.example;

import rife.bld.BuildCommand;
import rife.bld.Project;
import rife.bld.extension.CompileKawaOperation;
import rife.bld.operations.exceptions.ExitStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static rife.bld.dependencies.Repository.*;
import static rife.bld.dependencies.Scope.compile;

public class ExampleBuild extends Project {
    public ExampleBuild() {
        pkg = "edu.example";
        name = "app";
        mainClass = "edu.example.App";
        version = version(1, 0, 0);

        javaRelease = 17;

        downloadSources = true;
        autoDownloadPurge = true;

        repositories = List.of(MAVEN_CENTRAL);

        final var kawa = version(3, 1, 1);
        scope(compile).include(dependency("com.github.arvyy", "kawa", kawa));

        // Include the Kawa source directory when creating or publishing sources Java Archives
        jarSourcesOperation().sourceDirectories(new File(srcMainDirectory(), "kawa"));
    }

    public static void main(String[] args) {
        new ExampleBuild().start(args);
    }

    @BuildCommand(summary = "Compiles the Kawa project")
    @Override
    public void compile() throws IOException {
        // The source code located in src/main/kawa and src/test/kawa will be compiled
        new CompileKawaOperation()
                .fromProject(this)
                .execute();
    }

    // @BuildCommand(summary = "Generates Javadoc for the project")
    // @Override
    // public void javadoc() throws ExitStatusException, IOException, InterruptedException {
    //     new DokkaOperation()
    //             .fromProject(this)
    //             .loggingLevel(LoggingLevel.INFO)
    //             // Create build/javadoc
    //             .outputDir(new File(buildDirectory(), "javadoc"))
    //             .outputFormat(OutputFormat.JAVADOC)
    //             .execute();
    // }
}
