package net.rim.device.internal.ui;

import net.rim.device.api.system.Bitmap;

public final class Cursor {
   private Bitmap _bitmap;
   private int _originX;
   private int _originY;
   public static final int PREDEFINED_CURSOR_RIGHT_POINTER;
   public static final int PREDEFINED_CURSOR_HAND;
   public static final int PREDEFINED_CURSOR_I_BAR;
   public static final int PREDEFINED_CURSOR_ZOOM_IN;
   public static final int PREDEFINED_CURSOR_ZOOM_OUT;
   private static Cursor[] PREDEFINED_CURSORS = new Cursor[]{
      new Cursor(Bitmap.getBitmapResource("net_rim_cldc", "right_pointer.gif"), 0, 2),
      new Cursor(Bitmap.getBitmapResource("net_rim_cldc", "hand_pointer.gif"), 4, 0),
      new Cursor(Bitmap.getBitmapResource("net_rim_cldc", "bar_pointer.gif"), 7, 8),
      new Cursor(Bitmap.getBitmapResource("net_rim_cldc", "zoomin_pointer.gif"), 6, 6),
      new Cursor(Bitmap.getBitmapResource("net_rim_cldc", "zoomout_pointer.gif"), 6, 6)
   };

   public Cursor(Bitmap bitmap, int originX, int originY) {
      this._bitmap = bitmap;
      this._originX = originX;
      this._originY = originY;
   }

   public final Bitmap getBitmap() {
      return this._bitmap;
   }

   public final int getOriginX() {
      return this._originX;
   }

   public final int getOriginY() {
      return this._originY;
   }

   public final int getOrginX() {
      return this.getOriginX();
   }

   public final int getOrginY() {
      return this.getOriginY();
   }

   public static final Cursor getPredefinedCursor(int cursor) {
      return PREDEFINED_CURSORS[cursor];
   }
}
