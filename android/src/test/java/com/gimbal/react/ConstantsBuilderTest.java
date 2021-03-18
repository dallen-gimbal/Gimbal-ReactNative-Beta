package com.gimbal.react;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ConstantsBuilderTest {

    @Test
    public void noAddsBuildsAnEmptyMap() {
        Map<String, Object> constants = new ConstantsBuilder().build();

        assertThat(constants, Matchers.anEmptyMap());
    }

    @Test
    public void buildsCorrectNumberOfUniqueValues() {
        Map<String, Object> constants
                = new ConstantsBuilder()
                .add("ZERO", 0)
                .add("ONE", 1)
                .add("TWO", 2)
                .add("THREE", 3)
                .add("FOO", "BAR")
                .build();

        assertThat(constants, Matchers.aMapWithSize(5));
        assertThat(constants.get("ZERO"), is(0));
        assertThat(constants.get("ONE"), is(1));
        assertThat(constants.get("TWO"), is(2));
        assertThat(constants.get("THREE"), is(3));
        assertThat(constants.get("FOO"), is("BAR"));
    }

    @Test
    public void mostRecentAddReplacesPrevious() {
        Map<String, Object> constants
                = new ConstantsBuilder()
                .add("ZERO", "OOPS")
                .add("ZERO", TestEnum.ZERO)
                .build();

        assertThat(constants, Matchers.aMapWithSize(1));
        assertThat(constants.get("ZERO"), is(TestEnum.ZERO.ordinal()));
    }

    @Test
    public void buildsOrdinalsFromEnums() {
        Map<String, Object> constants
                = new ConstantsBuilder()
                .add("ZERO", TestEnum.ZERO)
                .add("ONE", TestEnum.ONE)
                .add("TWO", TestEnum.TWO)
                .add("THREE", TestEnum.THREE)
                .build();

        assertThat(constants.get("ZERO"), is(TestEnum.ZERO.ordinal()));
        assertThat(constants.get("ONE"), is(TestEnum.ONE.ordinal()));
        assertThat(constants.get("TWO"), is(TestEnum.TWO.ordinal()));
        assertThat(constants.get("THREE"), is(TestEnum.THREE.ordinal()));
    }

    @Test
    public void buildsNonEnumsAsThemselves() {
        Map<String, Object> constants
                = new ConstantsBuilder()
                .add("ZERO", "foo")
                .add("ONE", 1)
                .build();

        assertThat(constants.get("ZERO"), is("foo"));
        assertThat(constants.get("ONE"), is(1));
    }

    enum TestEnum {
        ZERO,
        ONE,
        TWO,
        THREE
    }
}
