package com.ljmu.andre.Translation;

import android.util.Xml;
import com.ljmu.andre.Translation.Translator.Translation;
import com.ljmu.andre.snaptools.Utils.Callable;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import timber.log.Timber;

import java.io.IOException;
import java.io.InputStream;

import static com.ljmu.andre.snaptools.Utils.Assert.nonFatalAssert;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class TranslationXmlParser {
    static void readXmlFile(InputStream in, Callable<Translation> translationCallable) {
        Timber.d("Reading translation XML file");

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            initRootTag(parser);
            readTranslations(parser, translationCallable);
        } catch (XmlPullParserException e) {
            Timber.e(e);
        } catch (IOException e) {
            Timber.e(e);
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static void initRootTag(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "translations");

        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String attributeName = parser.getAttributeName(0);

            if (attributeName.equals("language")) {
                Timber.d("Language: "
                        + parser.getAttributeValue(i));
            }
        }
    }

    private static void readTranslations(XmlPullParser parser, Callable<Translation> translationCallable) throws XmlPullParserException, IOException {
        while (parser.next() != XmlPullParser.END_TAG) {
//			Timber.d(/*Attributes: */ decryptMsg(new byte[]{-84, -116, -92, 42, -14, -123, -8, -2, -93, 90, 85, 71, -63, 119, -45, -48})
//					+ parser.getAttributeCount());
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            // Starts by looking for the entry tag
//			Timber.d(/*Name: */ decryptMsg(new byte[]{-123, 70, -25, 81, 112, 28, 16, 125, -84, 91, -113, -11, -105, -19, 21, 111})
//					+ name);

            if (name.equals("string")) {
                Translation translation = readTranslation(parser);

//				Timber.d(/*Found translation: */ decryptMsg(new byte[]{79, 88, -108, 109, 11, 60, 38, -57, -33, 31, -71, 75, 25, -96, -6, -61, 30, -117, 57, -90, -85, 12, 33, -86, 17, -27, -67, -39, 117, -105, -7, -39})
//						+ translation);

                if (translation != null)
                    translationCallable.call(translation);
            } else {
                skip(parser);
            }
        }
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    private static Translation readTranslation(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "string");

        String name = null;
        String text = null;
        String packVersion = null;
        String packFlavour = null;

        for (int i = 0; i < parser.getAttributeCount(); i++) {
            String attributeName = parser.getAttributeName(i);

            switch (attributeName) {
                case "name":
                    name = parser.getAttributeValue(i);
                    break;
                case "pack":
                    packVersion = parser.getAttributeValue(i);
                    break;
                case "flavour":
                    packFlavour = parser.getAttributeValue(i);
                    break;
            }
        }

        if (parser.next() == XmlPullParser.TEXT) {
            text = parser.getText();

            if (text != null)
                text = text.replace("\\n", "\n");

            parser.nextTag();
        }

        parser.require(XmlPullParser.END_TAG, null, "string");

        if (nonFatalAssert("Missing [Name: %s] or [Text: %s]",
                name, text)) {
            return null;
        }

        return new Translation(name, text)
                .setPackFlavour(packFlavour)
                .setPackVersion(packVersion);
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
