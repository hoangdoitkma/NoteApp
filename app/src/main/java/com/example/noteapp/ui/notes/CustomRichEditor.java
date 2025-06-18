package com.example.noteapp.ui.notes;

import android.content.Context;
import android.util.AttributeSet;

import jp.wasabeef.richeditor.RichEditor;

public class CustomRichEditor extends RichEditor {

    public CustomRichEditor(Context context) {
        super(context);
    }

    public CustomRichEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRichEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // Set font name via execCommand
    public void setFontName(String fontName) {
        String js = "document.execCommand(\"fontName\", false, '" + fontName + "');";
        execJavaScript(js);
    }

    // Set font size using px via font tag workaround
    public void setFontSizePx(int pxSize) {
        String js = "document.execCommand(\"fontSize\", false, '7');" +
                "var fonts = document.getElementsByTagName('font');" +
                "for (var i = 0; i < fonts.length; i++) {" +
                "  if (fonts[i].size == '7') {" +
                "    fonts[i].removeAttribute('size');" +
                "    fonts[i].style.fontSize = '" + pxSize + "px';" +
                "  }" +
                "}";
        execJavaScript(js);
    }

    // Utility method to run JS
    private void execJavaScript(String js) {
        this.post(() -> this.loadUrl("javascript:(function() {" + js + "})()"));
    }
}
