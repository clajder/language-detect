/*
 * The MIT License (MIT)
 *
 *  Copyright (c) 2015 Oembedler Inc. and Contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 *  rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 *  persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.cybozu.labs.langdetect.util;

import com.cybozu.labs.langdetect.util.LangProfile;
import org.junit.*;

import static org.junit.Assert.assertEquals;

/**
 * @author Nakatani Shuyo
 */
public class LangProfileTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link com.cybozu.labs.langdetect.util.LangProfile#LangProfile()}.
     */
    @Test
    public final void testLangProfile() {
        LangProfile profile = new LangProfile();
        assertEquals(profile.name, null);
    }

    /**
     * Test method for {@link com.cybozu.labs.langdetect.util.LangProfile#LangProfile(java.lang.String)}.
     */
    @Test
    public final void testLangProfileStringInt() {
        LangProfile profile = new LangProfile("en");
        assertEquals(profile.name, "en");
    }

    /**
     * Test method for {@link com.cybozu.labs.langdetect.util.LangProfile#add(java.lang.String)}.
     */
    @Test
    public final void testAdd() {
        LangProfile profile = new LangProfile("en");
        profile.add("a");
        assertEquals((int) profile.freq.get("a"), 1);
        profile.add("a");
        assertEquals((int) profile.freq.get("a"), 2);
        profile.omitLessFreq();
    }


    /**
     * Illegal call test for {@link LangProfile#add(String)}
     */
    @Test
    public final void testAddIllegally1() {
        LangProfile profile = new LangProfile(); // Illegal ( available for only JSONIC ) but ignore  
        profile.add("a"); // ignore
        assertEquals(profile.freq.get("a"), null); // ignored
    }

    /**
     * Illegal call test for {@link LangProfile#add(String)}
     */
    @Test
    public final void testAddIllegally2() {
        LangProfile profile = new LangProfile("en");
        profile.add("a");
        profile.add("");  // Illegal (string's length of parameter must be between 1 and 3) but ignore
        profile.add("abcd");  // as well
        assertEquals((int) profile.freq.get("a"), 1);
        assertEquals(profile.freq.get(""), null);     // ignored
        assertEquals(profile.freq.get("abcd"), null); // ignored

    }

    /**
     * Test method for {@link com.cybozu.labs.langdetect.util.LangProfile#omitLessFreq()}.
     */
    @Test
    public final void testOmitLessFreq() {
        LangProfile profile = new LangProfile("en");
        String[] grams = "a b c \u3042 \u3044 \u3046 \u3048 \u304a \u304b \u304c \u304d \u304e \u304f".split(" ");
        for (int i = 0; i < 5; ++i)
            for (String g : grams) {
                profile.add(g);
            }
        profile.add("\u3050");

        assertEquals((int) profile.freq.get("a"), 5);
        assertEquals((int) profile.freq.get("\u3042"), 5);
        assertEquals((int) profile.freq.get("\u3050"), 1);
        profile.omitLessFreq();
        assertEquals(profile.freq.get("a"), null); // omitted
        assertEquals((int) profile.freq.get("\u3042"), 5);
        assertEquals(profile.freq.get("\u3050"), null); // omitted
    }

    /**
     * Illegal call test for {@link com.cybozu.labs.langdetect.util.LangProfile#omitLessFreq()}.
     */
    @Test
    public final void testOmitLessFreqIllegally() {
        LangProfile profile = new LangProfile();
        profile.omitLessFreq();  // ignore
    }

}
