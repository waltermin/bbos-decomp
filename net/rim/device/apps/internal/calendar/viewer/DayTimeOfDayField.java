package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import java.util.Vector;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.calendar.calconstants.CalOptionCache;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.pim.TimeBasedCollection;
import net.rim.device.cldc.util.CalendarExtensions;

final class DayTimeOfDayField extends DayList {
   private TimeBasedCollection _timeBasedCollection;
   private UiApplication _utilApp = UiApplication.getUiApplication();
   private XYPoint _popupLocation = (XYPoint)(new Object());
   private long _visibleStartOfList;
   private long _visibleEndOfList;
   private static final int DAYS_IN_WEEK;
   public static final int TRANSITION_SPACING;
   private static final int NEAR_MILLIS;
   private static Tag TAG_DESCRIPTION_AREA = Tag.create("day-description-area");
   private static Tag TAG_APPOINTMENT = Tag.create("day-appointment");
   private static Tag TAG_DAY = Tag.create("day");

   DayTimeOfDayField() {
      super(true);
   }

   final void init() {
      this._timeBasedCollection = TimeBasedCollection.getInstance();
   }

   final void uninit() {
   }

   @Override
   protected final boolean supportAdvancedThemeing() {
      return false;
   }

   final Vector loadDay(long timeInTheDay, Object object) {
      this.calculateDayBoundaries(timeInTheDay);
      Vector events = (Vector)(new Object());
      long start = this.getStartOfList();
      long end = this.getEndOfList();
      this._timeBasedCollection.getElementsVisibleDuring(start, end - start, super._tz, events);
      return events;
   }

   @Override
   protected final void updateTransitions(Vector events, long loadTime) {
      super.updateTransitions(events, loadTime);
      this._visibleStartOfList = super._list.getAt(0)._timeInMillis;
      this._visibleEndOfList = super._list.getAt(super._numTransitions - 1)._timeInMillis;
   }

   private final void calculateDayBoundaries(long time) {
      Calendar cal = super._cal;
      CalendarExtensions calEx = super._calEx;
      calEx.setTimeLong(time);
      DateTimeUtilities.zeroCalendarTime(cal);
      DateTimeUtilities.verifyStartOfDay(cal, calEx);
      this.setStartOfList(calEx.getTimeLong());
      calEx.add(5, 1);
      DateTimeUtilities.verifyStartOfDay(cal, calEx);
      this.setEndOfList(calEx.getTimeLong());
   }

   @Override
   protected final int calculateMaxTimeWidth(Font font) {
      String timeStr = TimeStringCache.getString(0);
      return font.getBounds(timeStr);
   }

   @Override
   protected final void addDateTimeTransitions() {
      int maxEvents = super._numTransitions;
      int insertionOffset = maxEvents;
      this.sortTransitions();
      int eventOffset = 0;
      Calendar cal = super._cal;
      CalendarExtensions calEx = super._calEx;
      CalendarOptions calendarOptions = CalendarOptions.getOptions();
      int startOfDay = calendarOptions.getDayStart();
      int endOfDay = calendarOptions.getDayEnd();
      if (endOfDay < startOfDay + 3600000) {
         endOfDay = startOfDay + 3600000;
      }

      if (endOfDay >= 86400000) {
         endOfDay = 86399999;
      }

      calEx.setTimeLong(this.getStartOfList());
      DateTimeUtilities.calendarSetWithDst(cal, calEx, 11, startOfDay / 3600000);
      long timeSequence = calEx.getTimeLong();
      DateTimeUtilities.calendarSetWithDst(cal, calEx, 11, endOfDay / 3600000 + 1);
      long timeEnd = calEx.getTimeLong();

      do {
         DayList$Transition eventTransition;
         if (eventOffset < maxEvents) {
            eventTransition = super._list.getAt(eventOffset);
         } else {
            eventTransition = null;
         }

         if (eventTransition != null && timeSequence + 300000 >= eventTransition._timeInMillis) {
            eventOffset++;
            if (eventTransition._transitionType != 2 && timeSequence - 300000 < eventTransition._timeInMillis) {
               timeSequence += 3600000;
            }
         } else {
            if (timeSequence < timeEnd) {
               super._list.setTransition(insertionOffset, (byte)0, timeSequence, 0);
               insertionOffset++;
            }

            timeSequence += 3600000;
         }
      } while (timeSequence < timeEnd || eventOffset + 1 < maxEvents);

      super._numTransitions = insertionOffset;
      super._list.clearTransitions(insertionOffset);
      this.sortTransitions();
   }

   final boolean popQuickInput(long initialStart, long initialDuration, int initialKey, char initialChar) {
      this.getPopupLocation(this._popupLocation);
      InPlaceEventDialog quickInputDialog = new InPlaceEventDialog(this._popupLocation.x, this._popupLocation.y);
      long start = initialStart;
      long duration = initialDuration / 60000;
      duration *= 60000;
      if (duration < 0) {
         duration = 3600000;
      }

      quickInputDialog.setStart(start);
      quickInputDialog.setDuration(duration);
      if (initialChar != 0) {
         quickInputDialog.seedEventDescription(initialKey, initialChar);
      }

      this._utilApp.pushModalScreen(quickInputDialog);
      if (quickInputDialog.userCancelled()) {
         return false;
      }

      start = quickInputDialog.getStart();
      duration = quickInputDialog.getDuration();
      String subject = quickInputDialog.getEventDescription();
      CalOptionCache.setTimeWithFocus(start);
      CalOptionCache.setSuggestedUserDuration(duration);
      CalOptionCache.setSuggestedUserText(subject);
      return true;
   }

   final long getVisibleStartOfList() {
      return this._visibleStartOfList;
   }

   final long getVisibleEndOfList() {
      return this._visibleEndOfList;
   }

   @Override
   protected final String getTimeText(DayList$Transition entryToDisplay) {
      return entryToDisplay._displayTime;
   }

   @Override
   public final int getBackgroundColor() {
      ThemeAttributeSet attr = ThemeManager.getActiveTheme().getAttributeSet(TAG_DAY);
      return attr.getColor(0);
   }

   @Override
   protected final ThemeAttributeSet getDescriptionAreaTheme() {
      return ThemeManager.getActiveTheme().getAttributeSet(TAG_DESCRIPTION_AREA);
   }

   @Override
   protected final ThemeAttributeSet getAppointmentTheme() {
      return ThemeManager.getActiveTheme().getAttributeSet(TAG_APPOINTMENT);
   }

   @Override
   public final AccessibleContext getAccessibleChildAt(int index) {
      return super.getAccessibleChildAt(index);
   }
}
