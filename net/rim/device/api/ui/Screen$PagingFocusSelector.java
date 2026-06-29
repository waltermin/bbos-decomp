package net.rim.device.api.ui;

class Screen$PagingFocusSelector implements Screen$FocusSelector {
   private int _status;
   private Screen _screen;
   private boolean _success;
   private static Screen$PagingFocusSelector _selector = new Screen$PagingFocusSelector();

   public static Screen$PagingFocusSelector getSelector(Screen screen, int status) {
      _selector._screen = screen;
      _selector._status = status;
      return _selector;
   }

   @Override
   public void select() {
      this._success = true;
      this._screen._delegate.moveFocus(this._status);
   }

   public final boolean getSuccess() {
      return this._success;
   }
}
