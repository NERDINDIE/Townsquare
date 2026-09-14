with open('app/src/main/java/com/example/ui/components/MediaCardItem.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'onToggleSavedOffline = onToggleSavedOffline,\n                onReportContent = onReportContent?.let { { it(item) } },',
    'onToggleSavedOffline = onToggleSavedOffline?.let { { it(item) } },\n                onReportContent = onReportContent?.let { { it(item) } },'
)

with open('app/src/main/java/com/example/ui/components/MediaCardItem.kt', 'w') as f:
    f.write(content)

