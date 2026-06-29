package font;

import net.rim.device.api.ui.FontRegistry;

public class FontInj_Japanese {
   public static void libMain(String[] args) {
      FontRegistry.loadFont("RIM_japanese_A.sff4", "net_rim_font_japanese", "BBJapanese", true);
      FontRegistry.loadFont("RIM_japanese_B.sff4", "net_rim_font_japanese", "BBJapanese", true);
      FontRegistry.loadFont("RIM_japanese_C.sff4", "net_rim_font_japanese", "BBJapanese", true);
      FontRegistry.loadFont("RIM_japanese_D.sff4", "net_rim_font_japanese", "BBJapanese", true);
      FontRegistry.loadFont("RIM_japanese_E.sff4", "net_rim_font_japanese", "BBJapanese", true);
      FontRegistry.loadFont("RIM_japanese_latin.sff4", "net_rim_font_japanese", "BBJapanese", true);
      FontRegistry.loadFont("RIM_japanese_katakana.sff4", "net_rim_font_japanese", "BBJapanese", true);
   }
}
