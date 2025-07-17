import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.9.21"
    id("org.jetbrains.compose") version "1.5.11"
}

group = "com.arcane"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Compose BOM
    implementation(compose.desktop.currentOs)
    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.ui)
    implementation(compose.materialIconsExtended)
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
    
    // File compression libraries
    implementation("org.apache.commons:commons-compress:1.24.0")
    implementation("net.sf.sevenzipjbinding:sevenzipjbinding:16.02-2.01")
    implementation("net.sf.sevenzipjbinding:sevenzipjbinding-all-platforms:16.02-2.01")
    implementation("com.github.junrar:junrar:7.5.4")
    
    // Logging
    implementation("org.slf4j:slf4j-simple:2.0.9")
    
    // Testing
    testImplementation(kotlin("test"))
}

compose.desktop {
    application {
        mainClass = "com.arcane.MainKt"
        
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Arcane"
            packageVersion = "1.0.0"
            description = "Modern Archive Extraction Tool"
            copyright = "© 2025 Arcane. All rights reserved."
            vendor = "Arcane"
            
            macOS {
                bundleID = "com.arcane.app"
                // iconFile.set(project.file("src/main/resources/icon.icns"))
            }
            
            windows {
                // iconFile.set(project.file("src/main/resources/icon.ico"))
            }
            
            linux {
                // iconFile.set(project.file("src/main/resources/icon.png"))
            }
        }
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}
