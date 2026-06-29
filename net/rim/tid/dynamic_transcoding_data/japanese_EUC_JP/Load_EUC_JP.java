package net.rim.tid.dynamic_transcoding_data.japanese_EUC_JP;

import com.sun.cldc.i18n.j2me.TextProcessingRegistry;

public final class Load_EUC_JP {
   public static final void main(String[] argvs) {
      TextProcessingRegistry.loadTextProcessingData(
         "conversion_data_EUC_JP.cdbf", "net_rim_tid_dynamic_transcoding_data_EUC_JP", 0, "EUC-JP", 31, 1784741888, "BBJapanese"
      );
   }
}
