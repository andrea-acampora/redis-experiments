plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.jackson)
    implementation(libs.lettuce)
    implementation(libs.reactivex.java)
    testImplementation(libs.bundles.testing)
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        showCauses = true
        showStackTraces = true
        events(*org.gradle.api.tasks.testing.logging.TestLogEvent.values())
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
