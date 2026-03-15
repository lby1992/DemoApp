import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    // The Protobuf Gradle plugin (current version 0.9.6) is not compatible with AGP 9+.
    // Link to https://github.com/google/protobuf-gradle-plugin/issues/793.
    // Wait for the developers to fix it.
    // Move it to a separate pure java library module to avoid the issue.
    alias(libs.plugins.protobuf)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().configureEach {
            builtins {
                named("java") {
                    option("lite")
                }

                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    api(libs.protobuf.kotlin.lite)
}
