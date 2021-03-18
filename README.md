# react-native-gimbal

## Installation
Clone `github.com/PaeDae/react-native-gimbal-sdk`  
Add library to your React-Native project: `yarn add ../react-native-gimbal-sdk`  
If you are using `create-react-app`, you must eject the app to expose native code.  
If you are using Expo, you must run `exp detach` to expose native code.

### Android:
In `MainApplication.java`, add:
1. `import com.gimbal.android.Gimbal;`
1. In `MainApplication` class
    `private static final String GIMBAL_APP_API_KEY = "YOUR GIMBAL API KEY HERE";`
1. In `onCreate()` method
    `Gimbal.setApiKey(this, GIMBAL_APP_API_KEY);`

### iOS:
In `AppDelegate.m`, add:
1. `#import <Gimbal/Gimbal.h>`
1. In `didFinishLaunchingWithOptions` method:
    `[Gimbal setAPIKey:@"YOUR GIMBAL API KEY HERE" options:nil];`

Extra steps needed on React Native v0.60+:  
`cd ios && pod install && cd ..`

### To run the app:
* Android: `yarn react-native run-android` or `npx react-native run-android`
* iOS: `yarn react-native run-ios` or `npx react-native run-ios`
    
### Troubleshoot
If you encounter this error: `Unsupported class file major version 57.`  
It could be that JDK version 13 does not work well with React Native 0.60+. Use JDK 11.

----

## API

### `Gimbal`

```js
// start the Gimbal module:
Gimbal.start();

// check status:
Gimbal.isStarted();

// get the application ID:
Gimbal.getApplicationInstanceIdentifier();

// stop the Gimbal SDK:
Gimbal.stop();
```


### `PrivacyManager`

```js
PrivacyManager.getGdprConsentRequirement();

PrivacyManager.setUserConsent(PrivacyManager.GDPR_CONSENT_TYPE_PLACES, stateValue);

PrivacyManager.getUserConsent(PrivacyManager.GDPR_CONSENT_TYPE_PLACES);
```


### `PlaceManager`

```js
PlaceManager.startMonitoring();

PlaceManager.stopMonitoring();

PlaceManager.isMonitoring();

// event types:
'VisitStart'
'VisitStartWithDelay'
'VisitEnd'
'BeaconSighting'
'LocationDetected'
```


### `GimbalDebugger`

```js
GimbalDebugger.enableBeaconSightingsLogging();
GimbalDebugger.disableBeaconSightingsLogging();
GimbalDebugger.isBeaconSightingsLoggingEnabled();

GimbalDebugger.enableDebugLogging();
GimbalDebugger.disableDebugLogging();
GimbalDebugger.isDebugLoggingEnabled();

GimbalDebugger.enablePlaceLogging();
GimbalDebugger.disablePlaceLogging();
GimbalDebugger.isPlaceLoggingEnabled();
```
