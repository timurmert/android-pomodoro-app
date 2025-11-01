# Keep Hilt generated components
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }

# Keep WorkManager worker names
-keep class com.hydrabon.pomodoro.** extends androidx.work.ListenableWorker

# Keep Glance widget classes
-keep class com.hydrabon.pomodoro.glance.** { *; }
