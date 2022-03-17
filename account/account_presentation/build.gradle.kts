apply {
    from("$rootDir/compose-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.coreUi))
    "implementation"(project(Modules.accountDomain))
    "implementation"("com.google.firebase:firebase-auth-ktx:21.0.2")
}