// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    kotlin("kapt") version "2.0.10"
    id("com.google.devtools.ksp") version "2.0.10-1.0.24"
}