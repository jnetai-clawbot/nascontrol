# Add project specific ProGuard rules here.
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.jnetaol.nascontrol.data.model.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
