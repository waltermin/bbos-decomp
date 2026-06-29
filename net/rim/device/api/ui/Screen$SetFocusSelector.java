package net.rim.device.api.ui;

class Screen$SetFocusSelector implements Screen$FocusSelector {
   private Field _field;
   private Screen _screen;
   private static Screen$SetFocusSelector _selector = new Screen$SetFocusSelector();

   public static Screen$FocusSelector getSelector(Screen screen, Field field) {
      _selector._screen = screen;
      _selector._field = field;
      return _selector;
   }

   @Override
   public void select() {
      this._screen.onUnfocus();
      this._screen.setFocusChain(this._field);
      this._field = null;
   }
}
