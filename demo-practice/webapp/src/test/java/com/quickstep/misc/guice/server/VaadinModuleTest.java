package com.quickstep.misc.guice.server;

import junit.framework.TestCase;

import java.util.Properties;

import static org.junit.Assert.assertThrows;

public class VaadinModuleTest extends TestCase {

    public void testInterpolateProperties() {
        var testProperties = new Properties();
        testProperties.setProperty("a.b.c", "SomeValue");
        testProperties.setProperty("a.b.d", "Prefix-${a.b.c}-Suffix");

        var resultProperties = VaadinModule.interpolateProperties(testProperties);

        assertEquals("Prefix-SomeValue-Suffix", resultProperties.getProperty("a.b.d"));
    }

    public void testInterpolatePropertiesWithCycle() {
        var testProperties = new Properties();
        testProperties.setProperty("a.b.c", "${a.b.d}");
        testProperties.setProperty("a.b.d", "${a.b.c}");

        assertThrows(IllegalStateException.class, () -> {
            VaadinModule.interpolateProperties(testProperties);
        });
    }
}
