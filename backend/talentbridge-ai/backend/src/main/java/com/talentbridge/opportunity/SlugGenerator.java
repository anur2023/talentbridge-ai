package com.talentbridge.opportunity;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class SlugGenerator {

    private static final Pattern NON_ALNUM = Pattern.compile("[^a-z0-9]+");

    private SlugGenerator() {
    }

    public static String base(String title) {
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
        String slug = NON_ALNUM.matcher(normalized).replaceAll("-");
        slug = slug.replaceAll("(^-+)|(-+$)", "");
        return slug.isBlank() ? "opportunity" : slug;
    }
}
