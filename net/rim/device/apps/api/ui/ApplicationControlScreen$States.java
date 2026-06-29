package net.rim.device.apps.api.ui;

class ApplicationControlScreen$States {
   private boolean[] _state = new boolean[64];
   private boolean[] _nonDefault = new boolean[64];

   void setState(int index, boolean value) {
      this._state[index] = value;
   }

   boolean getState(int index) {
      return this._state[index];
   }

   void setNonDefault(int index, boolean value) {
      this._nonDefault[index] = value;
   }

   boolean getNonDefault(int index) {
      return this._nonDefault[index];
   }
}
