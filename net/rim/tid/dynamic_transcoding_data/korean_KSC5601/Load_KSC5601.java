package net.rim.tid.dynamic_transcoding_data.korean_KSC5601;

import com.sun.cldc.i18n.j2me.TextProcessingRegistry;

public final class Load_KSC5601 {
   public static final void main(String[] argvs) {
      TextProcessingRegistry.loadTextProcessingData(
         "conversion_data_KSC5601.cdbf", "net_rim_tid_dynamic_transcoding_data_KSC5601", 0, "KSC5601", 40, 1802436608, "BBKorean"
      );
   }
}
