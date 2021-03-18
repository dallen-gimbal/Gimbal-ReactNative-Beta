package com.gimbal.react;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.gimbal.android.Circle;
import com.gimbal.android.Coordinate;
import com.gimbal.android.EstablishedLocation;
import com.gimbal.android.EstablishedLocationsManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EstablishedLocationsManagerModule.class, EstablishedLocationsManager.class, Arguments.class})
public class EstablishedLocationsManagerModuleTest {
    private EstablishedLocationsManagerModule module;

    @Mock private EstablishedLocationsManager locationsManager;
    @Mock private ReactApplicationContext reactContext;
    @Mock private Promise promise;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(EstablishedLocationsManager.class);
        PowerMockito.when(EstablishedLocationsManager.getInstance()).thenReturn(locationsManager);
        module = new EstablishedLocationsManagerModule(reactContext);

        PowerMockito.mockStatic(Arguments.class);
        PowerMockito.when(Arguments.createMap()).thenAnswer(invocation -> new JavaOnlyMap());
        PowerMockito.when(Arguments.createArray()).thenAnswer(invocation -> new JavaOnlyArray());
    }

    @Test
    public void hasCorrectName() {
        assertThat(module.getName(), is("EstablishedLocationsManager"));
    }

    @Test
    public void startMonitoring() {
        module.startMonitoring();
        verify(locationsManager).startMonitoring();
    }

    @Test
    public void stopMonitoring() {
        module.stopMonitoring();
        verify(locationsManager).stopMonitoring();
    }

    @Test
    public void isMonitoringTrue() {
        when(locationsManager.isMonitoring()).thenReturn(true);
        module.isMonitoring(promise);

        verify(promise).resolve(true);
    }

    @Test
    public void isMonitoringFalse() {
        when(locationsManager.isMonitoring()).thenReturn(false);
        module.isMonitoring(promise);

        verify(promise).resolve(false);
    }

    @Test
    public void locationsReturnAsMap() {
        List<EstablishedLocation> mockLocations = new ArrayList<>();
        mockLocations.add(this.createLocation());
        when(locationsManager.getEstablishedLocations()).thenReturn(mockLocations);

        WritableArray expectedMaps = Arguments.createArray();
        WritableMap map = Arguments.createMap();
        map.putDouble("score", 1.0);
        map.putDouble("boundaryRadius", 2.0);
        map.putDouble("boundaryCenterLatitude", 3.0);
        map.putDouble("boundaryCenterLongitude", 4.0);
        expectedMaps.pushMap(map);

        module.getEstablishedLocations(promise);

        verify(promise).resolve(expectedMaps);
    }

    @Test
    public void noLocationsReturnEmptyArray() {
        List<EstablishedLocation> mockLocations = new ArrayList<>();
        when(locationsManager.getEstablishedLocations()).thenReturn(mockLocations);

        WritableArray expectedMaps = Arguments.createArray();

        module.getEstablishedLocations(promise);
        verify(promise).resolve(expectedMaps);
    }

    private EstablishedLocation createLocation() {
        EstablishedLocation location = mock(EstablishedLocation.class);
        Circle boundary = mock(Circle.class);
        Coordinate center = mock(Coordinate.class);

        when(location.getScore()).thenReturn(1.0);
        when(location.getBoundary()).thenReturn(boundary);
        when(boundary.getRadius()).thenReturn(2.0);
        when(boundary.getCenter()).thenReturn(center);
        when(center.getLatitude()).thenReturn(3.0);
        when(center.getLongitude()).thenReturn(4.0);

        return location;
    }
}
