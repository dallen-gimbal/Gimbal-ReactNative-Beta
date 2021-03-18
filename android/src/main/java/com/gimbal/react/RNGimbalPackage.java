package com.gimbal.react;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

public class RNGimbalPackage implements ReactPackage {

    @Nonnull
    @Override
    public List<NativeModule> createNativeModules(@Nonnull ReactApplicationContext reactContext) {
        return Arrays.<NativeModule>asList(
                new RNGimbalModule(reactContext),
                new PrivacyManagerModule(reactContext),
                new PlaceManagerModule(reactContext),
                new GimbalDebuggerModule(reactContext),
                new BeaconManagerModule(reactContext),
                new EstablishedLocationsManagerModule(reactContext)
        );
    }

    @Nonnull
    @Override
    public List<ViewManager> createViewManagers(@Nonnull ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
}
