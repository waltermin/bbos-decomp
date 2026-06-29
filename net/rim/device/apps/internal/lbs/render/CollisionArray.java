package net.rim.device.apps.internal.lbs.render;

import net.rim.device.api.system.Display;
import net.rim.device.api.util.Arrays;

final class CollisionArray {
   int _height;
   int _width;
   public static int BOX_SIZE = 4;
   static byte[] _data;

   CollisionArray() {
      this._height = Display.getHeight() / BOX_SIZE;
      this._width = Display.getWidth() / BOX_SIZE;
      _data = new byte[this._height * this._width];
      this.clear();
   }

   final void clear() {
      Arrays.fill(_data, (byte)0);
   }

   final int clamp(int i, int min, int max) {
      if (i < min) {
         return min;
      } else {
         return i > max ? max : i;
      }
   }

   final void markArea(int x, int y, int width, int height) {
      int startX = this.clamp(x / BOX_SIZE, 0, this._width - 1);
      int endX = this.clamp((x + width) / BOX_SIZE, 0, this._width - 1);
      int startY = this.clamp(y / BOX_SIZE, 0, this._height - 1);
      int endY = this.clamp((y + height) / BOX_SIZE, 0, this._height - 1);
      int offset = 0;

      for (int j = startY; j <= endY; j++) {
         offset = j * this._width;

         for (int i = startX; i <= endX; i++) {
            _data[offset + i] = 1;
         }
      }
   }

   final void unmarkArea(int x, int y, int width, int height) {
      int startX = this.clamp(x / BOX_SIZE, 0, this._width - 1);
      int endX = this.clamp((x + width) / BOX_SIZE, 0, this._width - 1);
      int startY = this.clamp(y / BOX_SIZE, 0, this._height - 1);
      int endY = this.clamp((y + height) / BOX_SIZE, 0, this._height - 1);
      int offset = 0;

      for (int j = startY; j <= endY; j++) {
         offset = j * this._width;

         for (int i = startX; i <= endX; i++) {
            _data[offset + i] = 0;
         }
      }
   }

   final boolean testArea(int x, int y, int width, int height) {
      int startX = this.clamp(x / BOX_SIZE, 0, this._width - 1);
      int endX = this.clamp((x + width) / BOX_SIZE, 0, this._width - 1);
      int startY = this.clamp(y / BOX_SIZE, 0, this._height - 1);
      int endY = this.clamp((y + height) / BOX_SIZE, 0, this._height - 1);
      int offset = 0;

      for (int j = startY; j <= endY; j++) {
         offset = j * this._width;

         for (int i = startX; i <= endX; i++) {
            if (_data[offset + i] != 0) {
               return true;
            }
         }
      }

      return false;
   }

   final boolean testArea(int x, int y, int width, int height, int padding) {
      int startX = this.clamp((x - padding) / BOX_SIZE, 0, this._width - 1);
      int endX = this.clamp((x + padding + width) / BOX_SIZE, 0, this._width - 1);
      int startY = this.clamp((y - padding) / BOX_SIZE, 0, this._height - 1);
      int endY = this.clamp((y + padding + height) / BOX_SIZE, 0, this._height - 1);
      int offset = 0;

      for (int j = startY; j <= endY; j++) {
         offset = j * this._width;

         for (int i = startX; i <= endX; i++) {
            if (_data[offset + i] != 0) {
               return true;
            }
         }
      }

      return false;
   }
}
