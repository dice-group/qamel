# QAMEL Offline data synchronizer

A modul which download your offline data and keeps them up-to-date.

### Setup

Copy the module directory into your Android app project.
In your top-level build.gradle, insert:
```
depencencies {
    compile project(':offlinedatasynchronizer')
    ...
}
```

On a public server, create a JSON file with an array as top-level element.
Whenever you release updated offline data, insert a JSON object of this structure at the first position:
```
  {
    "revision": "<revision of .tar.gz (to be found in offline_data/revision>",
    "timestamp": <timestamp in millis>,
    "size": <file size in bytes>,
    "md5": "<md5 of your offline data .tar.gz>",
    "url": "<url of the .tar.gz>"
  }
```

Insert the url of the JSON file in values/config.xml > config_jsonUri

### Usage

When your app starts, use this to check for available updates.

```java
//This broadcast receiver is called when the update has finished or failed
BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(OfflineDataManager.ACTION_UPDATE_COMPLETED)) {
            ...
        } else if (intent.getAction().equals(OfflineDataManager.ACTION_UPDATE_FAILED)) {
            ...
        }
    }
}
IntentFilter filter = new IntentFilter();
filter.addAction(OfflineDataManager.ACTION_UPDATE_COMPLETED);
filter.addAction(OfflineDataManager.ACTION_UPDATE_FAILED);
registerReceiver(receiver, filter);
//Checks for an update and downloads it if necessary
new OfflineDataManager(context).update();
```

Never call this while your dataset is locked! It might corrupt your database.
