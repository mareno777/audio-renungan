apply {
    from("$rootDir/base-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"("com.google.firebase:firebase-auth-ktx:21.0.2")
    "implementation" ("com.google.android.gms:play-services-auth:20.1.0")
}