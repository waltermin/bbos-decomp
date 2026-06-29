package net.rim.tid.dynamic_transcoding_data.russian_KOI8_R;

import com.sun.cldc.i18n.j2me.TextProcessingRegistry;

public final class Load_KOI8_R {
   public static final void main(String[] argvs) {
      TextProcessingRegistry.loadTextProcessingData(
         "conversion_data_KOI8_R.cdbf", "net_rim_tid_dynamic_transcoding_data_KOI8_R", 0, "KOI8-R", 25, 1920270336, "BBMillbank"
      );
   }
}
