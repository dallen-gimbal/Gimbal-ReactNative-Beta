package com.gimbal.react;

import android.os.Handler;
import android.os.Looper;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest({BridgeUtilities.class, Looper.class, Handler.class})
@RunWith(PowerMockRunner.class)
public class BridgeUtilitiesTest {

    @Mock private ReactContext reactContext;
    @Mock private DeviceEventManagerModule.RCTDeviceEventEmitter emitter;
    @Mock private WritableMap writableMap;
    @Mock private Looper looper;
    @Mock private Handler handler;

    @Before
    public void setup() throws Exception {
        when(reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class))
                .thenReturn(emitter);
        PowerMockito.when(reactContext.hasActiveCatalystInstance())
                .thenReturn(true);
        mockStatic(Looper.class);
        PowerMockito.when(Looper.getMainLooper()).thenReturn(looper);
        mockStatic(Handler.class);
        PowerMockito.whenNew(Handler.class).withAnyArguments().thenReturn(handler);
        Mockito.doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return true;
        }).when(handler).postDelayed(any(Runnable.class), anyLong());
    }

    @Test
    public void fromJSValueGetsCorrectEnumFromOrdinal() {
        assertExpectedEnumFromOrdinalJSValue(TestEnum.class, TestEnum.ZERO.ordinal(), TestEnum.ZERO);
        assertExpectedEnumFromOrdinalJSValue(TestEnum.class, TestEnum.ONE.ordinal(), TestEnum.ONE);
        assertExpectedEnumFromOrdinalJSValue(TestEnum.class, TestEnum.TWO.ordinal(), TestEnum.TWO);
        assertExpectedEnumFromOrdinalJSValue(TestEnum.class, TestEnum.THREE.ordinal(), TestEnum.THREE);
    }

    @SuppressWarnings("SameParameterValue")
    private <E extends Enum<E>> void assertExpectedEnumFromOrdinalJSValue(
            Class<E> type, int ordinal, E expectedEnum) {

        E convertedValue = BridgeUtilities.fromJSValue(type, ordinal);
        assertThat(convertedValue, is(expectedEnum));
    }

    @Test()
    public void fromJSValueWithUnknownOrdinalIsNull() {
        assertThat(BridgeUtilities.fromJSValue(TestEnum.class, 5), Matchers.nullValue());
    }

    @Test
    public void toJSValueOfEnumIsEnumOrdinal() {
        assertThat(BridgeUtilities.toJSValue(TestEnum.ZERO), is(TestEnum.ZERO.ordinal()));
        assertThat(BridgeUtilities.toJSValue(TestEnum.ONE), is(TestEnum.ONE.ordinal()));
        assertThat(BridgeUtilities.toJSValue(TestEnum.TWO), is(TestEnum.TWO.ordinal()));
        assertThat(BridgeUtilities.toJSValue(TestEnum.THREE), is(TestEnum.THREE.ordinal()));
    }

    @Test
    public void toJSValuePassesNonEnum() {
        Object value = new Object();
        assertThat(BridgeUtilities.toJSValue(value), is(value));
    }

    @Test
    public void toJSValueOfEnumAsObjectIsEnumOrdinal() {
        Object value = TestEnum.ONE;
        assertThat(BridgeUtilities.toJSValue(value), is(TestEnum.ONE.ordinal()));
    }

    @Test
    public void sendEventEmitsSuppliedDataWhenContextAvailable() {
        BridgeUtilities.sendEvent(reactContext, "TestEvent", writableMap);

        verify(emitter).emit("TestEvent", writableMap);
    }

    @Test
    public void sendEventEmitsSuppliedDataWhenContextEventuallyAvailable() {
        PowerMockito.when(reactContext.hasActiveCatalystInstance())
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(true);

        BridgeUtilities.sendEvent(reactContext, "TestEvent", writableMap);
        verify(emitter).emit("TestEvent", writableMap);
    }

    @Test
    public void sendEventDoesNotEmitWhenContextUnavailable() {
        PowerMockito.when(reactContext.hasActiveCatalystInstance())
                .thenReturn(false);

        BridgeUtilities.sendEvent(reactContext, "TestEvent", writableMap);
        verify(emitter, never()).emit(anyString(), any(WritableMap.class));
    }

    enum TestEnum {
        ZERO,
        ONE,
        TWO,
        THREE
    }
}
