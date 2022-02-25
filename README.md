# Moov
Moov is a social media app designed to enable users to follow - and be followed by - other users with
 similar taste in movies. Users can make posts about their favorite films in order to let others know
 their thoughts.

Go to [Moov Keynote](https://drive.google.com/file/d/1PFlDakg-aQtGsjLeimEGKtRKi5oZHudv/view?usp=sharing).

## Purpose
Its primary goal is to provide people with a means of finding many casual reviews in one place, as
 well as a way to find movies they might like in a more person-to-person manner.

## Features
* Create an account in which to store your reviews
* Change your password when your forget
* Search for movies and users by String input
* Make posts about movies via the movie search page, or by navigating to the movie's profile
* Edit or delete existing posts
* Follow and unfollow users, whose past posts will be added or removed to your Feed, respectively.
* Set an avatar to personalize your profile

## Issues
* There's an awkward time lag between an Activity being created and the content of that Activity being loaded\
* Unable to swipe to refresh on profile pages

## Building
The project can be checked out from GitHub in Android Studio by going to VCS > Checkout From Version
 Control > GitHub. At this point, you can go to Build > Rebuild Project which will produce an APK.
 Once the build is finished, you should go to the project's root directory > app > build > Outputs >
  Apk. In this location, there should be an app_debug.apk file. This file can be installed on an
  Android phone and run as long as non-Play Store app installations are authorized.

  Alternatively, you can run the app in a virtual emulator in Android Studio. Once Gradle has finished
  building (can be done manually from Tools > Android > Sync Project with Gradle Files), you can press
  the green "Run" button in the upport right corner of Android Studio. This should bring up a
  "Select Deployment Target" window. Add the bottom, there is a "Create New Virtual Device" button.
  You should click this, select a phone from the list that DOES have an icon in the Play Store column,
  and select a System Image that is compatible with Google Play. You can name the device and update any
  other settings, then hit "Finish."

  Once the emulator loads, you will likely need to 1) sign in to a valid Google account and
  2) update Play Services via the app store. At this point, you should be able to open and use
  Moov, which should have been installed during the emulator's setup process.
