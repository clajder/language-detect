# language-detect

[![Build Status](https://travis-ci.org/clajder/language-detect.svg?branch=master)](https://travis-ci.org/clajder/language-detect)   [ ![Download](https://api.bintray.com/packages/clajder/maven/language-detect/images/download.svg) ](https://bintray.com/clajder/maven/language-detect/_latestVersion)

Gradle port for language detection library implemented in plain Java (aliases: language identification, language guessing)

# Abstract

  * Generate language profiles from Wikipedia abstract xml
  * Detect language of a text using naive Bayesian filter
  * 99% over precision for 53 languages

# News

  * 21/12/2015
    * Moved to gradle build system
    * Fixes issues with some tests
    * Add support for JDK 7
    * Update DetectorFactory to support classpath lookup
    * Add upload to `Bintray`
  * 03/03/2014
    * Distribute a new package with short-text profiles (47 languages)
      * Build latest codes
      * Remove Apache Nutch's plugin (for API deprecation)
  * 01/12/2012
    * Migrate the repository of language-detection from subversion into git
      * for Maven support
....

# Requires #

  * Java 1.7 or later
  * JSONIC (bundled) < http://sourceforge.jp/projects/jsonic/devel/ , Apache License 2.0 >

Add the repositories:

```gradle
repositories {
    mavenCentral()
    maven { url  "http://dl.bintray.com/clajder/maven" }
}
```

Dependency:

```gradle
dependencies {
  compile 'com.cybozu.labs:language-detect:INSERT_LATEST_VERSION_HERE'
}
```

How to use the latest build with Maven:

```xml
<repository>
    <snapshots>
        <enabled>false</enabled>
    </snapshots>
    <id>bintray-clajder-maven</id>
    <name>bintray</name>
    <url>http://dl.bintray.com/clajder/maven</url>
</repository>
```

Dependency:

```xml
<dependency>
    <groupId>com.cybozu.labs</groupId>
    <artifactId>language-detect</artifactId>
    <version>INSERT_LATEST_VERSION_HERE</version>
</dependency>
```


# Usage

```
import java.util.ArrayList;
import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.Language;

class LangDetectSample {
    public void init(String profileDirectory) throws LangDetectException {
        DetectorFactory.loadProfile(profileDirectory);
    }
    public String detect(String text) throws LangDetectException {
        Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.detect();
    }
    public ArrayList<Language> detectLangs(String text) throws LangDetectException {
        Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.getProbabilities();
    }
}
```