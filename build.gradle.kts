// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.hilt.andorid) apply false
    alias(libs.plugins.ksp.android) apply false
    alias(libs.plugins.kotlin.serialize.plugin) apply false
}