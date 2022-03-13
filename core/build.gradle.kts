apply {
    from("$rootDir/base-module.gradle")
}

dependencies {
    "implementation"(KtorClient.core)
    "implementation"(KtorClient.cio)
    "implementation"(KtorClient.serialization)
    "implementation"(KtorClient.logging)
    "implementation"(KtorClient.logback)
}