package net.rim.device.api.ui;

class Screen$NavigationMovementFocusSelector implements Screen$FocusSelector {
   private int _dx;
   private int _dy;
   private int _status;
   private int _time;
   private Screen _screen;
   private boolean _success;
   private static Screen$NavigationMovementFocusSelector _selector = new Screen$NavigationMovementFocusSelector();

   public static Screen$NavigationMovementFocusSelector getSelector(Screen screen, int dx, int dy, int status, int time) {
      _selector._screen = screen;
      _selector._dx = dx;
      _selector._dy = dy;
      _selector._status = status;
      _selector._time = time;
      return _selector;
   }

   @Override
   public void select() {
      this._success = true;
      int remaining = 0;
      if (this._dy != 0) {
         remaining |= this._screen._delegate.moveFocus(this._dy, this._status | 131072, this._time);
      }

      if (this._dx != 0) {
         remaining |= this._screen._delegate.moveFocus(this._dx, this._status | 65536, this._time);
      }

      if (remaining != 0) {
         this._success = false;
      }
   }

   public final boolean getSuccess() {
      return this._success;
   }
}
