package font;

import net.rim.device.api.ui.FontRegistry;

public final class FontInj_europeSystem {
   public static final void libMain(String[] args) {
      FontRegistry.loadFont("System.cbtf", "net_rim_font_system", "System", true);
      FontRegistry.loadFont("SystemBold.cbtf", "net_rim_font_system", "System", true);
      FontRegistry.loadFont("SystemItalics.cbtf", "net_rim_font_system", "System", true);
   }
}
