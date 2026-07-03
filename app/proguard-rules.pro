# =============================================================================
# PhotoFix AI — ProGuard / R8 rules for release builds.
# =============================================================================

# Keep line numbers for readable crash reports, hide original file names.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*, Signature, Exceptions, InnerClasses, EnclosingMethod

# --- Retrofit / OkHttp (future production API mode) --------------------------
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn javax.annotation.**

# --- Gson (DTO serialization) ------------------------------------------------
-keep class com.umeetech.photofixai.data.remote.dto.** { *; }
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# --- Room --------------------------------------------------------------------
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-dontwarn androidx.room.paging.**

# --- ML Kit (on-device segmentation) -----------------------------------------
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# --- Kotlin / Coroutines -----------------------------------------------------
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }
-dontwarn kotlinx.coroutines.**

# Keep domain enums referenced by name (Room stores enum names as strings).
-keepclassmembers enum com.umeetech.photofixai.** { *; }
