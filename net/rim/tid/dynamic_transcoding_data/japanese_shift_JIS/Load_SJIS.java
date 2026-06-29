package net.rim.tid.dynamic_transcoding_data.japanese_shift_JIS;

import com.sun.cldc.i18n.j2me.TextProcessingRegistry;

public final class Load_SJIS {
   public static final void main(String[] argvs) {
      TextProcessingRegistry.loadTextProcessingData(
         "conversion_data_SJIS.cdbf", "net_rim_tid_dynamic_transcoding_data_Shift_JIS", 0, "Shift_JIS", 36, 1784741888, "BBJapanese"
      );
   }
}
