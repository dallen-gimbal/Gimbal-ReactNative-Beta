package com.gimbal.react;

import android.os.Handler;
import android.os.Looper;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.BeaconManager;
import com.gimbal.android.BeaconSighting;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BeaconManagerModule extends ReactContextBaseJavaModule {
    private static final String BEACON_SIGHTING = "BeaconSightingFromBeaconManager";
    private static final String BEACON_SIGHTING_KEY = "BEACON_SIGHTING";

    private BeaconManager beaconManager;

    BeaconManagerModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
        this.beaconManager = new BeaconManager();
        addBeaconEventListenerOnMainThread();
    }

    private void addBeaconEventListenerOnMainThread() {
        new Handler(Looper.getMainLooper()).post(() ->
                this.beaconManager.addListener(forwarder));
    }

    @Nonnull
    @Override
    public String getName() {
        return "BeaconManager";
    }

    @ReactMethod
    public void startListening() {
        beaconManager.startListening();
    }

    @ReactMethod
    public void stopListening() {
        beaconManager.stopListening();
    }

    @Override
    public Map<String, Object> getConstants() {
        return new ConstantsBuilder()
                .add(BEACON_SIGHTING_KEY, BEACON_SIGHTING)
                .build();
    }

    @Override
    public boolean hasConstants() {
        return true;
    }

    private final BeaconEventListener forwarder = new BeaconEventListener() {
        @Override
        public void onBeaconSighting(BeaconSighting beaconSighting) {
            super.onBeaconSighting(beaconSighting);
            sendEvent(BEACON_SIGHTING, BeaconUtilities.toMap(beaconSighting));
        }

        private void sendEvent(@Nonnull String eventName, @Nullable WritableMap data) {
            BridgeUtilities.sendEvent(getReactApplicationContext(), eventName, data);
        }
    };
}
