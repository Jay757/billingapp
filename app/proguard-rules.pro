# Keep BuildConfig and its fields to prevent issues in Release build
-keep class com.billsuper.BuildConfig { *; }

# Keep all classes that are used for JSON serialization/deserialization
-keep class org.json.** { *; }

# General network related keeps
-keep class java.net.** { *; }

