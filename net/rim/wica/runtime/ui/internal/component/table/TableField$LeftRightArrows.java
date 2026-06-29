package net.rim.wica.runtime.ui.internal.component.table;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.theme.Theme;

class TableField$LeftRightArrows implements GlobalEventListener {
   private Bitmap _left;
   private Bitmap _right;

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 2573494863350550132L) {
         this.setArrows();
      }
   }

   private TableField$LeftRightArrows() {
      this.setArrows();
      Application.getApplication().addGlobalEventListener(this);
   }

   private void setArrows() {
      this._left = this.rotateByMinusNintyDeg(Theme.getThemeBitmap(0));
      this._right = this.rotateByMinusNintyDeg(Theme.getThemeBitmap(1));
   }

   private Bitmap rotateByMinusNintyDeg(Bitmap bitmap) {
      Bitmap rotatedBitmap = null;
      if (bitmap != null) {
         int width = bitmap.getWidth();
         int height = bitmap.getHeight();
         int[] argb = new int[width * height];

         for (int i = 0; i < width; i++) {
            bitmap.getARGB(argb, i * height, 1, i, 0, 1, height);
         }

         rotatedBitmap = (Bitmap)(new Object(height, width));
         rotatedBitmap.setARGB(argb, 0, height, 0, 0, height, width);
      }

      return rotatedBitmap;
   }

   TableField$LeftRightArrows(TableField$1 x0) {
      this();
   }
}
