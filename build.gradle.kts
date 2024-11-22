// 项目级别的 build.gradle.kts 文件

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.navigation.safeargs) apply false
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
