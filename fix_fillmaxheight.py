import re

content = open("app/src/main/java/com/example/ui/screens/TvStreamingScreen.kt", "r").read()

imports = """
import androidx.compose.foundation.layout.fillMaxHeight
"""

if "import androidx.compose.foundation.layout.fillMaxHeight" not in content:
    idx = content.find("import ")
    content = content[:idx] + imports.strip() + "\n" + content[idx:]

with open("app/src/main/java/com/example/ui/screens/TvStreamingScreen.kt", "w") as f:
    f.write(content)
print("done")
