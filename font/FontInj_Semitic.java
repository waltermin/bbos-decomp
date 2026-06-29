package font;

import net.rim.device.api.ui.FontRegistry;

public final class FontInj_Semitic {
   public static final void libMain(String[] args) {
      FontRegistry.loadFont("RIM_semitic.sff4", "net_rim_font_semitic", "BBSemitic", true);
      FontRegistry.loadFont("RIM_semitic_serif.sff4", "net_rim_font_semitic", "BBSemiticSerif", true);
   }
}
