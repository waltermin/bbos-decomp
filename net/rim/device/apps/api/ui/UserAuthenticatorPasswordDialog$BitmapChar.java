package net.rim.device.apps.api.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.tid.text.AttributedString$Picture;
import net.rim.tid.text.AttributedString$PictureInfo;

class UserAuthenticatorPasswordDialog$BitmapChar implements AttributedString$Picture {
   private Bitmap _bitmap;
   private AttributedString$PictureInfo _info;

   UserAuthenticatorPasswordDialog$BitmapChar(Bitmap bitmap) {
      this._bitmap = bitmap;
      this._info = (AttributedString$PictureInfo)(new Object(this));
      this._info._x = 0;
      this._info._width = this._bitmap.getWidth();
      this._info._height = this._bitmap.getHeight();
      this._info._advance = this._bitmap.getWidth() + 1;
      int leading = this._info._height >= 20 ? 3 : (this._info._height >= 10 ? 2 : 1);
      this._info._y = -this._info._height + leading;
   }

   @Override
   public AttributedString$PictureInfo getInfo() {
      return this._info;
   }

   @Override
   public void draw(Graphics g, int x, int y) {
      g.drawBitmap(x, y, this._bitmap.getWidth(), this._bitmap.getHeight(), this._bitmap, 0, 0);
   }
}
