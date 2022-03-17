apply {
    from("$rootDir/base-module.gradle")
}

dependencies {
    "implementation"(KtorClient.core)
    "implementation"(KtorClient.cio)
    "implementation"(KtorClient.serialization)
    "implementation"(KtorClient.logging)
    "implementation"(KtorClient.logback)

    "implementation"(Media.version)

    "implementation"(ExoPlayer.exoPlayer)
}