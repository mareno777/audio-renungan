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

 "implementation"(ExoPlayer.exoPlayer)

 "implementation"(Coil.core)

 "kapt"(Room.compiler)
 "implementation"(Room.runtime)
 "implementation"(Room.ktx)
}