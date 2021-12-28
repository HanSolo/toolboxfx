package eu.hansolo.toolboxfx;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class FontMetrix {
    private final Text   internalText;
    private       double ascent;
    private       double descent;
    private       double lineHeight;


    public FontMetrix(final Font font) {
        internalText = new Text();
        internalText.setFont(font);
        final Bounds bounds = internalText.getLayoutBounds();
        lineHeight = bounds.getHeight();
        ascent     = -bounds.getMinY();
        descent    = bounds.getMaxY();
    }


    public double getAscent() { return ascent; }

    public double getDescent() { return descent; }

    public double getLineHeight() { return lineHeight; }

    public double computeStringWidth(final String text) {
        internalText.setText(text);
        return internalText.getLayoutBounds().getWidth();
    }
}
