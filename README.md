# [Kawa](https://www.gnu.org/software/kawa/) Extension for [b<span style="color:orange">l</span>d](https://rife2.com/bld) 

[![License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/java-17%2B-blue)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Kawa](https://img.shields.io/badge/kawa-3.1.1-blue.svg)](https://kotlinlang.org)
[![bld](https://img.shields.io/badge/1.9.0-FA9052?label=bld&labelColor=2392FF)](https://rife2.com/bld)

To install, please refer to the [extensions](https://github.com/rife2/bld/wiki/Extensions) and [support](https://github.com/rife2/bld/wiki/Kotlin-Support)
documentation.

## Compile Kawa Source Code

To compile the source code located in `src/main/kawa` and `src/test/kawa` from the current project:

```java
@BuildCommand(summary = "Compiles the Kawa project")
public void compile() throws IOException {
    new CompileKawaOperation()
            .fromProject(this)
            .execute();
}
```

```console
./bld compile
```

Please check the [Compile Operation documentation](https://rife2.github.io/bld-kawa/rife/bld/extension/CompileKotlinOperation.html#method-summary)
for all available configuration options.

## Generate Javadoc

TODO

## Template Project

There is also a [Template Project](https://github.com/rife2/kawa-bld-example) with support for Kawa extension?
