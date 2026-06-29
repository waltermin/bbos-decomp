package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import java.util.Vector;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.calendar.calconstants.CalOptionCache;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarProxy;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.cldc.util.CalendarExtensions;

final class DayController extends CalendarViewController {
   private VerticalFieldManager _listVFM = new VerticalFieldManager(2306142076376449024L);
   private DayTimeOfDayField _tod = new DayTimeOfDayField();
   private DayController$DayViewNavField _navField = new DayController$DayViewNavField(this);
   private CalendarOptions _calendarOptions = CalendarOptions.getOptions();
   private static final int FIRST_CONTROL_START = 0;
   private static final int FIRST_CONTROL_END = 31;
   private static final int SECOND_CONTROL_START = 127;
   private static final int SECOND_CONTROL_END = 159;
   private static final Tag DAY_INDICATOR_SELECTED_TAG = Tag.create("day-indicator-selected");
   private static final Tag DAY_INDICATOR_TAG = Tag.create("day-indicator");
   private static final Tag DAY_FOCUS_HILIGHT_TAG = Tag.create("day-focus-hilight");

   public DayController(CalendarApp calendarUIApplication, CalendarActions calActions) {
      super(calendarUIApplication, calActions, new DayVerbManager(calActions), false, false);
      this.setNavigationFieldForHeader(this._navField);
   }

   @Override
   protected final void initializeAdditionalFields() {
      this._tod.init();
   }

   @Override
   protected final void uninitializeAdditionalFields() {
      this._tod.uninit();
   }

   @Override
   protected final void addAdditionalFields(Screen screen) {
      this._listVFM.add(this._tod.getField());
      screen.add(this._listVFM);
      this._tod.getField().setFocus();
   }

   @Override
   protected final long getSelectedStartTime() {
      return this._tod.getSelectedStartTime();
   }

   @Override
   protected final long getSelectedEndTime() {
      return this._tod.getSelectedEndTime();
   }

   @Override
   protected final Object getSelectedObject() {
      return this._tod.getSelectedObject();
   }

   @Override
   public final void selectedEventChanged(Object event) {
   }

   @Override
   public final void selectedDateChanged(long date) {
   }

   @Override
   public final int moveFocus(Field field, int amount) {
      return 0;
   }

   @Override
   protected final Runnable loadViewContentsNow(
      long time, Object object, boolean updateSelectedDate, boolean reposition, boolean preserveSelectedTime, byte loadType
   ) {
      Runnable r = null;
      Vector events = this._tod.loadDay(time, object);
      return new DayController$DoUIRunnable(this, time, object, updateSelectedDate, reposition, preserveSelectedTime, events);
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
      } else if (key == 132) {
         hkResource = 340;
      } else if (key == 131) {
         hkResource = 341;
      }

      if (hkResource != 0) {
         key = CalendarApp._rb.getString(hkResource).charAt(0);
      }

