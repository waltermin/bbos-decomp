package net.rim.tid.dynamic_ling_data;

import net.rim.device.api.i18n.Locale;
import net.rim.tid.im.conv.repository.ILingDataLoader;

public class Dynamic_ling_data_english_us_35k_2 extends ILingDataLoader {
   public static void libMain(String[] param) {
      new Dynamic_ling_data_english_us_35k_2().registerData();
   }

   private Dynamic_ling_data_english_us_35k_2() {
      String[] names = new String[]{"english_us_35k_2.001.wrd", "english_us_35k_2.002.wrd", "english_us_35k_2.003.wrd"};
      super._resNames = new String[][]{names, names};
      super._locales = new Locale[]{Locale.get("en"), Locale.get("en", "US")};
      super._types = new int[]{17, 17, 51, -804984325, 720770, 84350977, 202904861, 453710090};
      super._versions = new int[]{65537, 65537, -804651006, 17, 17, 51, -804984325, 720770};
   }
}
