package com.gimbal.react;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.gimbal.android.Attributes;
import com.gimbal.android.Beacon;
import com.gimbal.android.BeaconSighting;
import com.gimbal.android.Place;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.Visit;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Looper.class, Handler.class, PlaceManagerModule.class, PlaceManager.class, Arguments.class})
@PowerMockIgnore("com.facebook.react.bridge.ReactBridge")
public class PlaceManagerModuleTest {

    private PlaceManagerModule module;
    private PlaceEventListener placeEventListener;

    @Mock PlaceManager placeManager;
    @Mock private ReactApplicationContext reactContext;
    @Mock private Looper looper;
    @Mock private Handler handler;
    @Mock private Promise promise;
    @Mock private DeviceEventManagerModule.RCTDeviceEventEmitter emitter;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Looper.class);
        PowerMockito.when(Looper.getMainLooper()).thenReturn(looper);
        PowerMockito.mockStatic(Handler.class);
        PowerMockito.whenNew(Handler.class).withAnyArguments().thenReturn(handler);
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return true;
        }).when(handler).post(any(Runnable.class));

        PowerMockito.mockStatic(PlaceManager.class);
        PowerMockito.when(PlaceManager.getInstance()).thenReturn(placeManager);
        doAnswer(invocation -> {
            placeEventListener = invocation.getArgument(0);
            return null;
        }).when(placeManager).addListener(any(PlaceEventListener.class));

        module = new PlaceManagerModule(reactContext);

        when(reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class))
                .thenReturn(emitter);
        PowerMockito.when(reactContext.hasActiveCatalystInstance())
                .thenReturn(true);
        PowerMockito.mockStatic(Arguments.class);
        PowerMockito.when(Arguments.createMap()).thenAnswer(invocation -> new JavaOnlyMap());
        PowerMockito.when(Arguments.createArray()).thenAnswer(invocation -> new JavaOnlyArray());
    }

    @Test
    public void hasCorrectName() {
        assertThat(module.getName(), is("PlaceManager"));
    }

    @Test
    public void startMonitoring() {
        module.startMonitoring();

        verify(placeManager).startMonitoring();
    }

    @Test
    public void stopMonitoring() {
        module.stopMonitoring();

        verify(placeManager).stopMonitoring();
    }

    @Test
    public void isMonitoringTrue() {
        when(placeManager.isMonitoring()).thenReturn(true);
        module.isMonitoring(promise);

        verify(promise).resolve(true);
    }

    @Test
    public void isMonitoringFalse() {
        when(placeManager.isMonitoring()).thenReturn(false);
        module.isMonitoring(promise);

        verify(promise).resolve(false);
    }

    @Test
    public void getConstants() {
        Map<String, Object> constants = module.getConstants();

        assertThat(constants, Matchers.aMapWithSize(5));
        assertThat(constants, hasKeyValuePair("BATTERY_LEVEL_LOW", Beacon.BatteryLevel.LOW.ordinal()));
        assertThat(constants, hasKeyValuePair("BATTERY_LEVEL_MED_LOW", Beacon.BatteryLevel.MEDIUM_LOW.ordinal()));
        assertThat(constants, hasKeyValuePair("BATTERY_LEVEL_MED_HIGH", Beacon.BatteryLevel.MEDIUM_HIGH.ordinal()));
        assertThat(constants, hasKeyValuePair("BATTERY_LEVEL_HIGH", Beacon.BatteryLevel.HIGH.ordinal()));
        assertThat(constants, hasKeyValuePair("BEACON_SIGHTING", "BeaconSightingFromPlaceManager"));
    }

    @Test
    public void hasConstants() {
        assertTrue(module.hasConstants());
    }

    @Test
    public void visitStartEmitsVisitAsWritableMap() {
        placeEventListener.onVisitStart(createVisit(true));

        ArgumentCaptor<WritableMap> mapCaptor = ArgumentCaptor.forClass(WritableMap.class);
        verify(emitter).emit(eq("VisitStart"), mapCaptor.capture());

        WritableMap visitMap = mapCaptor.getValue();
        assertThat(visitMap.getDouble("arrivalTimeInMillis"),
                Matchers.closeTo(123456L, 0.1));
        assertThat(visitMap.getDouble("departureTimeInMillis"),
                Matchers.closeTo(234567L, 0.1));
        assertThat(visitMap.getDouble("dwellTimeInMillis"),
                Matchers.closeTo(234567L - 123456L, 0.1));
        assertThat(visitMap.getString("visitId"), is("VISIT_ID"));

        ReadableMap placeMap = visitMap.getMap("place");
        assertNotNull(placeMap);
        assertThat(placeMap.getString("identifier"), is("PLACE_ID"));
        assertThat(placeMap.getString("name"), is("PLACE_NAME"));

        ReadableMap attributesMap = placeMap.getMap("attributes");
        assertNotNull(attributesMap);
        assertThat(attributesMap.toHashMap(), Matchers.aMapWithSize(2));
        assertThat(attributesMap.getString("KEY1"), is("VALUE1"));
        assertThat(attributesMap.getString("KEY2"), is("VALUE2"));
    }

    @Test
    public void visitStartWithDelayEmitsVisitAsWritableMap() {
        placeEventListener.onVisitStartWithDelay(createVisit(false), 30);

        ArgumentCaptor<WritableMap> mapCaptor = ArgumentCaptor.forClass(WritableMap.class);
        verify(emitter).emit(eq("VisitStartWithDelay"), mapCaptor.capture());

        WritableMap visitMap = mapCaptor.getValue();
        assertThat(visitMap.getDouble("arrivalTimeInMillis"),
                Matchers.closeTo(123456L, 0.1));
        assertThat(visitMap.getDouble("departureTimeInMillis"),
                Matchers.closeTo(234567L, 0.1));
        assertThat(visitMap.getDouble("dwellTimeInMillis"),
                Matchers.closeTo(234567L - 123456L, 0.1));
        assertThat(visitMap.getString("visitId"), is("VISIT_ID"));
        assertThat(visitMap.getInt("delay"), is(30));

        ReadableMap placeMap = visitMap.getMap("place");
        assertNotNull(placeMap);
        assertThat(placeMap.getString("identifier"), is("PLACE_ID"));
        assertThat(placeMap.getString("name"), is("PLACE_NAME"));

        ReadableMap attributesMap = placeMap.getMap("attributes");
        assertNotNull(attributesMap);
        assertThat(attributesMap.toHashMap(), Matchers.anEmptyMap());
    }

    @Test
    public void visitEndEmitsVisitAsWritableMap() {
        placeEventListener.onVisitEnd(createVisit(true));

        ArgumentCaptor<WritableMap> mapCaptor = ArgumentCaptor.forClass(WritableMap.class);
        verify(emitter).emit(eq("VisitEnd"), mapCaptor.capture());

        WritableMap visitMap = mapCaptor.getValue();
        assertThat(visitMap.getDouble("arrivalTimeInMillis"),
                Matchers.closeTo(123456L, 0.1));
        assertThat(visitMap.getDouble("departureTimeInMillis"),
                Matchers.closeTo(234567L, 0.1));
        assertThat(visitMap.getDouble("dwellTimeInMillis"),
                Matchers.closeTo(234567L - 123456L, 0.1));
        assertThat(visitMap.getString("visitId"), is("VISIT_ID"));

        ReadableMap placeMap = visitMap.getMap("place");
        assertNotNull(placeMap);
        assertThat(placeMap.getString("identifier"), is("PLACE_ID"));
        assertThat(placeMap.getString("name"), is("PLACE_NAME"));

        ReadableMap attributesMap = placeMap.getMap("attributes");
        assertNotNull(attributesMap);
        assertThat(attributesMap.toHashMap(), Matchers.aMapWithSize(2));
        assertThat(attributesMap.getString("KEY1"), is("VALUE1"));
        assertThat(attributesMap.getString("KEY2"), is("VALUE2"));
    }

    @Test
    public void locationDetectedEmitsLocationAsMap() {
        placeEventListener.locationDetected(createLocation());

        ArgumentCaptor<WritableMap> mapCaptor = ArgumentCaptor.forClass(WritableMap.class);
        verify(emitter).emit(eq("LocationDetected"), mapCaptor.capture());

        WritableMap location = mapCaptor.getValue();
        assertThat(location.getDouble("latitude"), is(38.0));
        assertThat(location.getDouble("longitude"), is(-117.0));
        assertThat(location.getDouble("accuracy"), is(25.0));
    }

    @Test
    public void sightingEmitsSightingAsMap() {
        placeEventListener.onBeaconSighting(
                createSighting(),
                Collections.singletonList(createVisit(false)));

        ArgumentCaptor<WritableMap> mapCaptor = ArgumentCaptor.forClass(WritableMap.class);
        verify(emitter).emit(eq("BeaconSightingFromPlaceManager"), mapCaptor.capture());

        WritableMap eventMap = mapCaptor.getValue();

        ReadableMap sightingMap = eventMap.getMap("beaconSighting");
        assertNotNull(sightingMap);
        assertThat(sightingMap.getInt("rssi"), is(-50));
        assertThat(sightingMap.getDouble("timeInMillis"), is(1234567.0));

        ReadableMap beaconMap = sightingMap.getMap("beacon");
        assertNotNull(beaconMap);
        assertThat(beaconMap.getString("identifier"), is("BEACON_ID"));
        assertThat(beaconMap.getString("uuid"), is("UUID"));
        assertThat(beaconMap.getString("name"), is("BEACON_NAME"));
        assertThat(beaconMap.getString("iconURL"), is("ICON_URL"));
        assertThat(beaconMap.getInt("batteryLevel"), is(Beacon.BatteryLevel.MEDIUM_LOW.ordinal()));
        assertThat(beaconMap.getInt("temperature"), is(71));

        ReadableArray visitArray = eventMap.getArray("visits");
        assertNotNull(visitArray);
        assertThat(visitArray.size(), is(1));
        ReadableMap visitMap = visitArray.getMap(0);
        assertNotNull(visitMap);
        assertThat(visitMap.getString("visitId"), is("VISIT_ID"));
    }

    private Visit createVisit(boolean withAttributes) {
        Visit visit = mock(Visit.class);
        Place place = mock(Place.class);
        Attributes attributes = mock(Attributes.class);

        when(visit.getArrivalTimeInMillis()).thenReturn(123456L);
        when(visit.getDepartureTimeInMillis()).thenReturn(234567L);
        when(visit.getDwellTimeInMillis()).thenReturn(234567L - 123456L);
        when(visit.getVisitID()).thenReturn("VISIT_ID");
        when(visit.getPlace()).thenReturn(place);

        when(place.getIdentifier()).thenReturn("PLACE_ID");
        when(place.getName()).thenReturn("PLACE_NAME");
        if (withAttributes) {
            when(place.getAttributes()).thenReturn(attributes);

            when(attributes.getAllKeys()).thenReturn(Arrays.asList("KEY1", "KEY2"));
            when(attributes.getValue("KEY1")).thenReturn("VALUE1");
            when(attributes.getValue("KEY2")).thenReturn("VALUE2");
        }
        return visit;
    }

    private BeaconSighting createSighting() {
        BeaconSighting sighting = mock(BeaconSighting.class);
        Beacon beacon = mock(Beacon.class);

        when(sighting.getRSSI()).thenReturn(-50);
        when(sighting.getTimeInMillis()).thenReturn(1234567L);
        when(sighting.getBeacon()).thenReturn(beacon);

        when(beacon.getBatteryLevel()).thenReturn(Beacon.BatteryLevel.MEDIUM_LOW);
        when(beacon.getIconURL()).thenReturn("ICON_URL");
        when(beacon.getIdentifier()).thenReturn("BEACON_ID");
        when(beacon.getName()).thenReturn("BEACON_NAME");
        when(beacon.getTemperature()).thenReturn(71);
        when(beacon.getUuid()).thenReturn("UUID");

        return sighting;
    }

    private Location createLocation() {
        Location location = mock(Location.class);

        when(location.getLatitude()).thenReturn(38.0);
        when(location.getLongitude()).thenReturn(-117.0);
        when(location.getAccuracy()).thenReturn(25.0f);
        return location;
    }

    private static <K, V> Matcher<Map<K, V>> hasKeyValuePair(@Nonnull K key, @Nonnull V value) {
        return new MapKeyValueMatcher<>(key, value);
    }
}
