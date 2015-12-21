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

/**
 * 
 */
package com.cybozu.labs.langdetect;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LanguageTest {

    @Test
    public final void testLanguage() {
        Language lang = new Language(null, 0);
        Assert.assertEquals(lang.lang, null);
        Assert.assertEquals(lang.prob, 0.0, 0.0001);
        Assert.assertEquals(lang.toString(), "");
        
        Language lang2 = new Language("en", 1.0);
        Assert.assertEquals(lang2.lang, "en");
        Assert.assertEquals(lang2.prob, 1.0, 0.0001);
        Assert.assertEquals(lang2.toString(), "en:1.0");
        
    }

}
