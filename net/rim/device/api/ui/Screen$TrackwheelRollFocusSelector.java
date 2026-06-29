package net.rim.device.api.ui;

class Screen$TrackwheelRollFocusSelector implements Screen$FocusSelector {
   private int _amount;
   private int _status;
   private int _time;
   private Screen _screen;
   private boolean _success;
   private static Screen$TrackwheelRollFocusSelector _selector = new Screen$TrackwheelRollFocusSelector();

   public static Screen$TrackwheelRollFocusSelector getSelector(Screen screen, int amount, int status, int time) {
      _selector._screen = screen;
      _selector._amount = amount;
      _selector._status = status;
      _selector._time = time;
      return _selector;
   }

   @Override
   public void select() {
      this._success = true;
      int remaining = this._screen._delegate.moveFocus(this._amount, this._status, this._time);
      if (remaining != 0) {
         this._success = false;
      }
   }

   public final boolean getSuccess() {
      return this._success;
   }
}
