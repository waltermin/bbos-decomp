package net.rim.device.cldc.io.commlink;

import net.rim.device.api.ui.Graphics;

final class ProfileString {
   private byte[] _data = new byte[128];
   private int _pointer;
   private static final int SIZE = 128;
   private static final int MODULO = 127;

   final void add(int code) {
      this._data[this._pointer] = (byte)code;
      this._pointer = this._pointer + 1 & 127;
   }

   final void add(int code, int subcode) {
      this._data[this._pointer] = (byte)code;
      this._pointer = this._pointer + 1 & 127;
      this._data[this._pointer] = (byte)subcode;
      this._pointer = this._pointer + 1 & 127;
   }

   final void drawYourself(Graphics graphics, int x, int y, int width) {
      int index = this._pointer + 1 & 127;
      int curx = x;
      int height = graphics.getFont().getHeight();

      while (index != this._pointer) {
         if (this._data[index] != 0) {
            curx += graphics.drawText((char)this._data[index], curx, y, 54, width);
            if (curx > width - 20) {
               curx = x;
               y += height;
            }
         }

         index = index + 1 & 127;
      }
   }
}
