package net.rim.device.api.ui;

class Screen$FindNewFocusSelector implements Screen$FocusSelector {
   private boolean _down;
   private Screen _screen;
   private static Screen$FindNewFocusSelector _selector = new Screen$FindNewFocusSelector();

   public static Screen$FocusSelector getSelector(Screen screen, boolean down) {
      _selector._screen = screen;
      _selector._down = down;
      return _selector;
   }

   @Override
   public void select() {
      int amount = this._down ? 1 : -1;
      Manager delegate = this._screen._delegate;
      if (delegate.moveFocus(amount, 32768, 0) != 0) {
         this._screen.onUnfocus();
         if (!delegate.isFocusable()) {
            return;
         }

         this._screen.onFocus(-amount);
         if (delegate.moveFocus(-amount, 32768, 0) != 0) {
            this._screen.onUnfocus();
            return;
         }
      }
   }
}
