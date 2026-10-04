# ============================================================================
# Townsquare OS Performance & Optimization ProGuard/R8 Rules
# Optimizes bytecode, eliminates dead code, and preserves reflection metadata
# ============================================================================

# Optimization flags & aggressiveness
-optimizationpasses 5
-allowaccessmodification
-repackageclasses ''
-overloadaggressively

# Preserve line numbers and source file names for crash report deobfuscation
-keepattributes SourceFile,LineNumberTable,EnclosingMethod,InnerClasses,Signature,*Annotation*

# ----------------------------------------------------------------------------
# Kotlin & Coroutines Optimizations
# ----------------------------------------------------------------------------
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keep class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
-keep class kotlinx.coroutines.CoroutineExceptionHandler { *; }
-dontwarn kotlinx.coroutines.**

# ----------------------------------------------------------------------------
# Jetpack Compose & Material 3 Rules
# ----------------------------------------------------------------------------
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.material3.** { *; }
-keepclassmembers class * implements androidx.compose.runtime.State {
    <methods>;
}

# ----------------------------------------------------------------------------
# Room Database & SQLite Performance
# ----------------------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Dao interface * { *; }
-keep @androidx.room.Entity class * { *; }
-dontwarn androidx.room.paging.**

# ----------------------------------------------------------------------------
# Coil Image Loader & Disk Cache
# ----------------------------------------------------------------------------
-keep class coil.** { *; }
-dontwarn coil.**

# ----------------------------------------------------------------------------
# Data Models & Diagnostic Telemetry
# ----------------------------------------------------------------------------
-keep class com.example.data.model.** { *; }
-keep class com.example.diagnostics.** { *; }
-keepclassmembers class com.example.diagnostics.** {
    <fields>;
    <methods>;
}

# ----------------------------------------------------------------------------
# General Android OS Performance Rules
# ----------------------------------------------------------------------------
-dontwarn sun.misc.Unsafe
-dontwarn java.lang.invoke.**
