package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.controller.Duration;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MeetingInfoProvider;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MultiServiceEvent;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.pim.TimeBasedCollection;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.calendar.eventprovider.EventImpl;
import net.rim.device.cldc.util.CalendarExtensions;

final class WeekField extends Field {
   private int _labelHeight;
   private int _halfLabelHeight;
   private int _bufferedHeight;
   private int _heightToStart;
   private int _topMoreFirstY;
   private int _topMoreSecondY;
   private long _millisecondsPerPixel = 360000;
   private int _halfHourInPixels;
   private int _minimumHourInPixels;
   private long _timeToView = System.currentTimeMillis();
   private int _startingDOW;
   private int _firstDayDisplayed;
   private int _dayWithFocusIndex;
   private Calendar _cal = Calendar.getInstance();
   private TimeZone _tz = this._cal.getTimeZone();
   private WeekField$WeekFieldDayContents[] _days = new WeekField$WeekFieldDayContents[]{
      new WeekField$WeekFieldDayContents(this),
      new WeekField$WeekFieldDayContents(this),
      new WeekField$WeekFieldDayContents(this),
      new WeekField$WeekFieldDayContents(this),
      new WeekField$WeekFieldDayContents(this),
      new WeekField$WeekFieldDayContents(this),
      new WeekField$WeekFieldDayContents(this)
   };
   private StringBuffer _yearText = (StringBuffer)(new Object());
   private StringBuffer _monthText = (StringBuffer)(new Object());
   private SimpleDateFormat _yearFormatter = (SimpleDateFormat)(new Object("yyyy"));
   private SimpleDateFormat _monthFormatter = (SimpleDateFormat)(new Object("MMM"));
   private StringBuffer _tempBuffer = (StringBuffer)(new Object());
   private String[] _dow = CommonResources.getStringArray(116);
   private String[] _dowMedium = CommonResources.getStringArray(9094);
   private XYRect _focusRect = (XYRect)(new Object());
   private long _topOfScreenFromEndOfDay;
   private int _startHour;
   private int _endHour;
   private CalendarViewController _callback;
   private boolean _scrollByHour = true;
   private boolean _drawTimeLines = true;
   private Object _selectedEvent;
   private boolean _weekLoaded;
   private char[] _dom = new char[14];
   private int _adjustedDayWidth;
   private int _adjustedTotalWeekWidth;
   private int _widthOfDurationBar;
   private int _widthForDurationBar;
   private int _adjustedWeekHeight;
   private int _defaultWeekHeight;
   private boolean _lowResolution;
   private int _leftColumnWidth;
   private TimeBasedCollection _timeBasedCollection;
   private XYRect _dayHeaderInvalidFocusRect = (XYRect)(new Object());
   private Vector _eventVector = (Vector)(new Object());
   private ThemeAttributeSet _baseAttr;
   private ThemeAttributeSet _daysOfWeekAttr;
   private ThemeAttributeSet _currentDayFocusHilightAttr;
   private ThemeAttributeSet _focusHilightAttr;
   private ThemeAttributeSet _currentDayVectorAttr;
   private ThemeAttributeSet _dateAttr;
   private ThemeAttributeSet _timeAttr;
   private ThemeAttributeSet _viewAttr;
   private ThemeAttributeSet _weekendAttr;
   private ThemeAttributeSet _weekdayAttr;
   private ThemeAttributeSet _allDayAttr;
   private ThemeAttributeSet _moreAttr;
   private ThemeAttributeSet _appointmentBorderAttr;
   private ThemeAttributeSet _meetingAttr;
   private ThemeAttributeSet _simpleAppointmentAttr;
   private Theme _theme;
   private Font _fontNormal;
   private Font _fontBold;
   private boolean _useMultiServiceColors;
   private static final int NUM_DAYS;
   private static final int NUM_DAYS_MINUS_ONE;
   private static final int GAP_START;
   private static final int WIDTH_OF_GAP;
   private static final int MIN_TIME_COLUMN_WIDTH;
   private static final int TIME_COLUMN_RIGHT_MARGIN;
   private static final int TIME_COLUMN_LEFT_MARGIN;
   private static final int WEEK_SEPARATOR_WIDTH;
   private static final int MAX_SLOTS_VISIBLE;
   private static final long HALF_HOUR_IN_MILLISECONDS;
   private static final byte EMPHASIZED_PROP;
   private static final byte NO_DUR_BAR_PROP;
   private static final int DASHED_LINE_STIPPLE;
   private static final int DASHED_LINE_STIPPLE_COLOUR;
   private static final int SOLID_LINE_STIPPLE;
   private static final Bitmap EMPHASIS_BITMAP = Bitmap.getBitmapResource("EmphasizedDay.gif");
   private static long[] _slotInUseUntil = new long[3];
   private static WeekField$EventEntryComparator _eeComparator = new WeekField$EventEntryComparator();
   private static final char ZERO;
   private static final Tag BASE_TAG = Tag.create("week");
   private static final Tag DATE_TAG = Tag.create("week-date");
   private static final Tag DAYSOFWEEK_TAG = Tag.create("week-daysofweek");
   private static final Tag TIME_TAG = Tag.create("week-time");
   private static final Tag VIEW_TAG = Tag.create("week-view");
   private static final Tag SIMPLE_APPOINTMENT_TAG = Tag.create("week-simple-appointment");
   private static final Tag MEETING_TAG = Tag.create("week-meeting");
   private static final Tag ALLDAY_TAG = Tag.create("week-allday");
   private static final Tag MORE_TAG = Tag.create("week-more");
   private static final Tag APPOINTMENT_BORDER_TAG = Tag.create("week-appointment-border");
   private static final Tag WEEKDAY_TAG = Tag.create("week-weekday");
   private static final Tag WEEKEND_TAG = Tag.create("week-weekend");
   private static final Tag FOCUS_HILIGHT_TAG = Tag.create("week-focus-hilight");
   private static final Tag CURRENT_DAY_TAG = Tag.create("day-indicator-selected");
   private static final Tag CURRENT_DAY_TAG_VECTOR = Tag.create("week-current-day");
   private static final Tag CURRENT_DAY_FOCUS_HILIGHT_TAG = Tag.create("week-current-day-focus");

