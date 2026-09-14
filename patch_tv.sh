cat << 'INNER_EOF' > app/src/main/java/com/example/ui/screens/TvStreamingScreen.kt.patch
--- app/src/main/java/com/example/ui/screens/TvStreamingScreen.kt
+++ app/src/main/java/com/example/ui/screens/TvStreamingScreen.kt
@@ -1090,2 +1090,13 @@
 
+private fun parseStartTimeMinutes(timeStr: String): Int {
+    return try {
+        val parts = timeStr.split(" - ")
+        val startParts = parts[0].split(":")
+        val h = startParts[0].toInt()
+        val m = startParts[1].toInt()
+        h * 60 + m
+    } catch(e: Exception) {
+        6 * 60
+    }
+}
+
 INNER_EOF
patch -p0 < app/src/main/java/com/example/ui/screens/TvStreamingScreen.kt.patch
