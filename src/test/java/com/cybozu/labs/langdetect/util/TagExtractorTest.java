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

import org.junit.Assert;
import org.junit.Test;

public class TagExtractorTest {

    @Test
    public final void testTagExtractor() {
        TagExtractor extractor = new TagExtractor(null, 0);
        Assert.assertEquals(extractor.target_, null);
        Assert.assertEquals(extractor.threshold_, 0);

        TagExtractor extractor2 = new TagExtractor("abstract", 10);
        Assert.assertEquals(extractor2.target_, "abstract");
        Assert.assertEquals(extractor2.threshold_, 10);
    }

    @Test
    public final void testSetTag() {
        TagExtractor extractor = new TagExtractor(null, 0);
        extractor.setTag("");
        Assert.assertEquals(extractor.tag_, "");
        extractor.setTag(null);
        Assert.assertEquals(extractor.tag_, null);
    }

    @Test
    public final void testAdd() {
        TagExtractor extractor = new TagExtractor(null, 0);
        extractor.add("");
        extractor.add(null);    // ignore
    }

    @Test
    public final void testCloseTag() {
        TagExtractor extractor = new TagExtractor(null, 0);
        extractor.closeTag();    // ignore
    }

    @Test
    public final void testNormalScenario() {
        TagExtractor extractor = new TagExtractor("abstract", 10);
        Assert.assertEquals(extractor.count(), 0);

        LangProfile profile = new LangProfile("en");

        // normal
        extractor.setTag("abstract");
        extractor.add("This is a sample text.");
        profile.update(extractor.closeTag());
        Assert.assertEquals(extractor.count(), 1);
        Assert.assertEquals(profile.n_words[0], 17);  // Thisisasampletext
        Assert.assertEquals(profile.n_words[1], 22);  // _T, Th, hi, ...
        Assert.assertEquals(profile.n_words[2], 17);  // _Th, Thi, his, ...

        // too short
        extractor.setTag("abstract");
        extractor.add("sample");
        profile.update(extractor.closeTag());
        Assert.assertEquals(extractor.count(), 1);

        // other tags
        extractor.setTag("div");
        extractor.add("This is a sample text which is enough long.");
        profile.update(extractor.closeTag());
        Assert.assertEquals(extractor.count(), 1);
    }

    /**
     * Test method for {@link com.cybozu.labs.langdetect.util.TagExtractor#clear()}.
     */
    @Test
    public final void testClear() {
        TagExtractor extractor = new TagExtractor("abstract", 10);
        extractor.setTag("abstract");
        extractor.add("This is a sample text.");
        Assert.assertEquals(extractor.buf_.toString(), "This is a sample text.");
        Assert.assertEquals(extractor.tag_, "abstract");
        extractor.clear();
        Assert.assertEquals(extractor.buf_.toString(), "");
        Assert.assertEquals(extractor.tag_, null);
    }


}