   public WeekField() {
      super(18014398509481984L);
      this.setTag(BASE_TAG);
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      this._theme = ThemeManager.getActiveTheme();
      this._fontNormal = this.getFont().derive(0);
      this._fontBold = this._fontNormal.derive(1);
      this._baseAttr = this._theme.getAttributeSet(BASE_TAG);
      this._daysOfWeekAttr = this._theme.getAttributeSet(DAYSOFWEEK_TAG);
      this._currentDayFocusHilightAttr = this._theme.getAttributeSet(CURRENT_DAY_FOCUS_HILIGHT_TAG);
      this._focusHilightAttr = this._theme.getAttributeSet(FOCUS_HILIGHT_TAG);
      this._currentDayVectorAttr = this._theme.getAttributeSet(CURRENT_DAY_TAG_VECTOR);
      this._dateAttr = this._theme.getAttributeSet(DATE_TAG);
      this._timeAttr = this._theme.getAttributeSet(TIME_TAG);
      this._viewAttr = this._theme.getAttributeSet(VIEW_TAG);
      this._weekendAttr = this._theme.getAttributeSet(WEEKEND_TAG);
      this._weekdayAttr = this._theme.getAttributeSet(WEEKDAY_TAG);
      this._allDayAttr = this._theme.getAttributeSet(ALLDAY_TAG);
      this._moreAttr = this._theme.getAttributeSet(MORE_TAG);
      this._appointmentBorderAttr = this._theme.getAttributeSet(APPOINTMENT_BORDER_TAG);
      this._meetingAttr = this._theme.getAttributeSet(MEETING_TAG);
      this._simpleAppointmentAttr = this._theme.getAttributeSet(SIMPLE_APPOINTMENT_TAG);
   }

   public final void init(CalendarViewController callback) {
      this._callback = callback;
      this._timeBasedCollection = TimeBasedCollection.getInstance();
   }

   public final void uninit() {
      this._callback = null;
      this._timeBasedCollection = null;
   }

   public final void loadWeek(long timeToView, Vector events) {
      this._useMultiServiceColors = CalendarOptions.getOptions().getAppointmentColorMode() != 0;
      events.setSize(0);
      if (this._timeBasedCollection != null) {
         this._timeBasedCollection.getElementsVisibleDuring(timeToView - 604800000, 1209600000, this._tz, events);
      }
   }

   public final void paintWeek(long timeToView, Object object, Vector events) {
      this._timeToView = timeToView;
      ((CalendarExtensions)this._cal).setTimeLong(this._timeToView);
      this._dayWithFocusIndex = this._cal.get(7) - 1;
      this.calculateDayStarts();
      this.populateWeek(events);
      this.calculateDayInfo();
      Object selectedEvent;
      if (object != null && this._days[this._dayWithFocusIndex].setEntryWithFocus(object)) {
         selectedEvent = object;
      } else {
         selectedEvent = this.getSelectedObject();
      }

      if (selectedEvent != this._selectedEvent) {
         this._selectedEvent = selectedEvent;
         this._callback.selectedEventChanged(selectedEvent);
      }

      this._weekLoaded = true;
   }

   private final void populateWeek(Vector events) {
      for (int i = 0; i < 7; i++) {
         this._days[i].setDayPopulated(false);
      }

      int firstDay = this._firstDayDisplayed;
      int numEvents = events.size();

      for (int i = 0; i < numEvents; i++) {
         RIMModel calElement = (RIMModel)events.elementAt(i);
         Duration currDur = (Duration)calElement;
         long eventStart = currDur.getStart(this._tz);
         long eventEnd = eventStart + currDur.getDuration(this._tz);

         for (int j = 0; j < 7; j++) {
            long dayStart = this._days[j].getSOD();
            long dayEnd = this._days[j].getEOD();
            if (eventStart < dayEnd && (eventEnd > dayStart || eventStart >= dayStart)) {
               this._days[j].addEvent(calElement, eventStart, eventEnd);
            }
         }
      }

      for (int i = 0; i < 7; i++) {
         this._days[i].setDayPopulated(true);
      }
   }

   public final void setInitialDOWs(int startingDOW, int firstDOWDisplayed) {
      if (startingDOW >= 0 && startingDOW <= 6) {
         this._startingDOW = startingDOW;
      }

      if (firstDOWDisplayed >= 0 && firstDOWDisplayed <= 6) {
         this._firstDayDisplayed = firstDOWDisplayed;
      }

      this.invalidate();
   }

   public final void setStartAndEndOfDay(int startHour, int endHour) {
      this._startHour = startHour;
      this._endHour = endHour;
      this._topOfScreenFromEndOfDay = (24 - this._startHour) * 3600000;
      if (this._weekLoaded) {
         if (this.scaleWeekWindow()) {
            this.updateLayout();
         }

         if (this.performEventLayout(false, true) | this.adjustTopOfScreenOffset()) {
            this.invalidate();
         }
      }
   }

   public final long getSelectedStartTime() {
      this._timeToView = this._days[this._dayWithFocusIndex].getStartOfFocus();
      return this._timeToView;
   }

   public final long getSelectedEndTime() {
      return this._days[this._dayWithFocusIndex].getEndOfFocus();
   }

   public final Object getSelectedObject() {
      WeekField$EventEntry entryWithFocus = this._days[this._dayWithFocusIndex].getEntryWithFocus();
      return entryWithFocus != null ? entryWithFocus._object : null;
   }

   private final boolean performEventLayout(boolean repopulateIfNecessary, boolean forceReLayout) {
      boolean needRepaint = false;
      Calendar cal = this._cal;
      CalendarExtensions calEx = (CalendarExtensions)cal;

      for (int i = 0; i < 7; i++) {
         WeekField$WeekFieldDayContents day = this._days[i];
         if (repopulateIfNecessary && !day.isDayPopulated()) {
            day.populateDay();
            needRepaint = true;
         }

         if (forceReLayout || !day.isDayLaidOut()) {
            calEx.setTimeLong(day.getSOD());
            DateTimeUtilities.calendarSetWithDst(cal, calEx, 11, this._startHour);
            long dayStart = calEx.getTimeLong();
            DateTimeUtilities.calendarSetWithDst(cal, calEx, 11, this._endHour);
            long dayEnd = calEx.getTimeLong();
            if (this._startHour > 0) {
               dayStart -= 1800000;
            }

            if (this._endHour < 24) {
               dayEnd += 1800000;
            }

            day.setPreferredSODAndEOD(dayStart, dayEnd);
            day.layoutDay();
            needRepaint = true;
         }
      }

      this._days[this._dayWithFocusIndex].setFocus(this._timeToView);
      return needRepaint;
   }

   private final void calculateDayInfo() {
      Calendar cal = this._cal;
      CalendarExtensions calEx = (CalendarExtensions)cal;
      calEx.setTimeLong(this._timeToView);
      this._yearText.setLength(0);
      this._yearFormatter.format(cal, this._yearText, null);
      this._monthText.setLength(0);
      this._monthFormatter.format(cal, this._monthText, null);
      if (this.performEventLayout(true, false) | this.adjustTopOfScreenOffset()) {
         this.invalidate();
      }
   }

