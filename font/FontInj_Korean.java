package font;

import net.rim.device.api.ui.FontRegistry;

public class FontInj_Korean {
   public static void libMain(String[] args) {
      FontRegistry.loadFont("RIM_korean_A.sff4", "net_rim_font_korean", "BBKorean", true);
      FontRegistry.loadFont("RIM_korean_B.sff4", "net_rim_font_korean", "BBKorean", true);
      FontRegistry.loadFont("RIM_korean_C.sff4", "net_rim_font_korean", "BBKorean", true);
      FontRegistry.loadFont("RIM_korean_D.sff4", "net_rim_font_korean", "BBKorean", true);
      FontRegistry.loadFont("RIM_korean_latin_han_A.sff4", "net_rim_font_korean", "BBKorean", true);
      FontRegistry.loadFont("RIM_korean_latin_han_B.sff4", "net_rim_font_korean", "BBKorean", true);
   }
}
