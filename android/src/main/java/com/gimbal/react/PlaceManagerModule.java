package com.gimbal.react;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.gimbal.android.Attributes;
import com.gimbal.android.Beacon.BatteryLevel;
import com.gimbal.android.BeaconSighting;
import com.gimbal.android.Place;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.Visit;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlaceManagerModule extends ReactContextBaseJavaModule {

    private static final String BATTERY_LEVEL_LOW = "BATTERY_LEVEL_LOW";
    private static final String BATTERY_LEVEL_MED_LOW = "BATTERY_LEVEL_MED_LOW";
    private static final String BATTERY_LEVEL_MED_HIGH = "BATTERY_LEVEL_MED_HIGH";
    private static final String BATTERY_LEVEL_HIGH = "BATTERY_LEVEL_HIGH";
    private static final String VISIT_START_WITH_DELAY = "VisitStartWithDelay";
    private static final String VISIT_END = "VisitEnd";
    private static final String VISIT_START = "VisitStart";
    private static final String BEACON_SIGHTING = "BeaconSightingFromPlaceManager";
    private static final String BEACON_SIGHTING_KEY = "BEACON_SIGHTING";
    private static final String LOCATION_DETECTED = "LocationDetected";

    PlaceManagerModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
        addPlaceEventListenerOnMainThread();
    }

    private void addPlaceEventListenerOnMainThread() {
        new Handler(Looper.getMainLooper()).post(() ->
                PlaceManager.getInstance().addListener(forwarder));
    }

    @Nonnull
    @Override
    public String getName() {
        return "PlaceManager";
    }

    @ReactMethod
    public void startMonitoring() {
        PlaceManager.getInstance().startMonitoring();
    }

    @ReactMethod
    public void stopMonitoring() {
        PlaceManager.getInstance().stopMonitoring();
    }

    @ReactMethod
    public void isMonitoring(Promise promise) {
        promise.resolve(PlaceManager.getInstance().isMonitoring());
    }
    
    @ReactMethod
    public void getCurrentVisits() {
        List<Visit> visits = PlaceManager.getInstance().currentVisits()
        sendEvent("CurrentVisits", toArray(visits));
    }

    @Override
    public Map<String, Object> getConstants() {
        return new ConstantsBuilder()
                .add(BATTERY_LEVEL_LOW, BatteryLevel.LOW)
                .add(BATTERY_LEVEL_MED_LOW, BatteryLevel.MEDIUM_LOW)
                .add(BATTERY_LEVEL_MED_HIGH, BatteryLevel.MEDIUM_HIGH)
                .add(BATTERY_LEVEL_HIGH, BatteryLevel.HIGH)
                .add(BEACON_SIGHTING_KEY, BEACON_SIGHTING)
                .build();
    }

    @Override
    public boolean hasConstants() {
        return true;
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final PlaceEventListener forwarder = new PlaceEventListener() {
        @Override
        public void onVisitStart(Visit visit) {
            sendEvent(VISIT_START, toMap(visit));
        }

        @Override
        public void onVisitStartWithDelay(Visit visit, int i) {
            WritableMap visitWithDelay = toMap(visit);
            visitWithDelay.putInt("delay", i);
            sendEvent(VISIT_START_WITH_DELAY, visitWithDelay);
        }

        @Override
        public void onVisitEnd(Visit visit) {
            sendEvent(VISIT_END, toMap(visit));
        }

        @Override
        public void onBeaconSighting(BeaconSighting beaconSighting, List<Visit> list) {
            sendEvent(BEACON_SIGHTING, toMap(beaconSighting, list));
        }

        @Override
        public void locationDetected(Location location) {
            sendEvent(LOCATION_DETECTED, toMap(location));
        }

        private void sendEvent(@Nonnull String eventName, @Nullable WritableMap data) {
            BridgeUtilities.sendEvent(getReactApplicationContext(), eventName, data);
        }
    };

    private static WritableMap toMap(Visit visit) {
        WritableMap map = Arguments.createMap();
        map.putMap("place", toMap(visit.getPlace()));
        map.putString("visitId", visit.getVisitID());
        map.putDouble("arrivalTimeInMillis", visit.getArrivalTimeInMillis());
        map.putDouble("departureTimeInMillis", visit.getDepartureTimeInMillis());
        map.putDouble("dwellTimeInMillis", visit.getDwellTimeInMillis());
        return map;
    }

    private static WritableMap toMap(Place place) {
        WritableMap map = Arguments.createMap();
        map.putString("identifier", place.getIdentifier());
        map.putString("name", place.getName());
        map.putMap("attributes", toMap(place.getAttributes()));
        return map;
    }

    private static WritableMap toMap(Attributes attributes) {
        WritableMap map = Arguments.createMap();
        if (attributes != null) {
            for (String key : attributes.getAllKeys()) {
                map.putString(key, attributes.getValue(key));
            }
        }
        return map;
    }

    private static WritableMap toMap(Location location) {
        WritableMap map = Arguments.createMap();
        map.putDouble("latitude", location.getLatitude());
        map.putDouble("longitude", location.getLongitude());
        map.putDouble("accuracy", location.getAccuracy());
        return map;
    }

    private static WritableMap toMap(BeaconSighting beaconSighting, List<Visit> visits) {
        WritableMap map = Arguments.createMap();
        map.putMap("beaconSighting", BeaconUtilities.toMap(beaconSighting));
        map.putArray("visits", toArray(visits));
        return map;
    }

    private static WritableArray toArray(List<Visit> visits) {
        WritableArray array = Arguments.createArray();
        for (Visit visit: visits) {
            array.pushMap(toMap(visit));
        }
        return array;
    }
}
