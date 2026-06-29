package net.rim.plazmic.internal.mediaengine.util;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.math.VecMath;

public final class MatrixDecomposition implements TransformationMatrix {
   private int _theta;
   private int _mx;
   private int _my;
   private int _kx;
   private int _ky;
   private int _tx;
   private int _ty;
   private static int[] _testSquareX = new int[]{
      0, 1, 1, 0, -805044213, 775162112, 774909491, 3420721, -805044206, 1699549184, 543254884, 1768386117, 1092642158, 18768, -805044199, 1699878656
   };
   private static int[] _testSquareY = new int[]{
      0, 0, 1, 1, -804651004, 0, 1, 1, 0, -805044213, 775162112, 774909491, 3420721, -805044206, 1699549184, 543254884
   };
   private static int[] _testResultX = new int[4];
   private static int[] _testResultY = new int[4];

   public final void decomposeMatrix(int[] matrix, int position) {
      int xx = matrix[position];
      int yx = matrix[position + 3];
      int xy = matrix[position + 1];
      int yy = matrix[position + 4];
      this._tx = matrix[position + 2];
      this._ty = matrix[position + 5];
      int det = Fixed32.mul(xx, yy) - Fixed32.mul(xy, yx);
      if (det == 0) {
         throw new Object("cannot decompose matrix with zero determinant");
      }

      this._theta = Fixed32.atand2(yx, xx);
      int c = Fixed32.cosd(this._theta);
      int s = Fixed32.sind(this._theta);
      boolean useCos = Fixed32.abs(c) > Fixed32.div(65536, Fixed32.sqrt(131072));
      if (useCos) {
         this._kx = Fixed32.div(xx, c);
      } else {
         this._kx = Fixed32.div(yx, s);
      }

      this._ky = Fixed32.div(det, this._kx);
      this._mx = Fixed32.div(Fixed32.mul(c, xy) + Fixed32.mul(s, yy), this._ky);
      VecMath.transformPoints(matrix, position, _testSquareX, _testSquareY, _testResultX, _testResultY);
      int opposite = _testResultY[1] - _testResultY[0];
      int adjacent = _testResultX[1] - _testResultX[0];
      this._my = Fixed32.atand2(opposite, adjacent);
   }

   @Override
   public final int getTheta() {
      return this._theta;
   }

   @Override
   public final int getSkewX() {
      return Fixed32.atand2(this._mx, 65536);
   }

   @Override
   public final int getSkewY() {
      return this._my;
   }

   @Override
   public final int getScaleX() {
      return this._kx;
   }

   @Override
   public final int getScaleY() {
      return this._ky;
   }

   @Override
   public final int getTranslateX() {
      return this._tx;
   }

   @Override
   public final int getTranslateY() {
      return this._ty;
   }
}
