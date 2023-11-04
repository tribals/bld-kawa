/*
 * Copyright 2023 the original author or authors.
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

import rife.bld.BuildCommand;
import rife.bld.Project;
import rife.bld.publish.PublishDeveloper;
import rife.bld.publish.PublishLicense;
import rife.bld.publish.PublishScm;

import java.util.List;

import static rife.bld.dependencies.Repository.*;
import static rife.bld.dependencies.Scope.compile;
import static rife.bld.dependencies.Scope.test;
import static rife.bld.operations.JavadocOptions.DocLinkOption.NO_MISSING;

public class CompileKotlinOperationBuild extends Project {
    public CompileKotlinOperationBuild() {
        pkg = "rife.bld.extension";
        name = "bld-kotlin";
        version = version(0, 9, 0, "SNAPSHOT");

        javaRelease = 17;
        downloadSources = true;
        autoDownloadPurge = true;
        repositories = List.of(MAVEN_LOCAL, MAVEN_CENTRAL, RIFE2_RELEASES);

        scope(compile)
                .include(dependency("org.jetbrains.kotlin", "kotlin-compiler", version(1, 9, 20)))
                .include(dependency("com.uwyn.rife2", "bld", version(1, 7, 6, "SNAPSHOT")));
        scope(test)
                .include(dependency("org.junit.jupiter", "junit-jupiter", version(5, 10, 0)))
                .include(dependency("org.junit.platform", "junit-platform-console-standalone", version(1, 10, 0)))
                .include(dependency("org.assertj", "assertj-core", version(3, 24, 2)));

        javadocOperation()
                .javadocOptions()
                .author()
                .docLint(NO_MISSING)
                .link("https://rife2.github.io/bld/");

        publishOperation()
                .repository(version.isSnapshot() ? repository("rife2-snapshot") : repository("rife2"))
                .info()
                .groupId("com.uwyn.rife2")
                .artifactId("bld-kotlin")
                .description("bld Kotlin Extension")
                .url("https://github.com/rife2/bld-kotlin")
                .developer(new PublishDeveloper().id("ethauvin").name("Erik C. Thauvin").email("erik@thauvin.net")
                        .url("https://erik.thauvin.net/"))
                .license(new PublishLicense().name("The Apache License, Version 2.0")
                        .url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
                .scm(new PublishScm().connection("scm:git:https://github.com/rife2/bld-kotlin.git")
                        .developerConnection("scm:git:git@github.com:rife2/bld-kotlin.git")
                        .url("https://github.com/rife2/bld-kotlin"))
                .signKey(property("sign.key"))
                .signPassphrase(property("sign.passphrase"));

        testOperation().mainClass("rife.bld.extension.CompileKotlinOperationTest");
    }

    public static void main(String[] args) {
        new CompileKotlinOperationBuild().start(args);
    }

    @BuildCommand(summary = "Runs PMD analysis")
    public void pmd() {
        new PmdOperation()
                .fromProject(this)
                .failOnViolation(true)
                .ruleSets("config/pmd.xml")
                .execute();
    }
}