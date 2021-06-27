import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform")
}

val GROUP: String by project
val VERSION_NAME: String by project

group = GROUP
version = VERSION_NAME

fun configInterop(target: org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget) {
    val main by target.compilations.getting
    val sqlite3 by main.cinterops.creating {
        includeDirs("$projectDir/src/include")
    }

    target.compilations.forEach { kotlinNativeCompilation ->
        kotlinNativeCompilation.kotlinOptions.freeCompilerArgs += when {
            HostManager.hostIsLinux -> listOf(
                "-linker-options",
                "-lsqlite3 -L/usr/lib/x86_64-linux-gnu -L/usr/lib"
            )
            HostManager.hostIsMingw -> listOf("-linker-options", "-lsqlite3 -Lc:\\msys64\\mingw64\\lib")
            else -> listOf("-linker-options", "-lsqlite3")
        }
    }
}

kotlin {
    val knTargets = listOf(
            macosX64(),
            iosX64(),
            iosArm64(),
            iosArm32(),
            watchosArm32(),
            watchosArm64(),
            watchosX86(),
            watchosX64(),
            tvosArm64(),
            tvosX64(),
            mingwX64("mingw"),
            linuxX64()
        )

    knTargets
        .forEach { target ->
            configInterop(target)
        }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
            }
        }
        commonTest {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-common")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
            }
        }

        val nativeCommonMain = sourceSets.maybeCreate("nativeCommonMain")
        val nativeCommonTest = sourceSets.maybeCreate("nativeCommonTest")

        val appleMain = sourceSets.maybeCreate("appleMain").apply {
            dependsOn(nativeCommonMain)
        }
        val linuxMain = sourceSets.maybeCreate("linuxX64Main").apply {
            dependsOn(nativeCommonMain)
        }


        val mingwMain = sourceSets.maybeCreate("mingwMain").apply {
            dependsOn(nativeCommonMain)
        }
        knTargets.forEach { target ->
            when {
                target.name.startsWith("mingw") -> {
                    target.compilations.getByName("main").source(mingwMain)
                    target.compilations.getByName("test").source(nativeCommonTest)
                }
                target.name.startsWith("linux") -> {
                    target.compilations.getByName("main").source(linuxMain)
                    target.compilations.getByName("test").source(nativeCommonTest)
                }
                else -> {
                    target.compilations.getByName("main").source(appleMain)
                    target.compilations.getByName("test").source(nativeCommonTest)
                }
            }

        }
    }
}

if(!HostManager.hostIsLinux) {
    tasks.findByName("linuxX64Test")?.enabled = false
    tasks.findByName("linkDebugTestLinuxX64")?.enabled = false
    tasks.findByName("publishLinuxX64PublicationToMavenRepository")?.enabled = false
}

apply(from = "../gradle/gradle-mvn-mpp-push.gradle")

tasks.register("publishMac"){
    setDependsOn(tasks.filter { t -> t.name.startsWith("publish") && t.name.endsWith("ToMavenRepository") && !t.name.contains("Linux") }.map { it.name })
}
