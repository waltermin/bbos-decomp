package net.rim.device.api.games.util;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.math.VecMath;
import net.rim.device.api.ui.XYPoint;

public final class FP {
   public static final int PI;
   public static final int TWOPI;
   public static final int PI_OVER_2;
   public static final int E;
   public static final int HALF;
   public static final int QUARTER;

   public static final int toInt(int x) {
      return Fixed32.toInt(x);
   }

   public static final int intToFP(int x) {
      return Fixed32.toFP(x);
   }

   public static final int Mul(int x, int y) {
      return Fixed32.mul(x, y);
   }

   public static final int Div(int x, int y) {
      return Fixed32.div(x, y);
   }

   public static final int round(int n) {
      return Fixed32.round(n);
   }

   public static final int Sin(int f) {
      return Fixed32.Sin(f);
   }

   public static final int Cos(int f) {
      return Fixed32.Cos(f);
   }

   public static final int ArcTan(int f) {
      return Fixed32.ArcTan(f);
   }

   public static final boolean intersects(int ax0, int ay0, int ax1, int ay1, int bx0, int by0, int bx1, int by1) {
      return VecMath.intersects(ax0, ay0, ax1, ay1, bx0, by0, bx1, by1);
   }

   public static final boolean intersectionXY(int ax0, int ay0, int ax1, int ay1, int bx0, int by0, int bx1, int by1, XYPoint result) {
      if (!intersects(ax0, ay0, ax1, ay1, bx0, by0, bx1, by1)) {
         return false;
      }

      int px = intToFP(ax0);
      int py = intToFP(ay0);
      int rx = intToFP(ax1) - px;
      int ry = intToFP(ay1) - py;
      int qx = intToFP(bx0);
      int qy = intToFP(by0);
      int sx = intToFP(bx1) - qx;
      int sy = intToFP(by1) - qy;
      int det = Fixed32.mul(sx, ry) - Fixed32.mul(sy, rx);
      if (det == 0) {
         return false;
      }

      int z = Fixed32.div(Fixed32.mul(sx, qy - py) + Fixed32.mul(sy, px - qx), det);
      result.set(Fixed32.toRoundedInt(px + Fixed32.mul(z, rx)), Fixed32.toRoundedInt(py + Fixed32.mul(z, ry)));
      return true;
   }

   public static final boolean intersectionXY(XYPoint A1, XYPoint A2, XYPoint B1, XYPoint B2, XYPoint result) {
      return intersectionXY(A1.x, A1.y, A2.x, A2.y, B1.x, B1.y, B2.x, B2.y, result);
   }
}
