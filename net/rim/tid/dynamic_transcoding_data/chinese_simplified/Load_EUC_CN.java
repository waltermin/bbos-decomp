package net.rim.tid.dynamic_transcoding_data.chinese_simplified;

import com.sun.cldc.i18n.j2me.TextProcessingRegistry;

public final class Load_EUC_CN {
   public static final void main(String[] argvs) {
      TextProcessingRegistry.loadTextProcessingData(
         "conversion_data_EUC_CN.cdbf", "net_rim_tid_dynamic_transcoding_data_EUC_CN", 0, "GB2312", 30, 2053653326, "BBSimpChinese"
      );
   }
}
