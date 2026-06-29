package net.rim.device.internal.ui;

import net.rim.device.api.math.Fixed32;

class LanczosFilter implements Filter {
   int sincFP(int valFP) {
      if (valFP == 0) {
         return 65536;
      }

      valFP = Fixed32.mul(valFP, 205887);
      return Fixed32.div(Fixed32.Sin(valFP), valFP);
   }

   @Override
   public int weightFP(int valFP) {
      valFP = Fixed32.abs(valFP);
      return valFP < Fixed32.toFP(3) ? Fixed32.mul(this.sincFP(valFP), this.sincFP(valFP / 3)) : 0;
   }

   @Override
   public int widthFP() {
      return Fixed32.toFP(3);
   }
}
