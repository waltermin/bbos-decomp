package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.component.DateField;

final class CalendarViewController$NavDateField extends DateField {
   private boolean _focusable;
   private final CalendarViewController this$0;

   CalendarViewController$NavDateField(CalendarViewController _1, DateFormat dateFormat) {
      super(null, System.currentTimeMillis(), 1152921504606847222L);
      this.this$0 = _1;
      this._focusable = false;
      this.setFormat(dateFormat);
   }

   final void setFocusable(boolean focusable) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected final void fieldChangeNotify(int context) {
      super.fieldChangeNotify(context);
      if ((context & -2147483648) == 0) {
         long newTimeToView = this.getDate();
         this.this$0.loadViewContents(newTimeToView, (byte)0, null, (byte)0, true, true, (byte)0);
      }
   }

   @Override
   protected final boolean keyControl(char key, int status, int time) {
      if ((status & 1) == 0) {
         switch (key) {
            case '\u0080':
               break;
            case '\u0081':
            case '\u0082':
            case '\u0083':
            case '\u0084':
            default:
               return true;
         }
      }

      return super.keyControl(key, status, time);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      return key == '\n' ? true : super.keyChar(key, status, time);
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      if (Trackball.isSupported()) {
         super.invokeAction(1);
         return true;
      }

      SelectDate utilSelect = new SelectDate();
      utilSelect.setDate(System.currentTimeMillis());
      if (utilSelect.doSelection()) {
         this.this$0.loadViewContents(utilSelect.getDate(), (byte)0, null, (byte)0, true, true, (byte)0);
      }

      return true;
   }

   @Override
   public final boolean isFocusable() {
      return this._focusable;
   }
}
