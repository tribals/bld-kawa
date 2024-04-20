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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rife.bld.blueprints.BaseProjectBlueprint;
import rife.tools.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

class CompileKawaOperationTest {
    @BeforeAll
    static void beforeAll() {
        var level = Level.ALL;
        var logger = Logger.getLogger("rife.bld.extension");
        var consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(level);
        logger.addHandler(consoleHandler);
        logger.setLevel(level);
        logger.setUseParentHandlers(false);
    }

    @Test
    void testExecute() throws IOException {
        var tmpDir = Files.createTempDirectory("bld-kawa").toFile();

        try {
            var buildDir = new File(tmpDir, "build");
            var mainDir = new File(buildDir, "main");
            var testDir = new File(buildDir, "test");

            assertThat(mainDir.mkdirs()).isTrue();
            assertThat(testDir.mkdirs()).isTrue();

            var op = new CompileKawaOperation()
                    .fromProject(new BaseProjectBlueprint(new File("examples"), "edu.example", "app"))
                    .buildMainDirectory(mainDir)
                    .buildTestDirectory(testDir);

            op.execute();

            assertThat(tmpDir).isNotEmptyDirectory();
            assertThat(mainDir).isNotEmptyDirectory();
            assertThat(testDir).isEmptyDirectory();

            var mainOut = Path.of(mainDir.getAbsolutePath(), "edu", "example").toFile();
            assertThat(new File(mainOut, "App.class")).exists();

            // var testOut = Path.of(testDir.getAbsolutePath(), "edu", "example").toFile();
            // assertThat(new File(testOut, "ExampleTest.class")).exists();
        } finally {
            FileUtils.deleteDirectory(tmpDir);
        }
    }
}
