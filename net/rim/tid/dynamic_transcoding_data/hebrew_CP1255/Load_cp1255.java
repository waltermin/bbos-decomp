package net.rim.tid.dynamic_transcoding_data.hebrew_CP1255;

import com.sun.cldc.i18n.j2me.TextProcessingRegistry;

public final class Load_cp1255 {
   public static final void main(String[] argvs) {
      TextProcessingRegistry.loadTextProcessingData(
         "conversion_data_cp1255.cdbf", "net_rim_tid_dynamic_transcoding_data_CP1255", 0, "windows-1255", 21, 1751449600, "BBSemitic"
      );
   }
}
