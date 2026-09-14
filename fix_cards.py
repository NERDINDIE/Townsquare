import re

with open('app/src/main/java/com/example/ui/components/MediaCardItem.kt', 'r') as f:
    content = f.read()

# Replace duplicate 'onReportContent = onReportContent?.let { { it(item) } },'
content = re.sub(r'onReportContent = onReportContent\?\.let \{ \{ it\(item\) \} \},\s+onReportContent = onReportContent\?\.let \{ \{ it\(item\) \} \},', r'onReportContent = onReportContent?.let { { it(item) } },', content)

content = re.sub(r'onReportContent: \(\(\) -> Unit\)\? = null,\s+onReportContent: \(\(\) -> Unit\)\? = null,', r'onReportContent: (() -> Unit)? = null,', content)

# Fix unresolved reference 'Flag'
content = content.replace('androidx.compose.material.icons.Icons.Default.Flag', 'androidx.compose.material.icons.filled.Flag')
content = content.replace('import androidx.compose.material.icons.Icons', 'import androidx.compose.material.icons.Icons\nimport androidx.compose.material.icons.filled.Flag')

with open('app/src/main/java/com/example/ui/components/MediaCardItem.kt', 'w') as f:
    f.write(content)

