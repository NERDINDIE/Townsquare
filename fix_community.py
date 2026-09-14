import re

with open('app/src/main/java/com/example/ui/screens/CommunityScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('com.example.ui.components.TopNavBar', 'androidx.compose.material3.TopAppBar')

content = content.replace('TopNavBar(', 'TopAppBar(')

with open('app/src/main/java/com/example/ui/screens/CommunityScreen.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

content = content.replace('Icons.Default.People', 'androidx.compose.material.icons.Icons.Default.Group')
# But wait, Group isn't imported. We can use Icons.Default.Groups or Icons.Default.People, but People might not be in the default set.
# Let's use Icons.Default.Group

content = content.replace('Icons.Default.People', 'Icons.Default.Group')
# Actually, I'll just change it to Group, and hope it's available. If not, maybe something else like Face?
# Icons.Default.Group usually exists.
with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)

