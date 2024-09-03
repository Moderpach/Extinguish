import kotlinx.benchmark.gradle.*
import org.jetbrains.kotlin.allopen.gradle.*
import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    java
    kotlin("jvm")
    kotlin("plugin.allopen") version (libs.versions.kotlin)
    //alias(libs.plugins.jetbrainsKotlinJvm)
    //alias(libs.plugins.pluginAllOpen)
    alias(libs.plugins.kotlinBenchmark)
}

sourceSets.configureEach {
    kotlin.srcDirs("${this.name}/src/kotlin")
    java.srcDirs("${this.name}/src/java")
    resources.srcDirs("${this.name}/resources")
}

configure<AllOpenExtension> {
    annotation("org.openjdk.jmh.annotations.State")
}

/*java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}*/

dependencies {
    implementation(libs.kotlinBenchmark.runtime)
    implementation(libs.annotations)
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

benchmark {
    targets {
        // This one matches sourceSet name above
        register("main") {
            this as JvmBenchmarkTarget
            jmhVersion = "1.21"
        }
    }
}