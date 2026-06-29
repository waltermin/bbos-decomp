package net.rim.tid.dynamic_transcoding_data.european_CP1250;

import com.sun.cldc.i18n.j2me.TextProcessingRegistry;

public final class Load_cp1250 {
   public static final void main(String[] argvs) {
      TextProcessingRegistry.loadTextProcessingData(
         "conversion_data_cp1250.cdbf", "net_rim_tid_dynamic_transcoding_data_CP1250", 0, "windows-1250", 16, 1701707776, "BBMillbank"
      );
   }
}
