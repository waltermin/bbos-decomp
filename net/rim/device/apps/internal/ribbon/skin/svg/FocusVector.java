package net.rim.device.apps.internal.ribbon.skin.svg;

class FocusVector {
   int _dx;
   int _dy;

   boolean remaining() {
      return this._dx != 0 || this._dy != 0;
   }

   int delta(int direction) {
      if (direction == 0) {
         direction = this._dx == 0 ? 2 : 1;
      }

      switch (direction) {
         case 0:
            return 0;
         case 1:
            return this._dx;
         case 2:
         default:
            return this._dy;
      }
   }

   void decrement(int direction, int delta) {
      if (direction == 0) {
         direction = this._dx == 0 ? 2 : 1;
      }

      switch (direction) {
         case 1:
            this._dx -= delta;
         case 0:
            return;
         case 2:
         default:
            this._dy -= delta;
      }
   }

   void set(int direction, int value) {
      if (direction == 0) {
         direction = this._dx == 0 ? 2 : 1;
      }

      switch (direction) {
         case 1:
            this._dx = value;
         case 0:
            return;
         case 2:
         default:
            this._dy = value;
      }
   }
}
