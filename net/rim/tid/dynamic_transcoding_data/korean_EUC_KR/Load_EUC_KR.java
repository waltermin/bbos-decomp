package net.rim.tid.dynamic_transcoding_data.korean_EUC_KR;

import com.sun.cldc.i18n.j2me.TextProcessingRegistry;

public final class Load_EUC_KR {
   public static final void main(String[] argvs) {
      TextProcessingRegistry.loadTextProcessingData(
         "conversion_data_EUC_KR.cdbf", "net_rim_tid_dynamic_transcoding_data_EUC_KR", 0, "EUC-KR", 32, 1802436608, "BBKorean"
      );
   }
}
