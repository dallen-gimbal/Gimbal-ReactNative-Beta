package com.gimbal.react;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Map;

import javax.annotation.Nonnull;

class MapKeyValueMatcher<K, V> extends TypeSafeDiagnosingMatcher<Map<K, V>> {
    private K key;
    private V value;

    MapKeyValueMatcher(@Nonnull K key, @Nonnull V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Map to contain key value pair: (")
                .appendValue(key).appendText(", ").appendValue(value).appendText(")");
    }

    @Override
    protected boolean matchesSafely(Map<K, V> item, Description mismatchDescription) {
        if (!item.containsKey(key)) {
            mismatchDescription.appendText("does not contain the key ")
                    .appendValue(key);
            return false;
        }
        V itemValue = item.get(key);
        if (!value.equals(itemValue)) {
            mismatchDescription.appendText("found mismatched value ").appendValue(itemValue);
            return false;
        }
        return true;
    }
}
