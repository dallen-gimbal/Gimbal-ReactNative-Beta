package com.gimbal.react;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactApplicationContext;

import com.gimbal.android.GimbalDebugger;

import javax.annotation.Nonnull;

public class GimbalDebuggerModule extends ReactContextBaseJavaModule {

  public GimbalDebuggerModule(@Nonnull ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Nonnull
  @Override
  public String getName() {
      return "GimbalDebugger";
  }

  @ReactMethod
  public void disableBeaconSightingsLogging() {
    GimbalDebugger.disableBeaconSightingsLogging();
  }

  @ReactMethod
  public void disablePlaceLogging() {
    GimbalDebugger.disablePlaceLogging();
  }

  @ReactMethod
  public void disableDebugLogging() {
    GimbalDebugger.disableStatusLogging();
  }

  @ReactMethod
  public void enableBeaconSightingsLogging() {
    GimbalDebugger.enableBeaconSightingsLogging();
  }

  @ReactMethod
  public void enablePlaceLogging() {
    GimbalDebugger.enablePlaceLogging();
  }

  @ReactMethod
  public void enableDebugLogging() {
    GimbalDebugger.enableStatusLogging();
  }

  @ReactMethod
  public void isBeaconSightingsLoggingEnabled(Promise promise) {
    promise.resolve(GimbalDebugger.isBeaconSightingsLoggingEnabled());
  }

  @ReactMethod
  public void isPlaceLoggingEnabled(Promise promise) {
    promise.resolve(GimbalDebugger.isPlaceLoggingEnabled());
  }

  @ReactMethod
  public void isDebugLoggingEnabled(Promise promise) {
    promise.resolve(GimbalDebugger.isStatusLoggingEnabled());
  }
}
