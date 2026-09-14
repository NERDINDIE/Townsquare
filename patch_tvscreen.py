with open("app/src/main/java/com/example/ui/screens/TvStreamingScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    "                    onToggleScanlineFx = onToggleScanlineFx,\n                    onNextChannel = onNextChannel,\n                    onPrevChannel = onPrevChannel,",
    "                    onToggleScanlineFx = onToggleScanlineFx,\n                    onToggleRecording = { onToggleRecording(activeChannel) },\n                    onNextChannel = onNextChannel,\n                    onPrevChannel = onPrevChannel,"
)

with open("app/src/main/java/com/example/ui/screens/TvStreamingScreen.kt", "w") as f:
    f.write(content)
