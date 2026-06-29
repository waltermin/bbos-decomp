package net.rim.device.apps.internal.ribbon.skin.svg;

class FocusItem {
   String _id;
   String _left;
   String _right;
   String _up;
   String _down;

   FocusItem(String id, String left, String right, String up, String down) {
      this._id = id;
      this._left = left;
      this._right = right;
      this._down = down;
      this._up = up;
   }
}
