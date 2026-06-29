package net.rim.device.apps.internal.explorer.Media;

final class SmartlistScreen$ButtonInfo {
   private int _type;
   private String _string;
   private boolean _isRemove;

   SmartlistScreen$ButtonInfo(int type, String string, boolean isRemove) {
      this._type = type;
      this._string = string;
      this._isRemove = isRemove;
   }

   final int getType() {
      return this._type;
   }

   final String getString() {
      return this._string;
   }

   final boolean isRemove() {
      return this._isRemove;
   }
}
