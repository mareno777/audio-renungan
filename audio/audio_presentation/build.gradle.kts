apply {
    from("$rootDir/compose-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.coreUi))

    "implementation"(project(Modules.audioDomain))

    "implementation"(Coil.compose)

    "implementation"(Media.version)
    "implementation"(ExoPlayer.exoPlayer)
}