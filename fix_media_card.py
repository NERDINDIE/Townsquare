import re

with open('app/src/main/java/com/example/ui/components/MediaCardItem.kt', 'r') as f:
    content = f.read()

# Fix the signature in MediaCardItem
content = content.replace(
    'onToggleSavedOffline: ((MediaItemEntity) -> Unit)? = null,\n    onReportContent: (() -> Unit)? = null,',
    'onToggleSavedOffline: ((MediaItemEntity) -> Unit)? = null,\n    onReportContent: ((MediaItemEntity) -> Unit)? = null,'
)

# Fix the import for Flag
if 'import androidx.compose.material.icons.filled.Flag' not in content:
    content = content.replace('import androidx.compose.material.icons.filled.Favorite', 'import androidx.compose.material.icons.filled.Favorite\nimport androidx.compose.material.icons.filled.Flag')

# Fix Flag icon reference
content = content.replace('androidx.compose.material.icons.Icons.Default.Flag', 'Icons.Default.Flag')

with open('app/src/main/java/com/example/ui/components/MediaCardItem.kt', 'w') as f:
    f.write(content)