   private final void calculateDayStarts() {
      int numToProcess = (7 + this._dayWithFocusIndex - this._firstDayDisplayed) % 7;
      Calendar cal = this._cal;
      CalendarExtensions calEx = (CalendarExtensions)cal;
      calEx.setTimeLong(this._timeToView);
      calEx.add(5, -numToProcess);
      DateTimeUtilities.zeroCalendarTime(cal);
      DateTimeUtilities.verifyStartOfDay(cal, calEx);
      int currDay = cal.get(7) - 1;
      int prevDay = currDay;

      for (int i = 0; i < 7; i++) {
         WeekField$WeekFieldDayContents currDayContents = this._days[currDay];
         WeekField$WeekFieldDayContents prevDayContents = this._days[prevDay];
         long currDayTime = calEx.getTimeLong();
         if (currDayTime != currDayContents.getSOD()) {
            this._days[currDay].setDayPopulated(false);
            int currDOM = cal.get(5);
            this._dom[currDay * 2] = (char)(48 + currDOM / 10);
            this._dom[currDay * 2 + 1] = (char)(48 + currDOM % 10);
         }

         currDayContents.setSOD(currDayTime);
         prevDayContents.setEOD(currDayTime);
         int dstOffset = cal.get(16);
         currDayContents.setStartDstOffset(dstOffset);
         prevDayContents.setEndDstOffset(dstOffset);
         calEx.add(5, 1);
         if (DateTimeUtilities.verifyStartOfDay(cal, calEx)) {
            prevDayContents.resetDstOffsets();
            currDayContents.resetDstOffsets();
         }

         prevDay = currDay++;
         currDay %= 7;
      }

      this._days[prevDay].setEOD(calEx.getTimeLong());
      this._days[prevDay].setEndDstOffset(cal.get(16));
      calEx.add(5, 1);
      if (DateTimeUtilities.verifyStartOfDay(cal, calEx)) {
         this._days[prevDay].resetDstOffsets();
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      XYRect clip = graphics.getClippingRect();
      ThemeAttributeSet attr = this._theme.getAttributeSet(BASE_TAG);
      int cellBorderColor = ThemeAttributeSet.getColor(this, 1);
      int bottom_more_first_y = this._adjustedWeekHeight - this._bufferedHeight - 1;
      int bottom_more_second_y = bottom_more_first_y - 1;
      int spaceToBar = this._widthForDurationBar - this._widthOfDurationBar >> 1;
      int moreFirstEndX = this._adjustedDayWidth >> 1;
      int moreFirstStartX = moreFirstEndX - 1;
      int moreSecondStartX = moreFirstStartX - 1;
      int moreSecondEndX = moreFirstEndX + 1;
      char[] domString = this._dom;
      int currDayOffset = this._firstDayDisplayed;
      long currentTime = System.currentTimeMillis();
      if (clip.y < this._labelHeight) {
         graphics.pushContext(this._leftColumnWidth, 0, Display.getWidth(), this._labelHeight, 0, 0);
         if (this._daysOfWeekAttr != null) {
            this._daysOfWeekAttr.applyToGraphics(graphics);
         }

         for (int i = 0; i < 7; i++) {
            if (!this._days[currDayOffset].isDayEmphasized() && currDayOffset != 6 && currDayOffset != 0) {
               graphics.setFont(this._fontNormal);
            } else {
               graphics.setFont(this._fontBold);
            }

            int textX = this._leftColumnWidth + i * this._adjustedDayWidth;
            int prevColor = graphics.getColor();
            if (this._days[currDayOffset] != this._days[this._dayWithFocusIndex]) {
               if (this._days[currDayOffset].getSOD() <= currentTime && currentTime < this._days[currDayOffset].getEOD()) {
                  ThemeAttributeSet focusAttr = null;
                  if (Graphics.isColor()) {
                     focusAttr = this._currentDayVectorAttr;
                     graphics.setColor(focusAttr != null ? focusAttr.getColor(0) : 11119017);
                     graphics.fillRect(textX, 0, this._adjustedDayWidth, this._labelHeight);
                     graphics.setColor(focusAttr != null ? focusAttr.getColor(1) : 16777215);
                  }
               }
            } else if (Graphics.isColor()) {
               if (this._days[currDayOffset].getSOD() <= currentTime && currentTime < this._days[currDayOffset].getEOD()) {
                  ThemeAttributeSet focusAttr = this._currentDayFocusHilightAttr;
                  graphics.setColor(focusAttr != null ? focusAttr.getColor(0) : 255);
                  graphics.fillRect(textX, 0, this._adjustedDayWidth, this._labelHeight);
                  graphics.setColor(focusAttr != null ? focusAttr.getColor(1) : 16777215);
               } else {
                  ThemeAttributeSet focusAttr = this._focusHilightAttr;
                  graphics.setColor(focusAttr != null ? focusAttr.getColor(0) : 255);
                  graphics.fillRect(textX, 0, this._adjustedDayWidth, this._labelHeight);
                  graphics.setColor(focusAttr != null ? focusAttr.getColor(1) : 16777215);
               }
            }

            if (graphics.getFont().getAdvance(this._dowMedium[currDayOffset]) < this._adjustedDayWidth) {
               graphics.drawText(this._dowMedium[currDayOffset], textX, 1, 4, this._adjustedDayWidth);
            } else {
               graphics.drawText(this._dow[currDayOffset].charAt(0), textX, 0, 4, this._adjustedDayWidth);
            }

            if (domString[currDayOffset * 2] == '0') {
               graphics.drawText(domString, currDayOffset * 2 + 1, 1, textX, this._halfLabelHeight, 4, this._adjustedDayWidth);
            } else {
               graphics.drawText(domString, currDayOffset * 2, 2, textX, this._halfLabelHeight, 4, this._adjustedDayWidth);
            }

            graphics.setColor(prevColor);
            graphics.setColor(cellBorderColor);
            graphics.drawRect(textX, 0, this._adjustedDayWidth + 1, this._labelHeight);
            graphics.setColor(prevColor);
            currDayOffset = ++currDayOffset % 7;
         }

         graphics.popContext();
         if (clip.x < this._leftColumnWidth) {
            graphics.pushContext(0, 0, this._leftColumnWidth, this._labelHeight, 0, 0);
            if (this._dateAttr != null) {
               this._dateAttr.applyToGraphics(graphics);
               graphics.clear();
            }

            graphics.setFont(this._fontNormal);
            graphics.drawText(this._monthText, 0, this._monthText.length(), 0, 1, 4, this._leftColumnWidth - 1);
            graphics.drawText(this._yearText, 0, this._yearText.length(), 0, this._halfLabelHeight, 4, this._leftColumnWidth - 1);
            graphics.setColor(cellBorderColor);
            graphics.drawRect(0, 0, this._leftColumnWidth + 1, this._labelHeight);
            graphics.popContext();
         }
      }

      if (clip.x < this._leftColumnWidth) {
         int adjustedEvenMoreWeekHeight = this._adjustedWeekHeight + (this._halfHourInPixels << 1) + 1;
         graphics.pushRegion(0, this._labelHeight, this._leftColumnWidth, adjustedEvenMoreWeekHeight, 0, this._bufferedHeight);
         if (this._timeAttr != null) {
            this._timeAttr.applyToGraphics(graphics);
         }

         for (int i = 0; i < 24; i++) {
            if (i >= this._startHour && i <= this._endHour) {
               long yTemp = this._topOfScreenFromEndOfDay - (24 - i) * 3600000;
               int yLine = (int)(yTemp / this._millisecondsPerPixel);
               if (yLine + this._halfHourInPixels >= adjustedEvenMoreWeekHeight) {
                  break;
               }

               graphics.drawText(TimeStringCache.getString(i), -1, yLine, 37, this._leftColumnWidth - 1);
               graphics.drawLine(this._leftColumnWidth - 2, yLine, this._leftColumnWidth - 1, yLine);
            }
         }

         graphics.popContext();
      }

      int adjustedEvenMoreWeekHeight = this._adjustedWeekHeight + (this._halfHourInPixels << 1) + 1;
      graphics.pushRegion(this._leftColumnWidth, this._labelHeight, 7 * this._adjustedDayWidth, adjustedEvenMoreWeekHeight, 0, this._bufferedHeight);
      clip = graphics.getClippingRect();
      currDayOffset = this._firstDayDisplayed;
      if (this._viewAttr != null) {
         this._viewAttr.applyToGraphics(graphics);
      }

      WeekField$EventEntry[] events = null;
      if (!graphics.isDrawingStyleSet(8)) {
         int oldColour = graphics.getColor();

         for (int i = 0; i < 7; i++) {
            int column = i * this._adjustedDayWidth;
            if (Graphics.isColor()) {
               if (this._days[currDayOffset].getSOD() <= currentTime && currentTime < this._days[currDayOffset].getEOD()) {
                  attr = this._currentDayVectorAttr;
               } else if (currDayOffset != 0 && currDayOffset != 6) {
                  attr = this._weekdayAttr;
               } else {
                  attr = this._weekendAttr;
               }

               if (attr != null) {
                  graphics.setColor(attr.getColor(0));
                  graphics.fillRect(column, -(this._bufferedHeight - 1), this._adjustedDayWidth, this._adjustedWeekHeight);
               }
            }

            currDayOffset = ++currDayOffset % 7;
         }

         graphics.setColor(oldColour);
      }

      if (this._drawTimeLines && !graphics.isDrawingStyleSet(8)) {
         int oldColor = graphics.getColor();
         graphics.setColor(cellBorderColor);
         graphics.setStipple(Graphics.isColor() ? 286331153 : 1431655765);

         for (int i = 0; i < 24; i++) {
            if (i >= this._startHour && i <= this._endHour) {
               long yTemp = this._topOfScreenFromEndOfDay - (24 - i) * 3600000;
               int yLine = (int)(yTemp / this._millisecondsPerPixel);
               graphics.drawLine(1, yLine, 7 * this._adjustedDayWidth, yLine);
            }
         }

         graphics.setStipple(-1);
      }

      int twoHoursInPixelsPlusAlittleExtra = this._halfHourInPixels << 1 << 2;
      int dayColumnHeight = this._adjustedWeekHeight + twoHoursInPixelsPlusAlittleExtra;

      for (int i = 0; i < 7; i++) {
         WeekField$WeekFieldDayContents day = this._days[currDayOffset];
         int column = i * this._adjustedDayWidth;
         boolean bottomMoreIndicatorDrawn = false;
         boolean topMoreIndicatorDrawn = false;
         if (clip.intersects(column, -this._bufferedHeight, this._adjustedDayWidth, dayColumnHeight)) {
            if (day.isDayEmphasized()) {
               if (Graphics.isColor()) {
                  if (this._allDayAttr != null) {
                     graphics.setColor(this._allDayAttr.getColor(1));
                  }

                  int originalAlpha = graphics.getGlobalAlpha();
                  graphics.setGlobalAlpha(85);
                  graphics.rop(
                     -95, column + spaceToBar, -(this._bufferedHeight - 1), this._adjustedDayWidth - 2 * spaceToBar, this._adjustedWeekHeight, null, 0, 0
                  );
                  graphics.setGlobalAlpha(originalAlpha);
               } else {
                  graphics.drawBitmap(
                     column + spaceToBar, -(this._bufferedHeight - 1), this._adjustedDayWidth - 2 * spaceToBar, this._adjustedWeekHeight, EMPHASIS_BITMAP, 0, 0
                  );
               }
            }

            events = day.getEventEntries();
            int numEvents = day.getNumEventEntries();

            for (int j = 0; j < numEvents; j++) {
               if ((2 & events[j]._properties) == 0) {
                  long apptStart = events[j]._displayStart;
                  apptStart -= day.getEOD() - this._topOfScreenFromEndOfDay;
                  apptStart += day.getDstAdjustment(events[j]._displayStart);
                  long apptEnd = events[j]._displayEnd;
                  apptEnd -= day.getEOD() - this._topOfScreenFromEndOfDay;
                  apptEnd += day.getDstAdjustment(events[j]._displayEnd);
                  apptStart /= this._millisecondsPerPixel;
                  apptEnd /= this._millisecondsPerPixel;
                  if (apptEnd - 1 <= apptStart) {
                     apptEnd = apptStart + 2;
                  }

                  if (this._moreAttr != null) {
                     graphics.setColor(this._moreAttr.getColor(1));
                  }

                  if (!topMoreIndicatorDrawn && apptEnd <= -(this._bufferedHeight - 1)) {
                     graphics.drawLine(column + moreFirstStartX, this._topMoreFirstY, column + moreFirstEndX, this._topMoreFirstY);
                     graphics.drawLine(column + moreSecondStartX, this._topMoreSecondY, column + moreSecondEndX, this._topMoreSecondY);
                     topMoreIndicatorDrawn = true;
                  }

                  if (!bottomMoreIndicatorDrawn && apptStart >= this._adjustedWeekHeight - this._bufferedHeight) {
                     graphics.drawLine(column + moreFirstStartX, bottom_more_first_y, column + moreFirstEndX, bottom_more_first_y);
                     graphics.drawLine(column + moreSecondStartX, bottom_more_second_y, column + moreSecondEndX, bottom_more_second_y);
                     bottomMoreIndicatorDrawn = true;
                  }

                  int slot = events[j]._slot;
                  if (!day.isDayConflictFree()) {
                     slot = 1;
                  }

                  if (slot < 3) {
                     int apptX = column + slot * this._widthForDurationBar + spaceToBar;
                     int apptY = (int)apptStart;
                     int apptHeight = (int)(apptEnd - apptStart);
                     if (clip.intersects(apptX, apptY, this._widthOfDurationBar, apptHeight)) {
                        this.drawAppointment(graphics, apptX, apptY, this._widthOfDurationBar, apptHeight, events[j]._object, false);
                     }
                  }
               }
            }
         }

         if (++currDayOffset == 7) {
            currDayOffset = 0;
         }
      }

      graphics.popContext();
      clip = graphics.getClippingRect();
      currDayOffset = (7 + (7 + this._startingDOW - 1) % 7 - this._firstDayDisplayed) % 7;
      int xPos = this._leftColumnWidth + (1 + currDayOffset) * this._adjustedDayWidth;
      int prevColour = graphics.getColor();
      graphics.setColor(cellBorderColor);
      graphics.drawLine(xPos, this._labelHeight, xPos, this._labelHeight + this._adjustedWeekHeight);
      graphics.setColor(prevColour);
      graphics.setStipple(-1);
      graphics.setColor(cellBorderColor);
      int xTemp = this._leftColumnWidth + this._adjustedDayWidth;

      for (int i = 0; i < 7; i++) {
         graphics.drawLine(xTemp, this._labelHeight, xTemp, this._labelHeight + this._adjustedWeekHeight);
         xTemp += this._adjustedDayWidth;
      }

      graphics.drawLine(0, this._labelHeight, 0, this._labelHeight + this._adjustedWeekHeight);
      graphics.drawLine(this._leftColumnWidth, 0, this._leftColumnWidth, this._labelHeight + this._adjustedWeekHeight);
      graphics.drawLine(0, this._labelHeight, this._leftColumnWidth + 7 * this._adjustedDayWidth, this._labelHeight);
      graphics.drawLine(
         0, this._adjustedWeekHeight + this._labelHeight, this._leftColumnWidth + 7 * this._adjustedDayWidth, this._adjustedWeekHeight + this._labelHeight
      );
   }

   private final void drawAppointment(Graphics graphics, int x, int y, int width, int height, Object obj, boolean highlighted) {
      boolean eventIsMeeting = false;
      int eventColor = -1;
      if (obj instanceof Object) {
         eventColor = ((MultiServiceEvent)obj).getColour();
      }

      if (obj instanceof Object) {
         eventIsMeeting = ((MeetingInfoProvider)obj).isMeeting();
      }

      graphics.clear(x, y, width, height);
      if (!Graphics.isColor()) {
         graphics.drawRect(x, y, width, height);
      } else {
         if (!this._useMultiServiceColors) {
            eventColor = -1;
         }

         int oldColor = graphics.getColor();
         int originalAlpha = graphics.getGlobalAlpha();
         if (eventColor != -1) {
            graphics.setColor(16777215);
            graphics.fillRect(x, y, width, height);
            graphics.setColor(eventColor);
            graphics.setGlobalAlpha(80);
            graphics.fillRect(x, y, width, height);
            graphics.setGlobalAlpha(255);
            graphics.drawRect(x, y, width, height);
            if (eventIsMeeting) {
               for (int i = 0; i < width; i++) {
                  graphics.setStipple(rotateLeft(858993459, i));
                  graphics.drawLine(x + i, y, x + i, y + height - 1);
               }

               graphics.setStipple(-1);
            }
         } else {
            ThemeAttributeSet attr = null;
            attr = this._appointmentBorderAttr;
            if (attr != null) {
               if (!highlighted) {
                  graphics.setColor(attr.getColor(1));
               } else {
                  attr = this._theme.getAttributeSet(BASE_TAG);
                  if (attr != null) {
                     graphics.setColor(ThemeAttributeSet.getColor(this, 3));
                  }
               }
            }

            graphics.setGlobalAlpha(255);
            graphics.rop(-95, x, y, width, height, null, 0, 0);
            graphics.setGlobalAlpha(originalAlpha);
            if (eventIsMeeting) {
               attr = this._meetingAttr;
            } else {
               attr = this._simpleAppointmentAttr;
            }

            if (attr != null) {
               if (!highlighted) {
                  graphics.setColor(attr.getColor(1));
               } else {
                  attr = this._baseAttr;
                  if (attr != null) {
                     graphics.setColor(ThemeAttributeSet.getColor(this, 3));
                  }
               }
            }

            graphics.setGlobalAlpha(255);
            graphics.rop(-95, x + 1, y + 1, width - 2, height - 2, null, 0, 0);
         }

         graphics.setColor(oldColor);
         graphics.setGlobalAlpha(originalAlpha);
      }
   }

   private static final int rotateLeft(int value, int n) {
      return value << n | value >>> 32 - n;
   }

   @Override
   public final AccessibleContext getAccessibleSelectionAt(int index) {
      WeekField$WeekFieldDayContents temp = this._days[this._dayWithFocusIndex];
      WeekField$EventEntry focusEventEntry = temp.getEntryWithFocus();
      if (focusEventEntry != null) {
         EventImpl focusEventImpl = (EventImpl)focusEventEntry._object;
         return focusEventImpl;
      }

      long start = temp.getStartOfFocus();
      String date = "";
      if (start > 0) {
         DateField dateField = (DateField)(new Object("Date: ", start, 48));
         date = ((StringBuffer)(new Object())).append(date).append(dateField.toString()).toString();
      }

      return (AccessibleContext)(new Object(date, 28, 4, null));
   }

   @Override
   public final int getAccessibleChildCount() {
      return this._days[this._dayWithFocusIndex].getNumEventEntries();
   }

   @Override
   public final int getAccessibleRole() {
      return 28;
   }

   @Override
   public final boolean isAccessibleChildSelected(int index) {
      WeekField$EventEntry[] temp = this._days[this._dayWithFocusIndex].getEventEntries();
      return temp[index].equals(this._days[this._dayWithFocusIndex].getEntryWithFocus());
   }

   protected final void getPrimaryFocusRect(XYRect rect) {
      WeekField$WeekFieldDayContents dayWithFocus = this._days[this._dayWithFocusIndex];
      long startOfFocus = dayWithFocus.getStartOfFocus();
      long topFromScreenTop = startOfFocus - (dayWithFocus.getEOD() - this._topOfScreenFromEndOfDay) + dayWithFocus.getDstAdjustment(startOfFocus);
      long bottomFromFocusTop = dayWithFocus.getEndOfFocus() - startOfFocus;
      int numColumnsToDayWithFocus = (7 + this._dayWithFocusIndex - this._firstDayDisplayed) % 7;
      long diff = 0;
      if (topFromScreenTop < -1800000) {
         diff = (topFromScreenTop + 1800000) / this._millisecondsPerPixel * -1;
         topFromScreenTop = -1800000;
      }

      rect.set(
         this._leftColumnWidth + numColumnsToDayWithFocus * this._adjustedDayWidth,
         this._heightToStart + (int)(topFromScreenTop / this._millisecondsPerPixel),
         this._adjustedDayWidth,
         (int)(bottomFromFocusTop / this._millisecondsPerPixel - diff)
      );
   }

   @Override
   public final void getFocusRect(XYRect rect) {
      this.getPrimaryFocusRect(rect);
      if (!this._dayHeaderInvalidFocusRect.isEmpty()) {
         rect.union(this._dayHeaderInvalidFocusRect);
      }

      WeekField$WeekFieldDayContents dayWithFocus = this._days[this._dayWithFocusIndex];
      WeekField$EventEntry eventWithFocus = dayWithFocus.getEntryWithFocus();
      if (eventWithFocus != null && (2 & eventWithFocus._properties) == 0) {
         XYRect eventRect = Ui.getTmpXYRect();
         this.getEventRect(dayWithFocus, eventWithFocus, eventRect);
         if (!eventRect.isEmpty()) {
            eventRect.x = rect.x;
            eventRect.width = rect.width;
            if (eventRect.y < -this._bufferedHeight) {
               eventRect.height = eventRect.height - (-eventRect.y - this._bufferedHeight);
               eventRect.y = -this._bufferedHeight;
            }

            eventRect.y = eventRect.y + this._heightToStart;
            int currentColumn = (7 + this._dayWithFocusIndex - this._firstDayDisplayed) % 7;

            for (int nextDayIndex = (this._dayWithFocusIndex + 1) % 7;
               eventWithFocus._end > this._days[nextDayIndex].getSOD();
               nextDayIndex = (nextDayIndex + 1) % 7
            ) {
               int nextColumn = (7 + nextDayIndex - this._firstDayDisplayed) % 7;
               if (nextColumn <= currentColumn) {
                  break;
               }

               eventRect.y = this._labelHeight;
               eventRect.height = this._adjustedWeekHeight;
               eventRect.width = eventRect.width + this._adjustedDayWidth;
            }

            for (int var9 = (this._dayWithFocusIndex + 6) % 7; eventWithFocus._start < this._days[var9].getEOD(); var9 = (var9 + 6) % 7) {
               int nextColumn = (7 + var9 - this._firstDayDisplayed) % 7;
               if (nextColumn >= currentColumn) {
                  break;
               }

               eventRect.y = this._labelHeight;
               eventRect.height = this._adjustedWeekHeight;
               eventRect.x = eventRect.x - this._adjustedDayWidth;
               eventRect.width = eventRect.width + this._adjustedDayWidth;
            }

            rect.union(eventRect);
         }

         Ui.returnTmpXYRect(eventRect);
      }
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      XYRect focusRect = this._focusRect;
      this.getPrimaryFocusRect(focusRect);
      if (!this._drawTimeLines) {
         int yAdjustment = 0;
         if (focusRect.y == focusRect.y >> 1 << 1) {
            yAdjustment = 1;
         }

         graphics.invert(31, focusRect.y + yAdjustment, 1, focusRect.height - yAdjustment);
      }

      if (!Graphics.isColor()) {
         graphics.invert(focusRect);
         graphics.invert(focusRect.x, 0, focusRect.width, this._labelHeight);
      } else {
         if (!this._dayHeaderInvalidFocusRect.isEmpty()) {
            graphics.pushContext(this._dayHeaderInvalidFocusRect, 0, 0);
            graphics.clear();
            graphics.setDrawingStyle(8, on);
            this.paint(graphics);
            graphics.setDrawingStyle(8, false);
            graphics.popContext();
            this._dayHeaderInvalidFocusRect.set(0, 0, 0, 0);
         }

         ThemeAttributeSet attr = this._focusHilightAttr;
         if (on && attr != null) {
            graphics.setColor(attr.getColor(1));
            graphics.setBackgroundColor(attr.getColor(0));
         }

         int fg = graphics.getColor();
         graphics.setColor(graphics.getBackgroundColor());
         graphics.fillRect(focusRect.x, focusRect.y, focusRect.width, focusRect.height);
         graphics.setColor(fg);
         graphics.pushContext(focusRect.x, focusRect.y, focusRect.width, focusRect.height, 0, 0);
         graphics.setDrawingStyle(8, on);
         this.paint(graphics);
         graphics.setDrawingStyle(8, false);
         this.drawFocusInvertAppt(graphics, on);
         graphics.popContext();
      }

      this.drawFocusInvertAppt(graphics, on);
   }

   private final void drawFocusInvertAppt(Graphics graphics, boolean on) {
      XYRect focusRect = this._focusRect;
      WeekField$WeekFieldDayContents dayWithFocus = this._days[this._dayWithFocusIndex];
      WeekField$EventEntry eventWithFocus = dayWithFocus.getEntryWithFocus();
      if (eventWithFocus != null && (2 & eventWithFocus._properties) == 0) {
         this.invertEvent(graphics, focusRect.x, dayWithFocus, eventWithFocus, on);
         int currentColumn = (7 + this._dayWithFocusIndex - this._firstDayDisplayed) % 7;
         Object eventObject = eventWithFocus._object;

         for (int nextDayIndex = (this._dayWithFocusIndex + 1) % 7;
            eventWithFocus._end > this._days[nextDayIndex].getSOD();
            nextDayIndex = (nextDayIndex + 1) % 7
         ) {
            int nextColumn = (7 + nextDayIndex - this._firstDayDisplayed) % 7;
            if (nextColumn <= currentColumn) {
               break;
            }

            this.invertMatchingEvent(graphics, eventObject, nextColumn, this._days[nextDayIndex], on);
         }

         for (int var11 = (this._dayWithFocusIndex + 6) % 7; eventWithFocus._start < this._days[var11].getEOD(); var11 = (var11 + 6) % 7) {
            int nextColumn = (7 + var11 - this._firstDayDisplayed) % 7;
            if (nextColumn >= currentColumn) {
               return;
            }

            this.invertMatchingEvent(graphics, eventObject, nextColumn, this._days[var11], on);
         }
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27) {
         WeekField$WeekFieldDayContents dayWithFocus = this._days[this._dayWithFocusIndex];
         long sofa = dayWithFocus.getStartOfFocusAnchor();
         if (dayWithFocus.getStartOfFocus() != sofa || dayWithFocus.getEndOfFocus() != dayWithFocus.getEndOfFocusAnchor()) {
            this.focusRemove();
            dayWithFocus.setFocus(sofa);
            this.focusAdd(true);
            return true;
         }
      }

      return super.keyChar(key, status, time);
   }

   private final void invertMatchingEvent(
      Graphics graphics, Object eventObjectToMatch, int dayColumn, WeekField$WeekFieldDayContents dayContents, boolean focusOn
   ) {
      int xOffset = this._leftColumnWidth + dayColumn * this._adjustedDayWidth;
      WeekField$EventEntry[] events = dayContents.getEventEntries();
      int numEvents = dayContents.getNumEventEntries();

      for (int i = 0; i < numEvents; i++) {
         if (events[i]._object == eventObjectToMatch) {
            this.invertEvent(graphics, xOffset, dayContents, events[i], focusOn);
            return;
         }
      }
   }

   private final void getEventRect(WeekField$WeekFieldDayContents day, WeekField$EventEntry event, XYRect rect) {
      long endOfDay = day.getEOD();
      long apptStart = event._displayStart;
      long apptEnd = event._displayEnd;
      int spaceToBar = this._widthForDurationBar - this._widthOfDurationBar >> 1;
      apptStart -= endOfDay - this._topOfScreenFromEndOfDay - day.getDstAdjustment(event._displayStart);
      apptEnd -= endOfDay - this._topOfScreenFromEndOfDay - day.getDstAdjustment(event._displayEnd);
      apptStart /= this._millisecondsPerPixel;
      apptEnd /= this._millisecondsPerPixel;
      if (apptEnd <= apptStart) {
         apptEnd = apptStart + 1;
      }

      int slot = event._slot;
      if (!day.isDayConflictFree()) {
         slot = 1;
      }

      if (slot < 3) {
         int x = slot * this._widthForDurationBar + spaceToBar;
         int y = (int)apptStart;
         int w = this._widthOfDurationBar;
         int h = (int)(apptEnd - apptStart);
         rect.set(x, y, w, h);
      } else {
         rect.set(0, 0, 0, 0);
      }
   }

   private final void invertEvent(Graphics graphics, int xOffset, WeekField$WeekFieldDayContents day, WeekField$EventEntry event, boolean focusOn) {
      XYRect eventRect = Ui.getTmpXYRect();
      this.getEventRect(day, event, eventRect);
      if (!eventRect.isEmpty()) {
         int adjustedEvenMoreWeekHeight = this._adjustedWeekHeight + (this._halfHourInPixels << 1) + 1;
         graphics.pushRegion(xOffset, this._labelHeight, 7 * this._adjustedDayWidth, adjustedEvenMoreWeekHeight, 0, this._bufferedHeight + 1);
         eventRect.x++;
         eventRect.width -= 2;
         eventRect.height -= 2;
         if (this._useMultiServiceColors) {
            graphics.setGlobalAlpha(192);
            graphics.setColor(16777215);
            graphics.drawRect(eventRect.x, eventRect.y, eventRect.width, eventRect.height);
            graphics.drawRect(eventRect.x + 1, eventRect.y + 1, eventRect.width - 2, eventRect.height - 2);
         } else {
            graphics.invert(eventRect);
         }

         graphics.popContext();
      }

      Ui.returnTmpXYRect(eventRect);
   }

   @Override
   protected final void layout(int width, int height) {
      this.applyTheme();
      this._lowResolution = height < 100;
      this._minimumHourInPixels = this.getFont().getHeight();
      int maxWidth = 0;
      int timeWidth = 0;

      for (int i = 0; i < 24; i++) {
         if (i >= this._startHour && i <= this._endHour) {
            this._tempBuffer.setLength(0);
            this._tempBuffer.append(TimeStringCache.getString(i));
            timeWidth = this.getFont().getBounds(this._tempBuffer);
            if (timeWidth > maxWidth) {
               maxWidth = timeWidth;
            }
         }
      }

      this._leftColumnWidth = maxWidth + 1 + 1;
      if (this._leftColumnWidth < 33) {
         this._leftColumnWidth = 33;
      }

      int availableDayWidth = width - (this._leftColumnWidth + 1);
      this._adjustedDayWidth = availableDayWidth / 7;
      this._leftColumnWidth = width - 7 * this._adjustedDayWidth - 1;
      this._adjustedTotalWeekWidth = this._adjustedDayWidth * 7;
      this._widthOfDurationBar = this._adjustedDayWidth >> 2;
      this._widthForDurationBar = this._adjustedDayWidth / 3;
      int adjustedDescriptionHeight = this._minimumHourInPixels;
      if (this._lowResolution) {
         adjustedDescriptionHeight *= 3;
         adjustedDescriptionHeight++;
      } else {
         adjustedDescriptionHeight *= 5;
         adjustedDescriptionHeight += 3;
      }

      this._defaultWeekHeight = height - adjustedDescriptionHeight;
      this.scaleWeekWindow();
   }

   private final boolean scaleWeekWindow() {
      if (this._minimumHourInPixels == 0) {
         return false;
      }

      int maximumVisibleRows = (this._defaultWeekHeight - (this._minimumHourInPixels >> 1)) / this._minimumHourInPixels;
      int desiredVisibleRows = this._endHour - this._startHour + 1;
      int actualVisibleRows = Math.min(desiredVisibleRows, maximumVisibleRows);
      int adjustedHourInPixels = this._defaultWeekHeight * 10 / (actualVisibleRows * 10 + 5);
      this._millisecondsPerPixel = 3600000 / adjustedHourInPixels;
      this._halfHourInPixels = adjustedHourInPixels >> 1;
      this._bufferedHeight = this._halfHourInPixels + 1;
      this._halfLabelHeight = this._minimumHourInPixels;
      this._labelHeight = this._minimumHourInPixels * 2;
      this._heightToStart = this._bufferedHeight + this._labelHeight;
      this._topMoreFirstY = -this._bufferedHeight + 1;
      this._topMoreSecondY = this._topMoreFirstY + 1;
      int clippedHourAtBottom = (this._defaultWeekHeight - this._halfHourInPixels) % adjustedHourInPixels;
      int tmp;
      if (clippedHourAtBottom > this._halfHourInPixels + (this._halfHourInPixels >> 1)) {
         tmp = this._defaultWeekHeight + (adjustedHourInPixels - clippedHourAtBottom);
      } else {
         tmp = this._defaultWeekHeight - clippedHourAtBottom;
      }

      if (tmp != this._adjustedWeekHeight) {
         this._adjustedWeekHeight = tmp;
         this.setExtent(this._leftColumnWidth + 7 * this._adjustedDayWidth + 1, this._labelHeight + this._adjustedWeekHeight + 1);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final int getPreferredWidth() {
      return this._leftColumnWidth + 7 * this._adjustedDayWidth;
   }

   @Override
   public final int moveFocus(int amount, int status, int time) {
      boolean selecting = (status & 2) != 0;
      if ((status & 65537) != 0) {
         if (selecting) {
            return 0;
         }

         this.moveDayFocus(amount, true);
      } else {
         int amountExtra = this._days[this._dayWithFocusIndex].moveFocus(amount, selecting, false);
         if (amountExtra != 0) {
            this.moveDayFocus(amountExtra, false);
         }

         if (this.adjustTopOfScreenOffset()) {
            this.invalidate(0, this._labelHeight, this._adjustedTotalWeekWidth + this._leftColumnWidth, this._adjustedWeekHeight);
         }
      }

      Object selectedEvent = this.getSelectedObject();
      if (selectedEvent != this._selectedEvent) {
         this._selectedEvent = selectedEvent;
         this._callback.selectedEventChanged(this._selectedEvent);
      }

      if (Ui.isTTSEnabled()) {
         super.accessibleEventOccurred(6, new Object(1), new Object(2), this);
      }

      return 0;
   }

   @Override
   public final void moveFocus(int x, int y, int status, int time) {
      if (x >= 0 && y >= 0) {
         boolean selecting = (status & 2) != 0;
         if (y < this._adjustedWeekHeight + this._labelHeight && x < this._adjustedTotalWeekWidth + this._leftColumnWidth) {
            x -= this._leftColumnWidth;
            if (x >= 0) {
               int selectedDayIndex = x / this._adjustedDayWidth;
               int amountMoved = selectedDayIndex - this._dayWithFocusIndex;
               if (amountMoved != 0) {
                  this.moveDayFocus(amountMoved, true);
               }
            }

            if (y >= this._labelHeight) {
               int currentTimeslotIndex = (this._focusRect.y - this._labelHeight) / this._halfHourInPixels;
               int amount = (y - this._labelHeight) / this._halfHourInPixels - currentTimeslotIndex;
               this._days[this._dayWithFocusIndex].moveFocus(amount, selecting, true);
            }

            if (this.adjustTopOfScreenOffset()) {
               this.invalidate(0, this._labelHeight, this._adjustedTotalWeekWidth + this._leftColumnWidth, this._adjustedWeekHeight);
            }

            Object selectedEvent = this.getSelectedObject();
            if (selectedEvent != this._selectedEvent) {
               this._selectedEvent = selectedEvent;
               this._callback.selectedEventChanged(this._selectedEvent);
            }
         }

         if (Ui.isTTSEnabled()) {
            super.accessibleEventOccurred(6, new Object(1), new Object(2), this);
         }
      }
   }

   @Override
   public final boolean stylusTap(int x, int y, int status, int time) {
      XYRect extent = this.getExtent();
      return extent.contains(extent.x, extent.y, x, y);
   }

   @Override
   public final boolean stylusDoubleTap(int x, int y, int status, int time) {
      return true;
   }

   private final boolean adjustTopOfScreenOffset() {
      boolean needRepaint = false;
      long viewStartTime = this._days[this._dayWithFocusIndex].getEOD() - this._topOfScreenFromEndOfDay;
      long viewEndTime = viewStartTime + (this._adjustedWeekHeight - (this._bufferedHeight + 4)) * this._millisecondsPerPixel;
      long startOfFocus = this._days[this._dayWithFocusIndex].getStartOfFocus();
      startOfFocus += this._days[this._dayWithFocusIndex].getDstAdjustment(startOfFocus);
      long endOfFocus = this._days[this._dayWithFocusIndex].getEndOfFocus();
      endOfFocus += this._days[this._dayWithFocusIndex].getDstAdjustment(endOfFocus);
      long focusAnchor = this._days[this._dayWithFocusIndex].getStartOfFocusAnchor();
      focusAnchor += this._days[this._dayWithFocusIndex].getDstAdjustment(focusAnchor);
      long endFocusAnchor = this._days[this._dayWithFocusIndex].getEndOfFocusAnchor();
      endFocusAnchor += this._days[this._dayWithFocusIndex].getDstAdjustment(endFocusAnchor);
      if (endFocusAnchor >= viewEndTime) {
         needRepaint = true;
         viewStartTime = endOfFocus - (this._adjustedWeekHeight - (this._bufferedHeight + 4)) * this._millisecondsPerPixel;
         if (this._scrollByHour) {
            viewStartTime = (viewStartTime + 3600000 - 1) / 3600000;
            viewStartTime *= 3600000;
         }
      }

      if (focusAnchor < viewStartTime - 1800000) {
         needRepaint = true;
         viewStartTime = startOfFocus + 1800000;
         if (this._scrollByHour) {
            viewStartTime /= 3600000;
            viewStartTime *= 3600000;
         }
      }

      this._topOfScreenFromEndOfDay = this._days[this._dayWithFocusIndex].getEOD() - viewStartTime;
      return needRepaint;
   }

   private final void moveDayFocus(int amount, boolean preserveTimeOfDay) {
      Calendar cal = this._cal;
      CalendarExtensions calEx = (CalendarExtensions)cal;
      if (!preserveTimeOfDay) {
         calEx.setTimeLong(this._days[this._dayWithFocusIndex].getSOD());
         if (amount > 0) {
            amount = 1;
            DateTimeUtilities.calendarAddWithDst(cal, calEx, 5, 1);
         } else {
            amount = -1;
            calEx.add(14, -1);
         }
      } else {
         calEx.setTimeLong(this._days[this._dayWithFocusIndex].getStartOfFocusAnchor());
         DateTimeUtilities.calendarAddWithDst(cal, calEx, 5, amount);
      }

      int originalColumn = (7 + this._dayWithFocusIndex - this._firstDayDisplayed) % 7;
      this._timeToView = calEx.getTimeLong();
      this._dayWithFocusIndex = cal.get(7) - 1;
      this._callback.selectedDateChanged(this._timeToView);
      int invalidCount = 0;
      if (originalColumn + amount >= 7) {
         invalidCount = originalColumn + amount - 6;
         this._firstDayDisplayed += amount;
         this._firstDayDisplayed %= 7;
      } else if (originalColumn + amount < 0) {
         invalidCount = (originalColumn + amount) * -1;
         int flippedAmount = amount * -1;
         flippedAmount %= 7;
         this._firstDayDisplayed += 7 - flippedAmount;
         this._firstDayDisplayed %= 7;
      }

      if (invalidCount > 0) {
         int currentDayIndex = this._dayWithFocusIndex;

         for (int i = 0; i < invalidCount; i++) {
            this._days[currentDayIndex].setDayPopulated(false);
            if (amount < 0) {
               currentDayIndex++;
            } else {
               currentDayIndex += 6;
            }

            currentDayIndex %= 7;
         }

         this.calculateDayStarts();
      }

      this.calculateDayInfo();
      int x1;
      int widthToRepaint;
      if (amount > 0) {
         x1 = this._leftColumnWidth + originalColumn * this._adjustedDayWidth;
         widthToRepaint = (originalColumn == 6 ? 1 : 2) * this._adjustedDayWidth;
      } else {
         widthToRepaint = (originalColumn == 0 ? 1 : 2) * this._adjustedDayWidth;
         x1 = this._leftColumnWidth + (originalColumn + 1) * this._adjustedDayWidth - widthToRepaint;
      }

      this._dayHeaderInvalidFocusRect.set(x1, 0, widthToRepaint, this._labelHeight);
   }

   public final boolean hasLowResolution() {
      return this._lowResolution;
   }
}
