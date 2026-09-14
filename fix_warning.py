with open('app/src/main/java/com/example/ui/components/MediaCardItem.kt', 'r') as f:
    content = f.read()

content = content.replace('import androidx.compose.material.icons.filled.Flag', 'import androidx.compose.material.icons.filled.Warning')

with open('app/src/main/java/com/example/ui/components/MediaCardItem.kt', 'w') as f:
    f.write(content)
