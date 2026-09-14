#!/bin/bash
FILE="app/src/main/java/com/example/ui/components/MediaCardItem.kt"
sed -i 's/onToggleSavedOffline: ((MediaItemEntity) -> Unit)? = null,/onToggleSavedOffline: ((MediaItemEntity) -> Unit)? = null,\n    onReportContent: ((MediaItemEntity) -> Unit)? = null,/g' $FILE
sed -i 's/onToggleSavedOffline: (() -> Unit)? = null,/onToggleSavedOffline: (() -> Unit)? = null,\n    onReportContent: (() -> Unit)? = null,/g' $FILE

# Pass it down in MediaCardItem switch
sed -i 's/onToggleSavedOffline = onToggleSavedOffline?.let { { it(item) } },/onToggleSavedOffline = onToggleSavedOffline?.let { { it(item) } },\n                onReportContent = onReportContent?.let { { it(item) } },/g' $FILE

# Pass it down in each Card component to MediaActionBar
sed -i 's/onToggleSavedOffline = onToggleSavedOffline/onToggleSavedOffline = onToggleSavedOffline,\n                onReportContent = onReportContent/g' $FILE
