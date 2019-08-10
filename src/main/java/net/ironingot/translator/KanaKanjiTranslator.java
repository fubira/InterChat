package net.ironingot.translator;

public class KanaKanjiTranslator {
    public static String translate(String str) {
        return GoogleTranslatorAPI.translate(str, "ja-Hira", "ja");
    }
}
