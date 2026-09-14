import re

content = open("app/src/main/java/com/example/ui/screens/TvStreamingScreen.kt", "r").read()

content = content.replace("@Composable\n@Composable\nprivate fun TvOnDemandVaultSection", "@Composable\nprivate fun TvOnDemandVaultSection")

with open("app/src/main/java/com/example/ui/screens/TvStreamingScreen.kt", "w") as f:
    f.write(content)
print("done")
