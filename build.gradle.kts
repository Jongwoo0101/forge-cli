// ForgeCLI — ForgeFramework 커널의 명령줄 클라이언트.
//
// 이 프로젝트는 커널 소스를 포함하지 않는다. 커널은 Maven 아티팩트로 가져오며,
// 로컬 개발에서는 forge-framework 저장소에서 아래 한 줄을 먼저 실행해 둔다.
//
//     ./gradlew publishToMavenLocal
//
// 그러면 ~/.m2/repository 에 io.github.jongwoo0101:forgeframework:1.0 이 설치되고
// 아래 mavenLocal()이 그대로 집어간다.

plugins {
    java
    application
}

group = "io.github.jongwoo0101"
description = "ForgeCLI — command-line client for the ForgeFramework kernel"

repositories {
    // 커널을 로컬에서 직접 빌드해 쓰는 것이 기본 경로이므로 mavenLocal()이 먼저다.
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("io.github.jongwoo0101:forgeframework:${property("forgeFrameworkVersion")}")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    // 커널과 같은 기준선: 경고 0건.
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
}

tasks.withType<Jar>().configureEach {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "ForgeFramework",
            "Created-By" to "Gradle ${gradle.gradleVersion}",
            "Build-Jdk-Spec" to "21"
        )
    }
}

// ── 실행 ───────────────────────────────────────────────────────────
//
// `./gradlew run` 으로 셸을 바로 띄운다. 셸이 표준 입력을 읽으므로 stdin을
// 연결해 주어야 하고, 진행 로그가 섞이지 않도록 --console=plain 을 권장한다.
application {
    mainClass = "forgeframework.cli.ForgeCli"
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

// 일반 jar에는 Main-Class를 넣어두되, 실행에는 커널 jar가 클래스패스에 필요하다.
tasks.named<Jar>("jar") {
    manifest {
        attributes("Main-Class" to "forgeframework.cli.ForgeCli")
    }
}

// ── 단일 파일 실행 jar ─────────────────────────────────────────────
//
// GitHub 릴리즈에서 "받아서 바로 실행"이 되도록 커널을 포함한 jar를 함께 만든다.
// 클래스패스 실행 전용 산출물이므로 module-info.class는 제외한다
// (여러 모듈의 module-info가 한 jar에 섞이면 안 되고, 클래스패스에서는 무시된다).
val fatJar by tasks.registering(Jar::class) {
    group = "build"
    description = "커널을 포함한 단일 실행 jar를 만든다 (java -jar 로 바로 실행)."
    archiveClassifier = "all"
    manifest {
        attributes("Main-Class" to "forgeframework.cli.ForgeCli")
    }
    from(sourceSets["main"].output)
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith(".jar") }
            .map { zipTree(it) }
    })
    exclude("module-info.class")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named("assemble") {
    dependsOn(fatJar)
}

// ── Javadoc 기준 ───────────────────────────────────────────────────
//
// 이 프로젝트는 export하는 패키지가 없는 실행 애플리케이션이다. 외부에 API를
// 약속하지 않으므로 "모든 public 멤버에 문서를 달라"는 규칙(missing)까지
// 강제하지 않는다. 반면 깨진 링크·잘못된 HTML 같은 실제 문서 결함은
// 커널과 동일하게 오류로 막는다.
tasks.withType<Javadoc>().configureEach {
    (options as StandardJavadocDocletOptions).apply {
        encoding = "UTF-8"
        charSet = "UTF-8"
        docEncoding = "UTF-8"
        locale = "ko"
        addBooleanOption("Xdoclint:all,-missing", true)
        addBooleanOption("Werror", true)
    }
}
