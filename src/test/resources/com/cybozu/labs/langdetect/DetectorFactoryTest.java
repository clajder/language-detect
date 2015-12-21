package com.cybozu.labs.langdetect;

import org.junit.Test;

public class DetectorFactoryTest {

    @Test
    public final void testClasspathLoad() {
        DetectorFactory.loadProfile();
    }

}
