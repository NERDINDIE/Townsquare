python3 -c "
import os
content = open('app/src/main/java/com/example/ui/screens/TvStreamingScreen.kt').read()
start_idx = content.find('@Composable\nprivate fun EpgGuideSection(')
end_tag = '}\n\n@Composable\nprivate fun TvOnDemandVaultSection('
end_idx = content.find(end_tag)

if start_idx != -1 and end_idx != -1:
    print('Found start and end')
else:
    print('Failed to find', start_idx, end_idx)
"
