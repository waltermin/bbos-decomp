package net.rim.device.internal.ui;

import net.rim.device.api.math.Fixed32;

class Weights {
   int _left;
   int _right;
   int[] _weightsFP;

   Weights(Filter filter, int left, int right, int centerFP, int fscaleFP) {
      this._left = left;
      this._right = right;
      this._weightsFP = new int[right - left + 1];
      int sumFP = 0;

      for (int iSrc = this._left; iSrc <= this._right; iSrc++) {
         int weightFP = Fixed32.mul(fscaleFP, filter.weightFP(Fixed32.mul(fscaleFP, centerFP - Fixed32.toFP(iSrc))));
         this._weightsFP[iSrc - this._left] = weightFP;
         sumFP += weightFP;
      }

      for (int iSrc = this._left; iSrc <= this._right; iSrc++) {
         this._weightsFP[iSrc - this._left] = Fixed32.div(this._weightsFP[iSrc - this._left], sumFP);
      }
   }

   int filter(Fetcher fetcher) {
      int aFP = 0;
      int rFP = 0;
      int gFP = 0;
      int bFP = 0;

      for (int i = this._left; i <= this._right; i++) {
         int pixel = fetcher.get(i);
         int weightFP = this._weightsFP[i - this._left];
         bFP += (pixel & 0xFF) * weightFP;
         gFP += (pixel >> 8 & 0xFF) * weightFP;
         rFP += (pixel >> 16 & 0xFF) * weightFP;
         aFP += (pixel >> 24 & 0xFF) * weightFP;
      }

      return this.clampFP(aFP) << 24 | this.clampFP(rFP) << 16 | this.clampFP(gFP) << 8 | this.clampFP(bFP);
   }

   int clampFP(int dFP) {
      int i = Fixed32.toInt(dFP);
      if (i < 0) {
         return 0;
      } else {
         return i > 255 ? 255 : i;
      }
   }
}
