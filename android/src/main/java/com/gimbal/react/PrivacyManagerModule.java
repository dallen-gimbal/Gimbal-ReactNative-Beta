package com.gimbal.react;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.gimbal.android.PrivacyManager;
import com.gimbal.android.PrivacyManager.ConsentState;
import com.gimbal.android.PrivacyManager.ConsentType;
import com.gimbal.android.PrivacyManager.GdprConsentRequirement;

import java.util.Map;

import javax.annotation.Nonnull;

import static com.gimbal.react.BridgeUtilities.fromJSValue;
import static com.gimbal.react.BridgeUtilities.toJSValue;

public class PrivacyManagerModule extends ReactContextBaseJavaModule {

    private static final String GDPR_CONSENT_NOT_REQUIRED = "GDPR_CONSENT_NOT_REQUIRED";
    private static final String GDPR_CONSENT_REQUIRED = "GDPR_CONSENT_REQUIRED";
    private static final String GDPR_CONSENT_REQUIREMENT_UNKNOWN = "GDPR_CONSENT_REQUIREMENT_UNKNOWN";
    private static final String GDPR_CONSENT_TYPE_PLACES = "GDPR_CONSENT_TYPE_PLACES";
    private static final String GDPR_CONSENT_STATE_UNKNOWN = "GDPR_CONSENT_STATE_UNKNOWN";
    private static final String GDPR_CONSENT_STATE_GRANTED = "GDPR_CONSENT_STATE_GRANTED";
    private static final String GDPR_CONSENT_STATE_REFUSED = "GDPR_CONSENT_STATE_REFUSED";

    private static final String E_INVALID_CONSENT_VALUE = "E_INVALID_CONSENT_VALUE";

    PrivacyManagerModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Nonnull
    @Override
    public String getName() {
        return "PrivacyManager";
    }

    @Override
    public Map<String, Object> getConstants() {
        return new ConstantsBuilder()
                .add(GDPR_CONSENT_NOT_REQUIRED, GdprConsentRequirement.NOT_REQUIRED)
                .add(GDPR_CONSENT_REQUIRED, GdprConsentRequirement.REQUIRED)
                .add(GDPR_CONSENT_REQUIREMENT_UNKNOWN, GdprConsentRequirement.REQUIREMENT_UNKNOWN)
                .add(GDPR_CONSENT_TYPE_PLACES, ConsentType.PLACES_CONSENT)
                .add(GDPR_CONSENT_STATE_UNKNOWN, ConsentState.CONSENT_UNKNOWN)
                .add(GDPR_CONSENT_STATE_REFUSED, ConsentState.CONSENT_REFUSED)
                .add(GDPR_CONSENT_STATE_GRANTED, ConsentState.CONSENT_GRANTED)
                .build();
    }

    @Override
    public boolean hasConstants() {
        return true;
    }

    @ReactMethod
    public void getGdprConsentRequirement(Promise promise) {
        GdprConsentRequirement requirement
                = PrivacyManager.getInstance().getGdprConsentRequirement();
        promise.resolve(toJSValue(requirement));
    }

    @ReactMethod
    public void setUserConsent(int typeValue, int stateValue) {
        ConsentType consentType = fromJSValue(ConsentType.class, typeValue);
        ConsentState consentState = fromJSValue(ConsentState.class, stateValue);

        PrivacyManager.getInstance().setUserConsent(consentType, consentState);
    }

    @ReactMethod
    public void getUserConsent(int typeValue, Promise promise) {
        ConsentType consentType = fromJSValue(ConsentType.class, typeValue);
        if (consentType == null) {
            promise.reject(E_INVALID_CONSENT_VALUE, "Invalid type: " + typeValue);
            return;
        }

        promise.resolve(toJSValue(PrivacyManager.getInstance().getUserConsent(consentType)));
    }
}
