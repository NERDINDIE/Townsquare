with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

content = content.replace('import androidx.compose.material.icons.filled.Build', 'import androidx.compose.material.icons.filled.Build\nimport androidx.compose.material.icons.filled.Person')

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
