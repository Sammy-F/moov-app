# Moov
Moov is a social media app designed to enable users to follow - and be followed by - other users with similar taste in movies. Users can make posts about their favorite films in order to let others know their thoughts.

## Purpose
Its primary goal is to provide people with a means of finding many casual reviews in one place, as well as a way to find movies they might like in a more person-to-person manner.

## Features
* Create an account in which to store your reviews\
* Change your password when your forget\
* Search for movies and users by String input\
* Make posts about movies via the movie search page, or by navigating to the movie's profile\
* Edit or delete existing posts\
* Follow and unfollow users, whose past posts will be added or removed to your Feed, respectively.\
* Set an avatar to personalize your profile

## Issues
* There's an awkward time lag between an Activity being created and the content of that Activity being loaded\
* Unable to swipe to refresh on profile pages

## Building
The project can be checked out from GitHub in Android Studio by going to VCS > Checkout From Version Control > GitHub. At this point, if you have made changes, you can go to Build > Rebuild Project which will produce an APK. Once the build is finished, you should go to the project's root directory > app > build > Outputs > Apk. In this location, there should be an app_debug.apk file. If you have not made changes, the original [app-debug.apk](/app/build/outputs/apk/debug/app-debug.apk) is available at that file location.