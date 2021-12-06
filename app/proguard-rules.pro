-dontwarn android.support.v4.**
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**
-keep public class com.google.gson.* { public *; }
-dontwarn com.google.gson.**
-keepclassmembers enum * { *; }
-keep public class okio.* { public *; }
-dontwarn okio.**
-keep public class retrofit2.* { public *; }
-dontwarn retrofit2.**
-keep public class javax.annotation.* { public *; }
-dontwarn javax.annotation.**

-dontwarn org.joda.convert.**
-keep class org.joda.convert.** { *; }

-dontwarn org.conscrypt.**
-keep class org.conscrypt.** { *; }

-dontwarn sun.misc.Unsafe
-dontwarn com.octo.android.robospice.retrofit.RetrofitJackson**
-dontwarn retrofit.appengine.UrlFetchClient
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient

-dontwarn sun.misc.**

-keep class kotlin.reflect.jvm.internal.impl.builtins.BuiltInsLoaderImpl
-keep public class kotlin.reflect.jvm.internal.impl.** { public *; }
-keep public class kotlin.reflect.jvm.internal.impl.serialization.deserialization.builtins.** { public *; }
-keep class kotlin.Metadata { *; }
-keep class io.reactivex.functions.** { *; }

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-keepclassmembers public class * {
   public <init>(...);
}