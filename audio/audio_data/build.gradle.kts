apply {
    from("$rootDir/base-module.gradle")
}

dependencies {
 "implementation"(project(Modules.core))
 "implementation"(project(Modules.audioDomain))

 "implementation"(KtorClient.core)
 "implementation"(KtorClient.cio)
 "implementation"(KtorClient.serialization)
 "implementation"(KtorClient.logging)
 "implementation"(KtorClient.logback)

// "kapt"(Room.runtime)
// "implementation"(Room.compiler)
// "implementation"(Room.ktx)
}