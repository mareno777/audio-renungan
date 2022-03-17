apply {
    from("$rootDir/base-module.gradle")
}

dependencies {
 "implementation"(project(Modules.core))

 "implementation"(Media.version)

 "implementation"(ExoPlayer.exoPlayer)
// "implementation"(Coroutines.coroutinesAndroid)
}