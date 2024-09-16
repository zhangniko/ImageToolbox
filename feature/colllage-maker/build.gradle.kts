plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.feature)
    alias(libs.plugins.image.toolbox.hilt)
    alias(libs.plugins.image.toolbox.compose)
}

android.namespace = "ru.tech.imageresizershrinker.feature.collage_maker"

dependencies {
    implementation(libs.toolbox.collages)
}