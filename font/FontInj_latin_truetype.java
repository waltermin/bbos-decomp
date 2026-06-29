package font;

import net.rim.device.api.ui.FontRegistry;

public final class FontInj_latin_truetype {
   public static final void libMain(String[] args) {
      FontRegistry.loadSplitFont("BBAlphaSans", 2, "net_rim_font_latin_truetype", "BBAlpha Sans", true);
      FontRegistry.loadSplitFont("BBAlphaSansBold", 2, "net_rim_font_latin_truetype", "BBAlpha Sans", true);
      FontRegistry.loadSplitFont("BBAlphaSansItalic", 2, "net_rim_font_latin_truetype", "BBAlpha Sans", true);
      FontRegistry.loadSplitFont("BBAlphaSerif", 2, "net_rim_font_latin_truetype", "BBAlpha Serif", true);
      FontRegistry.loadSplitFont("BBAlphaSerifBold", 2, "net_rim_font_latin_truetype", "BBAlpha Serif", true);
      FontRegistry.loadSplitFont("BBAlphaSerifItalic", 2, "net_rim_font_latin_truetype", "BBAlpha Serif", true);
   }
}
