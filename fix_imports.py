import os

files = [
    'app/src/main/java/com/example/ui/screens/CreatorMonetizationScreen.kt',
    'app/src/main/java/com/example/ui/screens/ModerationDashboardScreen.kt'
]

for file in files:
    with open(file, 'r') as f:
        content = f.read()
    
    if 'androidx.compose.foundation.layout.width' not in content:
        content = content.replace('import androidx.compose.foundation.layout.padding', 'import androidx.compose.foundation.layout.padding\nimport androidx.compose.foundation.layout.width\nimport androidx.compose.foundation.layout.height')
    
    if 'ModerationDashboardScreen.kt' in file:
        if 'import androidx.compose.foundation.layout.Box' not in content:
            content = content.replace('import androidx.compose.foundation.layout.Column', 'import androidx.compose.foundation.layout.Box\nimport androidx.compose.foundation.layout.Column')
    
    with open(file, 'w') as f:
        f.write(content)
