package com.gimbal.react;

import java.util.HashMap;
import java.util.Map;

class ConstantsBuilder {
    private final Map<String, Object> constants = new HashMap<>();

    Map<String, Object> build() {
        return constants;
    }

    ConstantsBuilder add(String jsConstantName, Object value) {
        constants.put(jsConstantName, BridgeUtilities.toJSValue(value));
        return this;
    }
}
