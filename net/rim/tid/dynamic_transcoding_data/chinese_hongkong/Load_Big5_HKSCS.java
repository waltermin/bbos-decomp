package net.rim.tid.dynamic_transcoding_data.chinese_hongkong;

import com.sun.cldc.i18n.j2me.TextProcessingRegistry;

public final class Load_Big5_HKSCS {
   public static final void main(String[] argvs) {
      TextProcessingRegistry.loadTextProcessingData(
         "conversion_data_Big5_HKSCS.cdbf", "net_rim_tid_dynamic_transcoding_data_Big5_HKSCS", 0, "Big5-HKSCS", 38, 2053654603, "BBHongKong"
      );
      TextProcessingRegistry.loadTextProcessingData(
         "conversion_data_Big5_HKSCS.cdbf", "net_rim_tid_dynamic_transcoding_data_Big5_HKSCS", 0, "Big5", -1, 2053654603, "BBTradChinese"
      );
   }
}
