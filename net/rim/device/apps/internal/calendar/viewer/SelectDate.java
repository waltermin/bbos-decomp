package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.TrackwheelListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.container.PopupScreen;

final class SelectDate implements TrackwheelListener, KeyListener {
   long _date;
   boolean _userEscaped = true;

   final void setDate(long initial) {
      this._date = initial;
   }

   final long getDate() {
      return this._date;
   }

   final boolean doSelection() {
      DateField df = (DateField)(new Object(null, this._date, DateFormat.getInstance(40), 192));
      PopupScreen pickDate = new SelectDate$SelectScreen(this);
      pickDate.add((Field)(new Object(CalendarApp._rb.getString(320))));
      pickDate.add(df);
      pickDate.addTrackwheelListener(this);
      UiApplication.getUiApplication().pushModalScreen(pickDate);
      if (!this._userEscaped) {
         this._date = df.getDate();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         this._userEscaped = true;
         UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
         return true;
      } else if (key == '\n') {
         this._userEscaped = false;
         UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      this._userEscaped = false;
      UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
      return true;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean trackwheelRoll(int amount, int status, int time) {
      return (status & 1) == 0;
   }

   @Override
   public final boolean trackwheelUnclick(int status, int time) {
      return false;
   }
}
