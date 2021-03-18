package com.gimbal.react;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.gimbal.android.Gimbal;

import javax.annotation.Nonnull;

public class RNGimbalModule extends ReactContextBaseJavaModule {

    public RNGimbalModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    @Nonnull
    public String getName() {
        return "RNGimbal";
    }

    @ReactMethod
    public void start() {
        Gimbal.start();
    }

    @ReactMethod
    public void isStarted(Promise promise) {
        promise.resolve(Gimbal.isStarted());
    }

    @ReactMethod
    public void stop() {
        Gimbal.stop();
    }

    @ReactMethod
    public void getApplicationInstanceIdentifier(Promise promise) {
        promise.resolve(Gimbal.getApplicationInstanceIdentifier());
    }

    @ReactMethod
    public void resetApplicationInstanceIdentifier() {
        Gimbal.resetApplicationInstanceIdentifier();
    }
}
