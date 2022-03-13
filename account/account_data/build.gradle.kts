apply {
    from("$rootDir/base-module.gradle")
}

dependencies {
    "implementation"(project(Modules.core))
    "implementation"(project(Modules.accountDomain))

    "implementation"(KtorClient.core)
    "implementation"(KtorClient.cio)
    "implementation"(KtorClient.serialization)
    "implementation"(KtorClient.logback)
    "implementation"(KtorClient.logging)
}