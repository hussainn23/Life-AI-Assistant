package com.softec.lifeaiassistant.customClasses;

import android.text.Html;

public class BoldFormatter {

    public static CharSequence formatBold(String input) {
        // Replace each occurrence of **text** with <b>text</b>
        String htmlFormattedString = input.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");

        // Replace actual line breaks with <br> tags to preserve them in the formatted text
        htmlFormattedString = htmlFormattedString.replaceAll("\n", "<br>");

        // Use Html.fromHtml to parse the HTML string and apply the formatting
        return Html.fromHtml(htmlFormattedString, Html.FROM_HTML_MODE_LEGACY);
    }
}
