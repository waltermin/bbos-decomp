package font;

import net.rim.device.api.ui.FontRegistry;

public final class FontInj_europe {
   public static final void libMain(String[] args) {
      FontRegistry.loadFont("RIM_latin_millbank.sff4", "net_rim_font_european_sff", "BBMillbank", true);
      FontRegistry.loadFont("RIM_latin_millbank_tall.sff4", "net_rim_font_european_sff", "BBMillbankTall", true);
      FontRegistry.loadFont("RIM_latin_capitals.sff4", "net_rim_font_european_sff", "BBCapitals", true);
      FontRegistry.loadFont("RIM_european.sff4", "net_rim_font_european_sff", "BBSansSerif", true);
      FontRegistry.loadFont("RIM_european_serif.sff4", "net_rim_font_european_sff", "BBSerif", true);
      FontRegistry.loadFont("RIM_latin_sans_serif.sff4", "net_rim_font_european_sff", "BBSansSerifSquare", true);
      FontRegistry.loadFont("RIM_latin_serif.sff4", "net_rim_font_european_sff", "BBSerifFixed", true);
      FontRegistry.loadFont("RIM_latin_casual.sff4", "net_rim_font_european_sff", "BBCasual", true);
      FontRegistry.loadFont("RIM_latin_condensed.sff4", "net_rim_font_european_sff", "BBCondensed", true);
      FontRegistry.loadFont("RIM_latin_clarity.sff4", "net_rim_font_european_sff", "BBClarity", true);
      FontRegistry.loadFont("RIM_european_mono.sff4", "net_rim_font_european_sff", "BBMonospace", false);
      FontRegistry.loadFont("RIM_latin_condensed_mono.sff4", "net_rim_font_european_sff", "BBCondensedMonospace", false);
   }
}