      return key;
   }

   @Override
   protected final void updateSelectedDate(long selectedDate) {
      this._navField.setSelectedDate(selectedDate);
      super.updateSelectedDate(selectedDate);
   }

   private final boolean performQuickInput(int keycode, char c) {
      long lastTimeViewed = this.getSelectedStartTime();
      CalOptionCache.setTimeWithFocus(lastTimeViewed);
      CalendarProxy calProxy = CalendarProxy.getInstance();
      Object[] inPlaceInputs = calProxy.getRepositoryCopy(-2932280743217917193L);
      if (inPlaceInputs != null && inPlaceInputs.length > 0) {
         Verb inPlace = (Verb)inPlaceInputs[0];
         if (this._tod.popQuickInput(lastTimeViewed, this.getSelectedEndTime() - lastTimeViewed, keycode, c)) {
            CalOptionCache.setObjectWithFocus(inPlace.invoke(null));
            long newTimeToView = CalOptionCache.getTimeWithFocus();
            if (newTimeToView != lastTimeViewed) {
               this.loadViewContents(newTimeToView, (byte)0, null, (byte)0, true, false, (byte)0);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private final void repositionView(boolean preserveSelectedTime) {
      Calendar cal = this.getScratchCalendar();
      CalendarExtensions calEx = (CalendarExtensions)cal;
      int startOfDayOffset = this._tod.getFirstAllDayTransitionOffset();
      if (startOfDayOffset < 0) {
         long selTime = this.getSelectedStartTime();
         calEx.setTimeLong(selTime);
         cal.set(14, 0);
         cal.set(13, 0);
         cal.set(12, 0);
         cal.set(11, (int)((long)this._calendarOptions.getDayStart() / 3600000));
         selTime = calEx.getTimeLong();
         startOfDayOffset = this._tod.getOffsetFromTime(selTime);
      }

      this._tod.makeOffsetVisible(this._tod.getNumberOfItems() - 1);
      this._tod.makeOffsetVisible(startOfDayOffset);
      if (preserveSelectedTime) {
         this._tod.makeOffsetVisible(this._tod.getSelectedOffset());
      } else {
         long currTime = System.currentTimeMillis();
         int offsetWithFocus = 0;
         if (currTime < this._tod.getStartOfList() || currTime > this._tod.getEndOfList()) {
            offsetWithFocus = this._tod.getFirstNonEmptyTransitionOffset();
            if (offsetWithFocus < 0) {
               offsetWithFocus = this._tod.getFirstAllDayTransitionOffset();
            }
         } else if (currTime < this._tod.getVisibleStartOfList()) {
            offsetWithFocus = 0;
         } else if (currTime > this._tod.getVisibleEndOfList()) {
            offsetWithFocus = this._tod.getNumberOfItems() - 1;
         } else {
            offsetWithFocus = this._tod.getOffsetFromTime(currTime);
         }

         if (offsetWithFocus >= 0) {
            this._tod.makeOffsetVisible(offsetWithFocus);
            this._tod.setSelectedOffset(offsetWithFocus);
         } else {
            this._tod.setSelectedOffset(startOfDayOffset);
         }
      }
   }

   private final void moveToDifferentDay(int delta) {
      CalendarExtensions calEx = (CalendarExtensions)this.getScratchCalendar();
      long initialTimeSelection = this._tod.getPreferredTimeSelection();
      if (initialTimeSelection == 0) {
         initialTimeSelection = this.getSelectedStartTime();
      }

      calEx.setTimeLong(initialTimeSelection);
      calEx.add(5, delta);
      this.loadViewContents(calEx.getTimeLong(), (byte)0, null, (byte)0, true, true, true, (byte)0);
   }

   @Override
   public final void optionsChanged(int changedOptions) {
      if ((changedOptions & CalendarOptions.START_OF_DAY) == 0 && (changedOptions & CalendarOptions.END_OF_DAY) == 0) {
         if ((changedOptions & CalendarOptions.FIRST_DOW) != 0) {
            this._navField.setFirstDOW(this._calendarOptions.getFirstDayOfWeek());
         }
      } else {
         this.loadViewContents(0, (byte)1, null, (byte)1, false, false, (byte)1);
      }
   }

   @Override
   public final boolean trackwheelRoll(int amount, int status, int time) {
      super.trackwheelRoll(amount, status, time);
      if (this._listVFM.getFieldWithFocus() == this._tod.getField() && (status & 1) != 0) {
         this.moveToDifferentDay(amount);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      if (dy == 0 && dx != 0 && this._listVFM.getFieldWithFocus() == this._tod.getField()) {
         if ((status & 1) > 0) {
            dx /= Math.abs(dx);
            dx *= 7;
         }

         this.moveToDifferentDay(dx);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            if (Trackball.isSupported()) {
               super.keyChar('\n', 0, 0);
               return true;
            }
         default:
            return super.invokeAction(action);
      }
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      if (this._listVFM.getFieldWithFocus() != this._tod.getField() && key != 27) {
         return false;
      } else {
         return key == '\n' && this._listVFM.getFieldWithFocus() == this._tod.getField() && this.getSelectedObject() == null
            ? this.performQuickInput(0, '\u0000')
            : super.keyChar(key, status, time);
      }
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      boolean handleQuickInput = true;
      if (!this._calendarOptions.isQuickInputTriggeredByKeystrokes() || this._listVFM.getFieldWithFocus() != this._tod.getField()) {
         handleQuickInput = false;
      }

      char c = Keypad.map(keycode);
      if (c >= 0 && c <= 31 || c >= 127 && c <= 159) {
         handleQuickInput = false;
      }

      if (c == ' ' || c == '\b') {
         handleQuickInput = false;
      }

      return !handleQuickInput ? super.keyDown(keycode, time) : this.performQuickInput(keycode, c);
   }
}
