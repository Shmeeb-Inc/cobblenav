plugins {
    alias(libs.plugins.cobblenav.convention.common)
}

architectury {
    platformSetupLoomIde()
    fabric()
}

repositories {
	maven(url = "https://maven.terraformersmc.com/") // For Mod Menu
}

configurations {
    getByName("developmentFabric").extendsFrom(common.get())
}

dependencies {
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.kotlin)
    modImplementation(libs.fabric.api)
	
	modImplementation(libs.bundles.fabric) {
		isTransitive = false
	}
	modCompileOnly(libs.bundles.fabric.compileOnly) {
		isTransitive = false
	}
	modRuntimeOnly(libs.bundles.fabric.runtimeOnly) {
		isTransitive = false
	}

    shadowBundle(projects.common) {
        targetConfiguration = "transformProductionFabric"
        isTransitive = false
    }

	// Fix for Cobblemon dev on Fabric
	modRuntimeOnly("org.graalvm.js:js:22.3.0")
	modRuntimeOnly("org.graalvm.sdk:graal-sdk:22.3.0")
	modRuntimeOnly("org.graalvm.regex:regex:22.3.0")
	modRuntimeOnly("org.graalvm.truffle:truffle-api:22.3.0")
	modRuntimeOnly("com.ibm.icu:icu4j:71.1")
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }

        // Apex fork: bundle the MPL-2.0 license text so the built jar carries it (MPL 3.2)
        from(rootProject.file("LICENSE.txt")) {
            rename { "LICENSE-cobblenav.txt" }
        }
    }

    remapJar {
        // inputFile is already wired to the shadow jar by the common convention plugin
        val clientBuildPath = findProperty("CLIENT_BUILD_PATH") as String?
        if (clientBuildPath != null) {
            destinationDirectory.set(
                file(
                    clientBuildPath
                        .replace("/", File.separator)
                        .replace("%home%", System.getProperty("user.home"))
                )
            )
        }
    }
}

