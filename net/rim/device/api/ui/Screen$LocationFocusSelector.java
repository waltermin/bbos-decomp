package net.rim.device.api.ui;

class Screen$LocationFocusSelector implements Screen$FocusSelector {
   private int _x;
   private int _y;
   private int _status;
   private int _time;
   private Field _field;
   private boolean _success;
   private static Screen$LocationFocusSelector _selector = new Screen$LocationFocusSelector();

   public static Screen$LocationFocusSelector getSelector(Field field, int x, int y, int status, int time) {
      _selector._field = field;
      _selector._x = x;
      _selector._y = y;
      _selector._status = status;
      _selector._time = time;
      return _selector;
   }

   @Override
   public void select() {
      Field field = this._field;
      if (field == null) {
         this._success = false;
      } else {
         Screen screen = field.getScreen();
         Field original = screen.getLeafFieldWithFocus();
         screen.onUnfocus();
         this._success = field.moveFocusToPoint(this._x, this._y, this._status, this._time);
         if (this._success) {
            field.focusChangeNotify(1);

            for (Manager manager = field.getManager(); manager != screen; manager = manager.getManager()) {
               manager.focusChangeNotify(1);
               manager.setFieldWithFocus(field);
               field = manager;
            }
         } else if (original != null) {
            original.setFocus();
         }

         this._field = null;
      }
   }

   public final boolean getSuccess() {
      return this._success;
   }
}
