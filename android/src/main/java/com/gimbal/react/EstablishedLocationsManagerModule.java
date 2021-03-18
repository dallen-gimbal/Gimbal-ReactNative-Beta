package com.gimbal.react;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.gimbal.android.Circle;
import com.gimbal.android.Coordinate;
import com.gimbal.android.EstablishedLocation;
import com.gimbal.android.EstablishedLocationsManager;

import java.util.List;

import javax.annotation.Nonnull;

public class EstablishedLocationsManagerModule extends ReactContextBaseJavaModule {
    private static final String LOCATION_MAP_KEY_SCORE = "score";
    private static final String LOCATION_MAP_KEY_RADIUS = "boundaryRadius";
    private static final String LOCATION_MAP_KEY_LATITUDE = "boundaryCenterLatitude";
    private static final String LOCATION_MAP_KEY_LONGITUDE = "boundaryCenterLongitude";

    public EstablishedLocationsManagerModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Nonnull
    @Override
    public String getName() {
        return "EstablishedLocationsManager";
    }

    @ReactMethod
    public void startMonitoring() {
        EstablishedLocationsManager.getInstance().startMonitoring();
    }

    @ReactMethod
    public void stopMonitoring() {
        EstablishedLocationsManager.getInstance().stopMonitoring();
    }

    @ReactMethod
    public void isMonitoring(Promise promise) {
        promise.resolve(EstablishedLocationsManager.getInstance().isMonitoring());
    }

    @ReactMethod
    public void getEstablishedLocations(Promise promise) {
        promise.resolve(toArray(EstablishedLocationsManager.getInstance().getEstablishedLocations()));
    }

    private static WritableArray toArray(List<EstablishedLocation> establishedLocations) {
        WritableArray array = Arguments.createArray();
        for (EstablishedLocation establishedLocation: establishedLocations) {
            array.pushMap(toMap(establishedLocation));
        }
        return array;
    }

    private static WritableMap toMap(EstablishedLocation establishedLocation) {
        Circle locationBoundary = establishedLocation.getBoundary();
        Coordinate locationCenter = locationBoundary.getCenter();
        WritableMap map = Arguments.createMap();
        map.putDouble(LOCATION_MAP_KEY_SCORE, establishedLocation.getScore());
        map.putDouble(LOCATION_MAP_KEY_RADIUS, locationBoundary.getRadius());
        map.putDouble(LOCATION_MAP_KEY_LATITUDE, locationCenter.getLatitude());
        map.putDouble(LOCATION_MAP_KEY_LONGITUDE, locationCenter.getLongitude());
        return map;
    }
}
