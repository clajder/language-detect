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
package com.cybozu.labs.langdetect;

import com.cybozu.labs.langdetect.util.LangProfile;
import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Language Detector Factory Class
 * This class manages an initialization and constructions of {@link Detector}.
 * Before using language detection library, load profiles with
 * {@link DetectorFactory#loadProfile(String)} method and set initialization
 * parameters.
 * When the language detection, construct Detector instance via
 * {@link DetectorFactory#create()}. See also {@link Detector}'s sample code.
 * <ul>
 * <li>4x faster improvement based on Elmer Garduno's code. Thanks!</li>
 * </ul>
 *
 * @author Nakatani Shuyo
 * @see Detector
 */
public class DetectorFactory {
    public HashMap<String, double[]> wordLangProbMap;
    public ArrayList<String> langlist;
    public Long seed = null;

    private DetectorFactory() {
        wordLangProbMap = new HashMap<String, double[]>();
        langlist = new ArrayList<String>();
    }

    static private DetectorFactory instance_ = new DetectorFactory();

    public static void loadProfile(String profileDirectory) throws LangDetectException {
        loadProfile(new File(profileDirectory));
    }

    public static void loadProfile() throws LangDetectException, IOException {

        final String resourceDir = "/profiles";

        List<String> files = null;
        try (InputStream is = DetectorFactory.class.getResourceAsStream(resourceDir)) {
            files = IOUtils.readLines(is, Charsets.UTF_8);
        }

        if (files == null || files.size() == 0)
            throw new LangDetectException(ErrorCode.FileLoadError, "can't read '" + resourceDir + "'");

        int index = 0;
        for (String f : files) {
            try (InputStream is = DetectorFactory.class.getResourceAsStream(resourceDir + "/" + f)) {
                LangProfile profile = JSON.decode(is, LangProfile.class);
                addProfile(profile, index, files.size());
                ++index;
            } catch (JSONException e) {
                throw new LangDetectException(ErrorCode.FormatError, "profile format error in '" + resourceDir + f + "'");
            } catch (IOException e) {
                throw new LangDetectException(ErrorCode.FileLoadError, "can't open '" + resourceDir + f + "'");
            }
        }
    }

    public static void loadProfile(File profileDirectory) throws LangDetectException {
        File[] listFiles = profileDirectory.listFiles();
        if (listFiles == null)
            throw new LangDetectException(ErrorCode.NeedLoadProfileError, "Not found profile: " + profileDirectory);

        int langsize = listFiles.length, index = 0;
        for (File file : listFiles) {
            if (file.getName().startsWith(".") || !file.isFile())
                continue;
            FileInputStream is = null;
            try {
                is = new FileInputStream(file);
                LangProfile profile = JSON.decode(is, LangProfile.class);
                addProfile(profile, index, langsize);
                ++index;
            } catch (JSONException e) {
                throw new LangDetectException(ErrorCode.FormatError, "profile format error in '" + file.getName() + "'");
            } catch (IOException e) {
                throw new LangDetectException(ErrorCode.FileLoadError, "can't open '" + file.getName() + "'");
            } finally {
                try {
                    if (is != null)
                        is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Load profiles from specified directory. This method must be called once
     * before language detection.
     * @param json_profiles list of profiles in json
     * @throws LangDetectException single exception
     */
    public static void loadProfile(List<String> json_profiles) throws LangDetectException {
        int index = 0;
        int langsize = json_profiles.size();
        if (langsize < 2)
            throw new LangDetectException(ErrorCode.NeedLoadProfileError, "Need more than 2 profiles");

        for (String json : json_profiles) {
            try {
                LangProfile profile = JSON.decode(json, LangProfile.class);
                addProfile(profile, index, langsize);
                ++index;
            } catch (JSONException e) {
                throw new LangDetectException(ErrorCode.FormatError, "profile format error");
            }
        }
    }

    static void addProfile(LangProfile profile, int index, int langsize) throws LangDetectException {
        String lang = profile.name;
        if (instance_.langlist.contains(lang)) {
            throw new LangDetectException(ErrorCode.DuplicateLangError, "duplicate the same language profile");
        }
        instance_.langlist.add(lang);
        for (String word : profile.freq.keySet()) {
            if (!instance_.wordLangProbMap.containsKey(word)) {
                instance_.wordLangProbMap.put(word, new double[langsize]);
            }
            int length = word.length();
            if (length >= 1 && length <= 3) {
                double prob = profile.freq.get(word).doubleValue() / profile.n_words[length - 1];
                instance_.wordLangProbMap.get(word)[index] = prob;
            }
        }
    }

    /**
     * Clear loaded language profiles (reinitialization to be available)
     */
    static public void clear() {
        instance_.langlist.clear();
        instance_.wordLangProbMap.clear();
    }

    /**
     * Construct Detector instance
     * @return Detector instance
     * @throws LangDetectException single exception
     */
    static public Detector create() throws LangDetectException {
        return createDetector();
    }

    /**
     * Construct Detector instance with smoothing parameter
     * @param alpha variation
     * @return Detector instance
     * @throws LangDetectException single exception
     */
    public static Detector create(double alpha) throws LangDetectException {
        Detector detector = createDetector();
        detector.setAlpha(alpha);
        return detector;
    }

    static private Detector createDetector() throws LangDetectException {
        if (instance_.langlist.size() == 0)
            throw new LangDetectException(ErrorCode.NeedLoadProfileError, "need to load profiles");
        Detector detector = new Detector(instance_);
        return detector;
    }

    public static void setSeed(long seed) {
        instance_.seed = seed;
    }

    public static final List<String> getLangList() {
        return Collections.unmodifiableList(instance_.langlist);
    }
}
