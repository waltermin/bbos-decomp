package net.rim.tid.dynamic_ling_data;

import net.rim.device.api.i18n.Locale;
import net.rim.tid.im.conv.repository.ILingDataLoader;

public class Dynamic_ling_data_spanish_35k_2 extends ILingDataLoader {
   public static void libMain(String[] param) {
      new Dynamic_ling_data_spanish_35k_2().registerData();
   }

   private Dynamic_ling_data_spanish_35k_2() {
      String[] names = new String[]{"spanish_35k_2.001.wrd", "spanish_35k_2.002.wrd", "spanish_35k_2.003.wrd", "spanish_35k_2.004.wrd"};
      super._resNames = new Object[][][]{names};
      super._locales = new Object[]{Locale.get("es", "")};
      super._types = new int[]{17, -804650995, -1662573036, -1298415900};
      super._versions = new int[]{65537, -804650993, -1335522301, -1293666022};
   }
}
