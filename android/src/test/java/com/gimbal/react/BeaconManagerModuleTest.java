package com.gimbal.react;

import android.os.Handler;
import android.os.Looper;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.gimbal.android.Beacon;
import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.BeaconManager;
import com.gimbal.android.BeaconSighting;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Map;

import javax.annotation.Nonnull;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Looper.class, Handler.class, BeaconManagerModule.class, BeaconManager.class, Arguments.class})
@PowerMockIgnore("com.facebook.react.bridge.ReactBridge")
public class BeaconManagerModuleTest {
    private BeaconManagerModule module;
    private BeaconEventListener listener;

    @Mock BeaconManager beaconManager;
    @Mock private ReactApplicationContext reactContext;
    @Mock private Looper looper;
    @Mock private Handler handler;
    @Mock private DeviceEventManagerModule.RCTDeviceEventEmitter emitter;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Looper.class);
        when(Looper.getMainLooper()).thenReturn(looper);
        PowerMockito.mockStatic(Handler.class);
        PowerMockito.whenNew(Handler.class).withAnyArguments().thenReturn(handler);
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return true;
        }).when(handler).post(any(Runnable.class));

        PowerMockito.mockStatic(BeaconManager.class);
        PowerMockito.whenNew(BeaconManager.class).withNoArguments().thenReturn(beaconManager);
        doAnswer(invocation -> {
            listener = invocation.getArgument(0);
            return null;
        }).when(beaconManager).addListener(any(BeaconEventListener.class));

        module = new BeaconManagerModule(reactContext);

        when(reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class))
                .thenReturn(emitter);
        when(reactContext.hasActiveCatalystInstance())
                .thenReturn(true);
        PowerMockito.mockStatic(Arguments.class);
        PowerMockito.when(Arguments.createMap()).thenAnswer(invocation -> new JavaOnlyMap());
        PowerMockito.when(Arguments.createArray()).thenAnswer(invocation -> new JavaOnlyArray());
    }

    @Test
    public void hasCorrectName() {
        assertThat(module.getName(), is("BeaconManager"));
    }

    @Test
    public void getConstants() {
        Map<String, Object> constants = module.getConstants();

        assertThat(constants, Matchers.aMapWithSize(1));
        assertThat(constants, hasKeyValuePair("BEACON_SIGHTING", "BeaconSightingFromBeaconManager"));
    }

    @Test
    public void startListening() {
        module.startListening();

        verify(beaconManager).startListening();
    }

    @Test
    public void stopListening() {
        module.stopListening();

        verify(beaconManager).stopListening();
    }

    @Test
    public void beaconSightingEmitsSightingAsWritableMap() {
        listener.onBeaconSighting(createSighting());

        ArgumentCaptor<WritableMap> mapCaptor = ArgumentCaptor.forClass(WritableMap.class);
        verify(emitter).emit(eq("BeaconSightingFromBeaconManager"), mapCaptor.capture());

        WritableMap sightingMap = mapCaptor.getValue();
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
    }

    private BeaconSighting createSighting() {
        BeaconSighting sighting = mock(BeaconSighting.class);
        Beacon beacon = mock(Beacon.class);

        Mockito.when(sighting.getRSSI()).thenReturn(-50);
        Mockito.when(sighting.getTimeInMillis()).thenReturn(1234567L);
        Mockito.when(sighting.getBeacon()).thenReturn(beacon);

        Mockito.when(beacon.getBatteryLevel()).thenReturn(Beacon.BatteryLevel.MEDIUM_LOW);
        Mockito.when(beacon.getIconURL()).thenReturn("ICON_URL");
        Mockito.when(beacon.getIdentifier()).thenReturn("BEACON_ID");
        Mockito.when(beacon.getName()).thenReturn("BEACON_NAME");
        Mockito.when(beacon.getTemperature()).thenReturn(71);
        Mockito.when(beacon.getUuid()).thenReturn("UUID");

        return sighting;
    }

    private static <K, V> Matcher<Map<K, V>> hasKeyValuePair(@Nonnull K key, @Nonnull V value) {
        return new MapKeyValueMatcher<>(key, value);
    }
}
