package net.rim.tid.dynamic_transcoding_data.korean_Johab;

import com.sun.cldc.i18n.j2me.TextProcessingRegistry;

public final class Load_Johab {
   public static final void main(String[] argvs) {
      TextProcessingRegistry.loadTextProcessingData(
         "conversion_data_Johab.cdbf", "net_rim_tid_dynamic_transcoding_data_x_Johab", 0, "x-Johab", 35, 1802436608, "BBKorean"
      );
   }
}
