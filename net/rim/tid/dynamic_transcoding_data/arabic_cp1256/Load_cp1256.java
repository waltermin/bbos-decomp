package net.rim.tid.dynamic_transcoding_data.arabic_cp1256;

import com.sun.cldc.i18n.j2me.TextProcessingRegistry;

public final class Load_cp1256 {
   public static final void main(String[] argvs) {
      TextProcessingRegistry.loadTextProcessingData(
         "conversion_data_cp1256.cdbf", "net_rim_tid_dynamic_transcoding_data_CP1256", 0, "windows-1256", 22, 1634861056, "BBSemitic"
      );
   }
}
