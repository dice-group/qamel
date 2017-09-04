# data extraction module
## general
This module will extract specific user data from the android device:
* current **location**, including latitude, longitude and orientation of the device
* the **calendar events** that take place in the next 36 hours
* all stored **contacts**

## how to use
    (1) dataExtraction = new dataExtraction(Context context); //This will initialise the extraction
    (2) dataExtraction.start(); //This will start location updates
    (3) dataExtraction.stop(); //This will stop location updates
    (4) JSONObject json = dataExtraction.getData(); //This will return the extracted user data as a JSON

## output
The in line (4) returned JSON has the following structure:

    {
    "lat":"",
    "long":"",
    "orient":"",
    "ical":"",
    "vcards":[Array of vCards]
    }

## notes
* The initialisation in line (1) will start the extraction of contact and calendar data which may take a while,
so I highly recommend to execute this at app start, using a seperate thread
* As location updates will drain the battery very fast, they can be manually turned on and off with lines (2) and (3)
* I recommend calling the start() void when the user starts typing his question and calling the stop() void after
getting the JSON in line (4)
* lat, long and orient are double values, ical a String and vcards an Array of Strings
    * orient is a value between -180 and 180. West: -90, North: 0, East: 90, South is either -180 or 180
