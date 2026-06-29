package net.rim.device.internal.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.ResourceFetcher;

public class Background {
   private int _positionX;
   private int _positionY;
   private int _repeat;
   public static final int POSITION_X_INHERIT;
   public static final int POSITION_X_LEFT;
   public static final int POSITION_X_RIGHT;
   public static final int POSITION_X_CENTER;
   public static final int POSITION_Y_INHERIT;
   public static final int POSITION_Y_TOP;
   public static final int POSITION_Y_BOTTOM;
   public static final int POSITION_Y_CENTER;
   public static final int REPEAT_INHERIT;
   public static final int REPEAT_NONE;
   public static final int REPEAT_HORIZONTAL;
   public static final int REPEAT_VERTICAL;
   public static final int REPEAT_BOTH;

   public static Background createSolidBackground(int color) {
      return new BackgroundSolid(color);
   }

   public static Background createBitmapBackground(Bitmap bitmap) {
      BackgroundBitmap background = new BackgroundBitmap(bitmap);
      background.setOrigin(0, 0);
      return background;
   }

   public static Background createBitmapBackground(ResourceFetcher resource, String name) {
      BackgroundBitmap background = new NamedBackgroundBitmap(resource, name);
      background.setOrigin(0, 0);
      return background;
   }

   public void draw(Graphics _1, XYRect _2) {
      throw null;
   }

   public boolean freeStaleObject(int priority) {
      return false;
   }

   public boolean isTransparent() {
      throw null;
   }

   public final int getPositionX() {
      return this._positionX;
   }

   public final int getPositionY() {
      return this._positionY;
   }

   public final int getRepeat() {
      return this._repeat;
   }

   public void setPosition(int positionX, int positionY) {
      this._positionX = positionX;
      this._positionY = positionY;
   }

   public void setRepeat(int repeat) {
      this._repeat = repeat;
   }
}
