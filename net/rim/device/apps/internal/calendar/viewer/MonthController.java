package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;

final class MonthController extends CalendarViewController {
   private MonthTimeInMonth _tim = new MonthTimeInMonth();

   public MonthController(CalendarApp calendarUIApplication, CalendarActions calActions) {
      super(calendarUIApplication, calActions, new MonthVerbManager(calActions), true, true);
      this.setDelayedHeaderRendering(true);
   }

   @Override
   protected final void initializeAdditionalFields() {
      this._tim.init(this);
   }

   @Override
   protected final void uninitializeAdditionalFields() {
      this._tim.uninit();
   }

   @Override
   protected final void addAdditionalFields(Screen screen) {
      screen.add(this._tim.getField());
      this._tim.getField().setFocus();
   }

   @Override
   protected final long getSelectedStartTime() {
      return this._tim.getSelectedStartTime();
   }

   @Override
   protected final long getSelectedEndTime() {
      return this._tim.getSelectedEndTime();
   }

   @Override
   protected final Object getSelectedObject() {
      return null;
   }

   @Override
   public final void selectedEventChanged(Object event) {
   }

   @Override
   public final int moveFocus(Field field, int amount) {
      int monthChanged = this._tim.moveFocus((MonthField)field, amount);
      if (monthChanged == 0) {
         long time = this._tim.getSelectedStartTime();
         this.loadViewContents(time, (byte)0, null, (byte)1, true, true, (byte)1);
      }

      return 0;
   }

   @Override
   protected final Runnable loadViewContentsNow(
      long time, Object object, boolean updateSelectedDate, boolean reposition, boolean preserveSelectedTime, byte loadType
   ) {
      return this._tim.loadMonth(time);
   }

   @Override
   protected final boolean onDown(int eventTime, long lastTimeViewed, Screen screen) {
      return screen.dispatchTrackwheelEvent(519, 1, 1, eventTime);
   }

   @Override
   protected final boolean onUp(int eventTime, long lastTimeViewed, Screen screen) {
      return screen.dispatchTrackwheelEvent(519, -1, 1, eventTime);
   }

   @Override
   protected final boolean onRight(int eventTime, long lastTimeViewed, Screen screen) {
      return screen.dispatchTrackwheelEvent(519, 1, 0, eventTime);
   }

   @Override
   protected final boolean onLeft(int eventTime, long lastTimeViewed, Screen screen) {
      return screen.dispatchTrackwheelEvent(519, -1, 0, eventTime);
   }

   @Override
   protected final char mapKey(char key, int status) {
      int hkResource = 0;
      if (key == ' ') {
         if ((status & 2) != 0) {
            hkResource = 341;
         } else {
            hkResource = 340;
         }
      } else if (key == 132 && (status & 1) != 0) {
         hkResource = 340;
      } else if (key == 131 && (status & 1) != 0) {
         hkResource = 341;
      }

      if (hkResource != 0) {
         key = CalendarApp._rb.getString(hkResource).charAt(0);
      }

      return key;
   }

   @Override
   public final void optionsChanged(int changedOptions) {
      if ((changedOptions & CalendarOptions.FIRST_DOW) != 0) {
         this._tim.setStartingDOW(CalendarOptions.getOptions().getFirstDayOfWeek() - 1);
      }
   }

   @Override
   public final void selectedDateChanged(long selectedDate) {
      this.updateSelectedDate(selectedDate);
   }

   @Override
   public final void dateFormatChanged() {
      this.updateCurrentTime();
   }
}
