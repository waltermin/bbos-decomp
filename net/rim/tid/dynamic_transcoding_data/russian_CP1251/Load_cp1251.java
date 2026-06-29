package net.rim.tid.dynamic_transcoding_data.russian_CP1251;

import com.sun.cldc.i18n.j2me.TextProcessingRegistry;

public final class Load_cp1251 {
   public static final void main(String[] argvs) {
      TextProcessingRegistry.loadTextProcessingData(
         "conversion_data_cp1251.cdbf", "net_rim_tid_dynamic_transcoding_data_CP1251", 0, "windows-1251", 17, 1920270336, "BBMillbank"
      );
   }
}
