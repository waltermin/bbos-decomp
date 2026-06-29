package net.rim.device.internal.ui;

import net.rim.device.api.math.Fixed32;

class BilinearFilter implements Filter {
   @Override
   public int widthFP() {
      return 65536;
   }

   @Override
   public int weightFP(int valFP) {
      return Fixed32.abs(valFP) < 65536 ? 65536 - valFP : 0;
   }
}
