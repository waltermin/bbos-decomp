package font;

import net.rim.device.api.ui.FontRegistry;

public final class FontInj_ChineseCN {
   public static final void libMain(String[] args) {
      FontRegistry.loadFont("RIM_chineseS_A.sff4", "net_rim_font_chinese", "BBSimpChinese", true);
      FontRegistry.loadFont("RIM_chineseS_B.sff4", "net_rim_font_chinese", "BBSimpChinese", true);
      FontRegistry.loadFont("RIM_chineseS_C.sff4", "net_rim_font_chinese", "BBSimpChinese", true);
      FontRegistry.loadFont("RIM_chineseS_D.sff4", "net_rim_font_chinese", "BBSimpChinese", true);
      FontRegistry.loadFont("RIM_chineseS_latin.sff4", "net_rim_font_chinese", "BBSimpChinese", true);
   }
}
