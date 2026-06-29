package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.AccessibleEventDispatcher;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontMetrics;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYPoint;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleText;
import net.rim.device.api.ui.accessibility.AccessibleValue;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.controller.Duration;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.framework.model.DescriptionProvider;
import net.rim.device.apps.api.framework.model.UniqueIDProvider;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.ui.Edit$Helper;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.RichText;
import net.rim.vm.Array;

class DayList implements CalendarViewListField$CalendarViewListFieldCallback {
   protected CalendarViewListField _field;
   private StringBuffer _buf = (StringBuffer)(new Object());
   private XYRect _focus = (XYRect)(new Object());
   private int _summaryLines = -1;
   private boolean _displayLines = true;
   private boolean _displayEndTimes = true;
   private boolean _displayTimeBars = true;
   private boolean _timeBarsDisabledDueToSpaceLimitations = false;
   private boolean _displayInformationIcons = true;
   private boolean _clampTimes = true;
   private int _widthPerBar;
   private int _minDayLineHeight;
   private int _maxTimeWidth;
   private int _widthToDesc;
   private int _widthOfDesc;
   private int[] _sharedOffsets = new int[]{0, 0, -805044214, 1631782912, 1684956524, 29281, -805044213, 775162112};
   private final byte[] _sharedBytes = new byte[]{0};
   private Font[] _sharedFonts = new Object[1];
   private int _firstNonEmptyTransition = -1;
   private int _firstAllDayTransition = -1;
   private DayList$TransitionSortComparator _tsc = new DayList$TransitionSortComparator(this);
   protected Calendar _cal = Calendar.getInstance();
   protected CalendarExtensions _calEx = (CalendarExtensions)this._cal;
   protected TimeZone _tz = this._cal.getTimeZone();
   private DateFormat _dateFormat = DateFormat.getInstance(40);
   DayList$TransitionList _list = new DayList$TransitionList(this);
   protected int _numTransitions;
   private long _startOfList;
   private long _endOfList;
   protected boolean _splitDaysThatSpanMidnight;
   private int _markedOffset = -1;
   private int _maxSimultaneousBars;
   private long _preferredTimeSelection;
   protected IconCollection[] _icons = new Object[0];
   protected int[][] _indices = new int[0][0];
   private ThemeAttributeSet _themeAttributesHeader;
   private ThemeAttributeSet _themeAttributesAppointments;
   private ThemeAttributeSet _themeAttributesFreeTimeSeperators;
   private ThemeAttributeSet _themeAttributesConflictingAppointments;
   private ThemeAttributeSet _themeAttributesPastAppointments;
   private ThemeAttributeSet _themeAttributesPastHeader;
   private ThemeAttributeSet _themeAttributesPastFreeTimeSeperators;
   private int _themeGeneration;
   private int ALL_DAY_COLOUR = 4620980;
   private int LINE_COLOUR = 7833753;
   private int TIME_BAR_COLOUR = 32896;
   private int MARK_COLOUR = 14423100;
   private int HILIGHT_BACKGROUND_COLOUR = 6970061;
   private int HILIGHT_FOREGROUND_COLOUR = 16777215;
   private FontMetrics _fontMetrics = (FontMetrics)(new Object());
   private ResourceBundle _rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
   private int _borderMode;
   private XYEdges _padding;
   private int _accessibleStateSet;
   private static final int MAX_TIME_BARS = 24;
   private static final int TRANSITION_ARRAY_GROW_SIZE = 10;
   private static final char NO_EVENT_BAR_CHAR = '\uf460';
   private static final char BEGIN_EVENT_BAR_CHAR = '┌';
   private static final char MIDDLE_EVENT_BAR_CHAR = '│';
   private static final char END_EVENT_BAR_CHAR = '└';
   protected static final Bitmap CLOCK_BITMAP = Bitmap.getBitmapResource("Clock.gif");
   static final int TIME_COL_PAD = 1;
   static final int TIME_TO_DESC_PADDING_NO_ICON = 3;
   static final int TIME_TO_DESC_PADDING_ICON = 9;
   static final int FLAG_WIDTH = 5;
   private static final int DASHED_LINE_STIPPLE = 1431655765;
   private static final int SOLID_LINE_STIPPLE = -1;
   private static Tag TAG_HEADER = Tag.create("agenda-header");
   private static Tag TAG_APPOINTMENTS = Tag.create("appointments");
   private static Tag TAG_FREE_TIME_SEPERATORS = Tag.create("free-time-seperators");
   private static Tag TAG_CONFLICTING_APPOINTMENTS = Tag.create("conflicting-appointments");
   private static Tag TAG_PAST_HEADER = Tag.create("past-agenda-header");
   private static Tag TAG_PAST_FREE_TIME_SEPERATORS = Tag.create("past-free-time-seperators");
   private static Tag TAG_PAST_APPOINTMENTS = Tag.create("past-appointments");
   private static final Tag ALLDAY_TAG = Tag.create("day-allday");
   private static final Tag LINE_TAG = Tag.create("day-line");
   private static final Tag APPOINTEMENT_BAR_TAG = Tag.create("day-appointment-bar");
   private static final Tag CURRENT_TIME_TAG = Tag.create("day-current-time");
   private static final Tag FOCUS_HILIGHT_TAG = Tag.create("day-focus-hilight");
   private static final boolean CAPITALIZE_HEADERS = false;
   private static final int VECTOR_PADDING = 1;
   private static final XYEdges THICK_PADDING = (XYEdges)(new Object(2, 3, 2, 3));
   private static final XYEdges LEFT_PADDING = (XYEdges)(new Object(0, 0, 0, 6));
   private static final XYEdges RIGHT_PADDING = (XYEdges)(new Object(0, 6, 0, 1));
   private static final XYEdges NO_PADDING = (XYEdges)(new Object(0, 0, 0, 1));
   private static final int ROUND_BORDER_PADDING = 1;
   private static final int ROUND_BORDER_DIAMETER = 8;
   private static final int MODE_NONE = 0;
   private static final int MODE_RIGHT_SWATCH = 1;
   private static final int MODE_RIGHT_SWATCH_FILLED = 2;
   private static final int MODE_LEFT_SWATCH = 3;
   private static final int MODE_LEFT_SWATCH_FILLED = 4;
   private static final int MODE_ROUND_BORDER = 5;
   private static final int MODE_FILLED_ROUND_BORDER = 6;
   private static final int BORDER_FILL_ALPHA = 15;
   private static final int SPACE_PER_BAR = 6;
   private static final int TIME_BAR_WIDTH = 3;

   protected CalendarViewListField createCalendarViewListField(CalendarViewListField$CalendarViewListFieldCallback callback, boolean multiSelectAllowed) {
      return new CalendarViewListField(callback, multiSelectAllowed);
   }

   protected void applyTheme() {
      Theme theme = ThemeManager.getActiveTheme();
      this._themeAttributesHeader = theme.getAttributeSet(TAG_HEADER);
      this._themeAttributesAppointments = theme.getAttributeSet(TAG_APPOINTMENTS);
      this._themeAttributesFreeTimeSeperators = theme.getAttributeSet(TAG_FREE_TIME_SEPERATORS);
      this._themeAttributesPastHeader = theme.getAttributeSet(TAG_PAST_HEADER);
      this._themeAttributesPastAppointments = theme.getAttributeSet(TAG_PAST_APPOINTMENTS);
      this._themeAttributesPastFreeTimeSeperators = theme.getAttributeSet(TAG_PAST_FREE_TIME_SEPERATORS);
      this._themeAttributesConflictingAppointments = theme.getAttributeSet(TAG_CONFLICTING_APPOINTMENTS);
      this._field.setThemeAttributesSpecial(theme.getAttributeSet(ALLDAY_TAG), null);
      this.ALL_DAY_COLOUR = ThemeAttributeSet.getColor(this._field, 1);
      this._field.setThemeAttributesSpecial(theme.getAttributeSet(LINE_TAG), null);
      this.LINE_COLOUR = ThemeAttributeSet.getColor(this._field, 1);
      this._field.setThemeAttributesSpecial(theme.getAttributeSet(APPOINTEMENT_BAR_TAG), null);
      this.TIME_BAR_COLOUR = ThemeAttributeSet.getColor(this._field, 1);
      this._field.setThemeAttributesSpecial(theme.getAttributeSet(CURRENT_TIME_TAG), null);
      this.MARK_COLOUR = ThemeAttributeSet.getColor(this._field, 1);
      this._field.setThemeAttributesSpecial(theme.getAttributeSet(FOCUS_HILIGHT_TAG), null);
      this.HILIGHT_BACKGROUND_COLOUR = ThemeAttributeSet.getColor(this._field, 0);
      this.HILIGHT_FOREGROUND_COLOUR = ThemeAttributeSet.getColor(this._field, 1);
      this._field.setThemeAttributesSpecial(null, null);
   }

   protected boolean supportAdvancedThemeing() {
      throw null;
   }

   CalendarViewListField getField() {
      return this._field;
   }

   protected String getTimeText(DayList$Transition entryToDisplay) {
      String returnString = null;
      if (entryToDisplay._displayTime != null) {
         returnString = entryToDisplay._displayTime;
      }

      if (returnString == null) {
         Calendar startDate = Calendar.getInstance();
         startDate.setTime((Date)(new Object(entryToDisplay._timeInMillis)));
         returnString = TimeStringCache.getString(startDate);
      }

      if (CalendarOptions.getOptions().showEndTime()) {
         Calendar endDate = Calendar.getInstance();
         endDate.setTime((Date)(new Object(entryToDisplay._endTimeInMillis)));
         String endTimeString = ((StringBuffer)(new Object(" - "))).append(TimeStringCache.getString(endDate)).toString();
         returnString = ((StringBuffer)(new Object())).append(returnString).append(endTimeString).toString();
      }

      return returnString;
   }

   protected int calculateMaxTimeWidth(Font font) {
      String timeStr = null;
      timeStr = TimeStringCache.getString(0);
      return font.getBounds(timeStr);
   }

   protected void formatSeparatorDate(Calendar cal, StringBuffer sb) {
      this._dateFormat.format(cal, sb, null);
   }

   protected void setEndOfList(long endOfList) {
      this._endOfList = endOfList;
   }

   protected void setStartOfList(long startOfList) {
      this._startOfList = startOfList;
   }

   long getEndOfList() {
      return this._endOfList;
   }

   long getStartOfList() {
      return this._startOfList;
   }

   protected void getPopupLocation(XYPoint location) {
      UiApplication.getUiApplication().getActiveScreen().getFocusRect(this._focus);
      location.x = this._focus.x + this._maxSimultaneousBars * this._widthPerBar + this._maxTimeWidth + 3;
      location.y = this._focus.y;
   }

   protected int getIconWidth(IconCollection[] icons, int[][] indices) {
      int totalIconWidth = 0;
      int iconWidth = 0;

      for (int i = 0; i < icons.length; i++) {
         iconWidth = icons[i].getWidth(Integer.MAX_VALUE, this._minDayLineHeight) + 1;
         totalIconWidth += iconWidth * indices[i].length;
      }

      return totalIconWidth;
   }

   protected void getIconsForTransition(DayList$Transition curTransition) {
      if (curTransition._calElement instanceof Object) {
         DescriptionProvider dp = (DescriptionProvider)curTransition._calElement;
         dp.getIconsForField(7380487202915104824L, this._icons, this._indices);
      }
   }

   protected void addDateTimeTransitions() {
      throw null;
   }

   protected void reCrypt() {
      for (int i = 0; i < this._numTransitions; i++) {
         this._list.getAt(i).reCrypt(true, true);
      }
   }

   protected void getTransitionsFromEvents(Vector events, long loadTime) {
      TimeZone tz = this._tz;
      int numberOfEvents = events.size();
      int sequenceNumber = 0;
      boolean addEndTransitions = this._displayEndTimes;
      boolean clamping = this._clampTimes;
      long sol = this._startOfList;
      long eol = this._endOfList;
      boolean sortRequired = false;
      boolean freeTimeAdded = false;
      long endOfLastFreeBlock = 0;
      this._list.reset(events);
      this.verifyListBoundaries(loadTime);

      for (int i = 0; i < numberOfEvents; i++) {
         Duration calEvent = (Duration)events.elementAt(i);
         long start = calEvent.getStart(tz);
         long cachedDuration;
         long duration = cachedDuration = calEvent.getDuration(tz);
         long end = start + duration;
         if (endOfLastFreeBlock <= end) {
            endOfLastFreeBlock = this.addFreeTimeTransition(this._list, i, sequenceNumber, loadTime);
            if (endOfLastFreeBlock > 0) {
               freeTimeAdded = true;
            }
         }

         if (end < start) {
            end = Long.MAX_VALUE;
         }

         boolean done = false;
         boolean eventSpans = false;
         if (end >= this._startOfList && start <= this._endOfList) {
            if (end > this._endOfList) {
               duration -= end - this._endOfList;
               end = this._endOfList;
            }

            if (start < this._startOfList) {
               duration -= this._startOfList - start;
               start = this._startOfList;
               eventSpans = true;
            }

            while (true) {
               byte curTransitionType = 17;
               long curTransitionStart = start;
               String curTransitionDescription = null;
               Duration curTransitionElement = null;
               if (calEvent instanceof Object) {
                  DescriptionProvider dp = (DescriptionProvider)calEvent;
                  curTransitionDescription = dp.getStringForField(-4581712257088750184L);
               }

               curTransitionElement = calEvent;
               if (clamping) {
                  if (start < sol) {
                     curTransitionStart = sol;
                  } else if (start >= eol) {
                     curTransitionStart = eol - 1;
                  } else {
                     curTransitionType = 16;
                  }
               }

               boolean allDay = calEvent.isAllDay();
               if (start == end) {
                  curTransitionType = 32;
               }

               if (allDay) {
                  curTransitionType = 2;
               }

               this._list
                  .addTransition(curTransitionType, curTransitionStart, start + cachedDuration, curTransitionElement, curTransitionDescription, sequenceNumber);
               if (eventSpans) {
                  this._list.getAt(this._list.getCurrentOffset() - 1)._subsequentDayOfSpanningEvent = true;
               }

               if (addEndTransitions && start != end && !allDay) {
                  curTransitionType = 18;
                  curTransitionStart = start + duration;
                  curTransitionElement = calEvent;
                  curTransitionDescription = null;
                  if (clamping) {
                     if (end >= eol) {
                        curTransitionStart = eol - 1;
                     } else if (end < sol) {
                        curTransitionStart = sol;
                     } else {
                        curTransitionType = 19;
                     }
                  }

                  this._list.addTransition(curTransitionType, curTransitionStart, 0, curTransitionElement, curTransitionDescription, sequenceNumber);
               }

               sequenceNumber++;
               if (start != end && !DateTimeUtilities.isSameDate(start, end - 1, tz, null, this._cal)) {
                  this._calEx.setTimeLong(start);
                  int nextDay = ((Calendar)this._calEx).get(5) + 1;
                  ((Calendar)this._calEx).set(11, 0);
                  ((Calendar)this._calEx).set(12, 0);
                  ((Calendar)this._calEx).set(13, 0);
                  ((Calendar)this._calEx).set(5, nextDay);
                  DateTimeUtilities.verifyStartOfDay((Calendar)this._calEx, this._calEx);
                  start = this._calEx.getTimeLong();
                  eventSpans = true;
                  sortRequired = true;
                  if (this._list.getCurrentOffset() > 2 * numberOfEvents && start > eol) {
                     done = true;
                  }
               } else {
                  done = true;
               }

               if (!this._splitDaysThatSpanMidnight || done) {
                  break;
               }
            }
         }
      }

      if (!freeTimeAdded) {
         this.addFreeTimeToAllDays(loadTime);
      }

      this._numTransitions = this._list.getCurrentOffset();
      if (sortRequired) {
         this.sortTransitions();
      }
   }

   protected void addAccessibleState(int state) {
      if (this.isAccessibleStateSet(1)) {
         this.removeAccessibleState(1);
      }

      this._accessibleStateSet |= state;
   }

   protected void removeAccessibleState(int state) {
      this._accessibleStateSet &= ~state;
   }

   protected void verifyListBoundaries(long loadTime) {
   }

   protected void accessibleEventOccurred(int event, Object oldValue, Object newValue, AccessibleContext context) {
      AccessibleEventDispatcher.dispatchAccessibleEvent(event, oldValue, newValue, context);
   }

   protected long addFreeTimeTransition(DayList$TransitionList list, int index, int sequenceNumber, long loadTime) {
      return 0;
   }

   protected ThemeAttributeSet getDescriptionAreaTheme() {
      return null;
   }

   protected ThemeAttributeSet getAppointmentTheme() {
      return null;
   }

   long getSelectedStartTime() {
      int selection = this._field.getSelectedIndex();
      if (this._field.isMultiSelectInProgress()) {
         selection = Math.min(this._field.getAnchorIndex(), selection);
      }

      return this.getTimeFromOffset(selection);
   }

   long getSelectedEndTime() {
      int selection = this._field.getSelectedIndex();
      if (this._field.isMultiSelectInProgress()) {
         selection = Math.max(this._field.getAnchorIndex(), selection - 1);
      }

      return selection == this._numTransitions - 1 ? this.getTimeFromOffset(selection) + 3600000 : this.getTimeFromOffset(selection + 1);
   }

   protected Object getCurrentObject() {
      return this.getSelectedObject(true);
   }

   Object getSelectedObject() {
      return this.getSelectedObject(false);
   }

   protected void setSelectedObject(Object object) {
      this.setSelectedObject(object, -1);
   }

   protected void setSelectedObject(Object object, long time) {
      if (object != null) {
         long uid = 0;
         if (object instanceof Object) {
            UniqueIDProvider uniqueIDProvider = (UniqueIDProvider)object;
            uid = uniqueIDProvider.getLUID(null);
         }

         boolean found = false;
         int max = this._list.getLength();

         for (int i = 0; i < max; i++) {
            DayList$Transition curTrans = this._list.getAt(i);
            if (time == -1 || curTrans._timeInMillis == time) {
               Object currentObject = curTrans._calElement;
               if (currentObject == object) {
                  found = true;
               } else if (uid != 0 && currentObject instanceof Object && ((UniqueIDProvider)currentObject).getLUID(null) == uid) {
                  found = true;
               }

               if (found) {
                  this._preferredTimeSelection = 0;
                  this._field.setSelectedIndex(i);
                  return;
               }
            }
         }
      }
   }

   int getNumberOfItems() {
      return this._numTransitions;
   }

   void setSummaryLines(int summaryLines) {
      this._summaryLines = summaryLines;
   }

   protected void setDisplayLines(boolean displayLines) {
      this._displayLines = displayLines;
   }

   protected void setDisplayTimeBars(boolean displayTimeBars) {
      this._displayTimeBars = displayTimeBars;
   }

   protected void setDisplayEndTimes(boolean displayEndTimes) {
      this._displayEndTimes = displayEndTimes;
   }

   protected void setDisplayInformationIcons(boolean displayInformationIcons) {
      this._displayInformationIcons = displayInformationIcons;
   }

   protected void setClampTimes(boolean clampTimes) {
      this._clampTimes = clampTimes;
   }

   int getFirstNonEmptyTransitionOffset() {
      return this._firstNonEmptyTransition;
   }

   int getFirstAllDayTransitionOffset() {
      return this._firstAllDayTransition;
   }

   int getOffsetFromTime(long targetTime) {
      if (targetTime >= this._startOfList && targetTime < this._endOfList) {
         int max = this._numTransitions;
         int offset = -1;

         for (int i = 0; i < max; i++) {
            DayList$Transition curr = this._list.getAt(i);
            if (targetTime <= curr._timeInMillis) {
               offset = i;
               break;
            }
         }

         return offset;
      } else {
         return -1;
      }
   }

   long getTimeFromOffset(int offset) {
      int size = this._list.getLength();
      if (offset < 0 || offset > size) {
         offset = 0;
      }

      return offset <= size ? this._list.getAt(offset)._timeInMillis : 0;
   }

   int getSelectedOffset() {
      int selection = this._field.getSelectedIndex();
      return selection >= 0 && selection < this._list.getLength() ? selection : 0;
   }

   void setSelectedOffset(int selection) {
      this._preferredTimeSelection = 0;
      if (selection >= 0 && selection < this._list.getLength()) {
         this._field.setSelectedIndex(selection);
      }
   }

   void makeOffsetVisible(int offset) {
      this.makeOffsetVisible(offset, false);
   }

   void makeOffsetVisible(int offset, boolean forceAtTop) {
      this._field.makeOffsetVisible(offset, forceAtTop);
   }

   void markOffset(int offset) {
      if (offset < 0 || offset >= this._numTransitions || this._list.getAt(offset)._transitionType == 1 || this._list.getAt(offset)._transitionType == 2) {
         offset = -1;
      }

      this._markedOffset = offset;
      this._field.invalidate();
   }

   void setPreferredTimeSelection(long time) {
      this._preferredTimeSelection = time;
   }

   long getPreferredTimeSelection() {
      return this._preferredTimeSelection;
   }

   protected void updateTransitions(Vector events, long loadTime) {
      this.getTransitionsFromEvents(events, loadTime);
      this.addDateTimeTransitions();
      this.updateBars();
   }

   protected void sortTransitions() {
      this._list.sort();
   }

   protected long getLongTimeFromStartFromEventAndTime(long eventStartTime, int time) {
      CalendarExtensions calEx = (CalendarExtensions)this._cal;
      Calendar cal = this._cal;
      calEx.setTimeLong(eventStartTime);
      DateTimeUtilities.zeroCalendarTime(cal);
      if (cal.get(11) == 23) {
         calEx.add(10, 1);
      }

      int hour = time / 3600000;
      int minute = time % 3600000 / 60000;
      cal.set(9, 0);
      cal.set(12, minute);
      DateTimeUtilities.calendarSetWithDst(cal, calEx, 11, hour);
      return calEx.getTimeLong();
   }

   protected void addFreeTimeToAllDays(long loadTime) {
   }

   @Override
   public int getBackgroundColor() {
      return 16777215;
   }

   @Override
   public boolean isAccessibleStateSet(int state) {
      return (this._accessibleStateSet & state) != 0;
   }

   @Override
   public int getAccessibleStateSet() {
      return this._accessibleStateSet;
   }

   @Override
   public boolean isAccessibleChildSelected(int index) {
      return false;
   }

   @Override
   public AccessibleContext getAccessibleSelectionAt(int index) {
      return null;
   }

   @Override
   public int getAccessibleSelectionCount() {
      return 0;
   }

   @Override
   public int getAccessibleRole() {
      return 0;
   }

   @Override
   public AccessibleContext getAccessibleChildAt(int index) {
      String time = "";
      String date = "";
      Object selectedObject = this.getSelectedObject();
      if (selectedObject != null) {
         return (AccessibleContext)selectedObject;
      }

      int selection = this._field.getSelectedIndex();
      if (selection >= 0 && selection < this._list.getLength()) {
         DayList$Transition entry = this._list.getAt(selection);
         if (entry._transitionType == 3) {
            long startTime = entry._timeInMillis;
            long endTime = entry._endTimeInMillis;
            time = ((StringBuffer)(new Object())).append(time).append(EventUtilities.getFreeTimeDescription(startTime, endTime)).toString();
         } else {
            time = ((StringBuffer)(new Object())).append(time).append(entry._displayTime).toString();
         }

         long start = this.getStartOfList();
         if (start > 0) {
            DateField dateField = (DateField)(new Object("Date: ", start, 16));
            date = ((StringBuffer)(new Object())).append(date).append(dateField.toString()).toString();
         }

         return (AccessibleContext)(new Object(date, time, 28, 4, null, null));
      } else {
         return null;
      }
   }

   @Override
   public int getAccessibleChildCount() {
      return 0;
   }

   @Override
   public AccessibleContext getAccessibleParent() {
      return null;
   }

   @Override
   public AccessibleValue getAccessibleValue() {
      return null;
   }

   @Override
   public AccessibleText getAccessibleText() {
      return null;
   }

   @Override
   public String getAccessibleIconDescription() {
      return null;
   }

   @Override
   public boolean allowFocusAt(Object field, int index) {
      return true;
   }

   @Override
   public void layout(Object field, int width, int height) {
      this._borderMode = CalendarOptions.getOptions().getAppointmentColorMode();
      switch (this._borderMode) {
         case 0:
            this._padding = NO_PADDING;
            break;
         case 1:
         case 2:
         default:
            this._padding = RIGHT_PADDING;
            break;
         case 3:
         case 4:
            this._padding = LEFT_PADDING;
            break;
         case 5:
         case 6:
            this._padding = THICK_PADDING;
      }

      this.layoutWorker(field, width, height);
      if (this._widthOfDesc < this._fontMetrics.iMaxCharWidth * 5) {
         if (!this._timeBarsDisabledDueToSpaceLimitations) {
            this._timeBarsDisabledDueToSpaceLimitations = true;
            this.layoutWorker(field, width, height);
            return;
         }
      } else {
         this._timeBarsDisabledDueToSpaceLimitations = false;
      }
   }

   @Override
   public String getAccessibleDescription() {
      return null;
   }

   @Override
   public int getRowHeight(Object field, int index) {
      if (index < 0) {
         return this._minDayLineHeight;
      }

      if (index >= this._list.getLength()) {
         return 0;
      }

      DayList$Transition entry = this._list.getAt(index);
      byte type = entry._transitionType;
      if (type != 16 && type != 17 && type != 2 && type != 32) {
         return this._minDayLineHeight;
      }

      String str = entry.getSummaryText(this.supportAdvancedThemeing());
      if (str != null && entry._calElement instanceof Object) {
         this.getIconsForTransition(entry);
         int iconWidth = this.getIconWidth(this._icons, this._indices);
         Array.resize(this._icons, 0);
         Array.resize(this._indices, 0);
         int availableDescriptionWidth = this._widthOfDesc - iconWidth - (this._padding.right + this._padding.left);
         if (availableDescriptionWidth < 0) {
            availableDescriptionWidth = 0;
         }

         this._sharedOffsets[1] = str.length();
         Edit$Helper wrapInfo = RichText.calculateLengths(availableDescriptionWidth, str, this._sharedOffsets, this._sharedBytes, this._sharedFonts);
         int dayLineHeight = this._minDayLineHeight;
         int rowCount = wrapInfo._lineCount;

         for (int i = 0; i < rowCount; i++) {
            dayLineHeight = Math.max(dayLineHeight, this.getLineHeight(wrapInfo, i));
         }

         int firstLineWidth = wrapInfo._lengths[0] & 255;
         String vectorStr = null;
         if (firstLineWidth < str.length()) {
            vectorStr = str.substring(firstLineWidth);
            this._sharedOffsets[1] = vectorStr.length();
         }

         if (vectorStr != null) {
            wrapInfo = RichText.calculateLengths(
               this._widthOfDesc - (this._padding.right + this._padding.left), vectorStr, this._sharedOffsets, this._sharedBytes, this._sharedFonts
            );
            rowCount = wrapInfo._lineCount + 1;
         }

         if (rowCount <= 0) {
            rowCount = 1;
         }

         if (this._summaryLines > 0 && rowCount > this._summaryLines) {
            rowCount = this._summaryLines;
         }

         return dayLineHeight * rowCount + this._padding.top + this._padding.bottom;
      } else {
         return this._minDayLineHeight;
      }
   }

   @Override
   public int getPreferredWidth(Object field) {
      return 536870911;
   }

   @Override
   public void drawFocus(Object field, Graphics g, boolean on) {
      CalendarViewListField listField = this.getField();
      listField.paintFocus(g, on);
   }

   @Override
   public void drawListRow(Object field, Graphics graphics, int index, int y, int width, int height) {
      if (ThemeManager.getGeneration() != this._themeGeneration) {
         this._themeGeneration = ThemeManager.getGeneration();
         this.applyTheme();
      }

      if (index >= 0 && index < this._list.getLength()) {
         DayList$Transition entryToDisplay = this._list.getAt(index);
         byte type = entryToDisplay._transitionType;
         if (type == 1) {
            this.drawSeparatorTransition(entryToDisplay, graphics, field, y, width, height);
         } else if (type == 3) {
            this.drawFreeTimeTransition(entryToDisplay, graphics, field, y, width, height);
         } else {
            int currentDayLineHeight = this._minDayLineHeight;
            int diacriticSize = 0;
            char[] barString = entryToDisplay._barString;
            int[] barColours = entryToDisplay._barColours;
            boolean rowHasFocus = this.rowHasFocus(field, y, width, height);
            boolean startOfAppointment = type == 16 || type == 17 || type == 32;
            boolean allDay = type == 2;
            String summaryText = entryToDisplay.getSummaryText(this.supportAdvancedThemeing());
            boolean isDrawingFocus = graphics.isDrawingStyleSet(8) || this._field.isMultiSelectInProgress() && !rowHasFocus;
            XYEdges padding = summaryText == null ? NO_PADDING : this._padding;
            Edit$Helper wrapInfo = null;
            boolean isTwoPassLayout = false;
            byte firstLineLength = 0;
            int max = 1;
            int shortenedOffset = -1;
            if (startOfAppointment || allDay) {
               this.getIconsForTransition(entryToDisplay);
            }

            if (summaryText != null) {
               this._sharedOffsets[1] = summaryText.length();
               int availableDescriptionWidth = this._widthOfDesc - this.getIconWidth(this._icons, this._indices) - (padding.right + padding.left);
               wrapInfo = RichText.calculateLengths(availableDescriptionWidth, summaryText, this._sharedOffsets, this._sharedBytes, this._sharedFonts);
               firstLineLength = wrapInfo._lengths[0];
               String vectorSubstring = null;
               if ((firstLineLength & 255) < summaryText.length()) {
                  vectorSubstring = summaryText.substring(wrapInfo._lengths[0]);
                  this._sharedOffsets[1] = summaryText.length();
               }

               if (vectorSubstring != null) {
                  wrapInfo = RichText.calculateLengths(
                     this._widthOfDesc - (padding.right + padding.left), vectorSubstring, this._sharedOffsets, this._sharedBytes, this._sharedFonts
                  );
                  max = wrapInfo._lineCount + 1;
                  isTwoPassLayout = true;
               } else {
                  max = wrapInfo._lineCount;
               }

               if (this._summaryLines > 0 && max > this._summaryLines) {
                  max = this._summaryLines;
                  shortenedOffset = max - 1;
               }

               if (max <= 0) {
                  max = 1;
               }

               if (height > this._minDayLineHeight) {
                  currentDayLineHeight = Math.max(this._minDayLineHeight, (height - (padding.top + padding.bottom)) / max);
                  diacriticSize = currentDayLineHeight - this._minDayLineHeight;
               }
            }

            ThemeAttributeSet attributeSetToUse = null;
            if (this.supportAdvancedThemeing()) {
               if (entryToDisplay.isInThePast()) {
                  attributeSetToUse = this._themeAttributesPastAppointments;
               } else if (entryToDisplay.conflicts()) {
                  attributeSetToUse = this._themeAttributesConflictingAppointments;
               } else {
                  attributeSetToUse = this._themeAttributesAppointments;
               }
            }

            int currentX = 1;
            if (barString != null && this._displayTimeBars && !this._timeBarsDisabledDueToSpaceLimitations) {
               int oldColour = graphics.getColor();
               graphics.setColor(this.TIME_BAR_COLOUR);
               this.drawTimeBars(graphics, barString, barColours, 2, y, currentDayLineHeight + padding.top + padding.bottom);
               char[] extraLinesBarString = entryToDisplay._extraLinesBarString;
               int[] extraLinesBarColours = entryToDisplay._extraLinesBarColours;
               if (extraLinesBarString != null && this._displayTimeBars && !this._timeBarsDisabledDueToSpaceLimitations) {
                  int yCurr = y + currentDayLineHeight + padding.top + padding.bottom;

                  for (int i = 1; i < max; i++) {
                     this.drawTimeBars(graphics, extraLinesBarString, extraLinesBarColours, 2, yCurr, currentDayLineHeight);
                     yCurr += currentDayLineHeight;
                  }
               }

               graphics.setColor(oldColour);
            }

            if (entryToDisplay._displayTime == null && !allDay && !entryToDisplay._subsequentDayOfSpanningEvent) {
               this._calEx.setTimeLong(entryToDisplay._timeInMillis);
               entryToDisplay._displayTime = TimeStringCache.getString(this._cal);
            }

            int timeXLeft = this.supportAdvancedThemeing() ? 0 : currentX + this._maxSimultaneousBars * this._widthPerBar;
            int timeXRight = timeXLeft + this._maxTimeWidth;
            int timeXMiddle = timeXLeft + (this._maxTimeWidth >> 1);
            graphics.pushContext(timeXLeft, y, width, height, 0, 0);
            if (attributeSetToUse != null) {
               this.applyThemeAttributesToGraphics(graphics, (Field)field, attributeSetToUse);
            } else if (summaryText != null && graphics.isDrawingStyleSet(8)) {
               graphics.clear();
            }

            XYRect textRect = (XYRect)(new Object(this._widthToDesc, y, this._widthOfDesc, height));
            if (summaryText != null && entryToDisplay._colour != -1) {
               int oldColor = graphics.getColor();
               graphics.setColor(entryToDisplay._colour);
               int backgroundAlpha = 15;
               if (graphics.isDrawingStyleSet(8)) {
                  backgroundAlpha <<= 1;
               }

               switch (this._borderMode) {
                  case 0:
                     break;
                  case 1:
                     graphics.fillRect(
                        textRect.x + textRect.width - (padding.right - padding.left), textRect.y, padding.right - padding.left, textRect.height - 1
                     );
                     break;
                  case 2:
                     if (this.supportAdvancedThemeing()) {
                        if (!graphics.isDrawingStyleSet(8)) {
                           graphics.setBackgroundColor(16777215);
                           graphics.clear();
                           graphics.setGlobalAlpha(backgroundAlpha);
                           graphics.fillRect(0, textRect.y, width, textRect.height - 1);
                           graphics.setGlobalAlpha(255);
                        }

                        graphics.fillRect(
                           textRect.x + textRect.width - (padding.right - padding.left), textRect.y, padding.right - padding.left, textRect.height - 1
                        );
                     } else {
                        graphics.setGlobalAlpha(backgroundAlpha);
                        graphics.fillRect(textRect.x, textRect.y, textRect.width - 1, textRect.height - 1);
                        graphics.setGlobalAlpha(255);
                        graphics.fillRect(
                           textRect.x + textRect.width - (padding.right - padding.left), textRect.y, padding.right - padding.left, textRect.height - 1
                        );
                     }
                     break;
                  case 3:
                     if (this.supportAdvancedThemeing()) {
                        graphics.fillRect(0, textRect.y, padding.left - padding.right, textRect.height - 1);
                     } else {
                        graphics.fillRect(textRect.x, textRect.y, padding.left - padding.right, textRect.height - 1);
                     }
                     break;
                  case 4:
                     if (this.supportAdvancedThemeing()) {
                        if (!graphics.isDrawingStyleSet(8)) {
                           graphics.setBackgroundColor(16777215);
                           graphics.clear();
                           graphics.setGlobalAlpha(backgroundAlpha);
                           graphics.fillRect(0, textRect.y, width, textRect.height - 1);
                           graphics.setGlobalAlpha(255);
                        }

                        graphics.fillRect(0, textRect.y, padding.left - padding.right, textRect.height - 1);
                     } else {
                        graphics.setGlobalAlpha(backgroundAlpha);
                        graphics.fillRect(textRect.x, textRect.y, textRect.width - 1, textRect.height - 1);
                        graphics.setGlobalAlpha(255);
                        graphics.fillRect(textRect.x, textRect.y, padding.left - padding.right, textRect.height - 1);
                     }
                     break;
                  case 5:
                  default:
                     if (this.supportAdvancedThemeing()) {
                        graphics.drawRoundRect(1, textRect.y + 1, width - 2, textRect.height - 2, 8, 8);
                     } else {
                        graphics.drawRoundRect(textRect.x + 1 + 1, textRect.y + 1, textRect.width - 2 - 1, textRect.height - 2 - 1, 8, 8);
                     }
                     break;
                  case 6:
                     if (!this.supportAdvancedThemeing()) {
                        XYRect rect = Ui.getTmpXYRect();
                        rect.set(textRect.x + 1 + 1, textRect.y + 1, textRect.width - 2 - 1, textRect.height - 2 - 1);
                        graphics.setGlobalAlpha(backgroundAlpha);
                        graphics.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 8, 8);
                        graphics.setGlobalAlpha(255);
                        graphics.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 8, 8);
                        Ui.returnTmpXYRect(rect);
                     } else {
                        XYRect rect = Ui.getTmpXYRect();
                        rect.set(1, textRect.y + 1, width - 2, textRect.height - 2);
                        if (!graphics.isDrawingStyleSet(8)) {
                           graphics.setBackgroundColor(16777215);
                           graphics.clear();
                           graphics.setGlobalAlpha(backgroundAlpha);
                           graphics.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 8, 8);
                           graphics.setGlobalAlpha(255);
                        }

                        graphics.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 8, 8);
                        Ui.returnTmpXYRect(rect);
                     }
               }

               graphics.setColor(oldColor);
            }

            int borderAdjustment = this._markedOffset == index ? 1 : 0;
            XYRect hilightRect = (XYRect)(new Object());
            XYRect descFillRect = (XYRect)(new Object(this._widthToDesc, y, this._widthOfDesc, height));
            XYRect timeFillRect = (XYRect)(new Object(
               timeXLeft + borderAdjustment,
               y + borderAdjustment,
               this._maxTimeWidth - (borderAdjustment << 1),
               currentDayLineHeight - (borderAdjustment << 1) + padding.top + padding.bottom
            ));
            this._field.getHilightRect(hilightRect, false);
            if (!this.supportAdvancedThemeing()) {
               if (this._markedOffset == index) {
                  int oldColour = graphics.getColor();
                  graphics.setColor(this.MARK_COLOUR);
                  graphics.drawRect(timeXLeft, y, this._maxTimeWidth, currentDayLineHeight + padding.top + padding.bottom);
                  graphics.setColor(oldColour);
               }

               if (hilightRect.contains(timeFillRect) && isDrawingFocus) {
                  this.drawHilightRect(graphics, timeFillRect);
               }
            }

            if (allDay) {
               int oldColour = graphics.getColor();
               int allDayColour = entryToDisplay.isInThePast() ? oldColour : this.ALL_DAY_COLOUR;
               graphics.setColor(hilightRect.contains(timeFillRect) && isDrawingFocus ? this.HILIGHT_FOREGROUND_COLOUR : allDayColour);
               graphics.drawText('♦', timeXLeft, y + diacriticSize + padding.top, 4, this._maxTimeWidth);
               graphics.setColor(oldColour);
            } else if (entryToDisplay._subsequentDayOfSpanningEvent) {
               Bitmap bmp = CLOCK_BITMAP;
               if (bmp != null) {
                  int yOffset = graphics.getFont().getHeight() - bmp.getHeight() >> 1;
                  graphics.drawBitmap(
                     timeXMiddle - (bmp.getWidth() >> 1), y + diacriticSize + padding.top + yOffset, bmp.getWidth(), bmp.getHeight(), bmp, 0, 0
                  );
               }
            } else {
               int oldColor = graphics.getColor();
               if (hilightRect.contains(timeFillRect) && isDrawingFocus) {
                  graphics.setColor(this.HILIGHT_FOREGROUND_COLOUR);
               }

               String timeText = entryToDisplay._displayTime;
               int leftMargin = 0;
               if (this.supportAdvancedThemeing()) {
                  leftMargin = padding.left;
               }

               graphics.drawText(timeText, leftMargin, y + diacriticSize + padding.top, 5, timeXRight);
               graphics.setColor(oldColor);
            }

            graphics.pushContext(textRect, 0, 0);
            ThemeAttributeSet attr = null;
            if (wrapInfo == null) {
               attr = this.getDescriptionAreaTheme();
            } else {
               attr = this.getAppointmentTheme();
            }

            if (attr != null) {
               attr.applyToGraphics(graphics);
               graphics.clear();
            }

            if (!startOfAppointment && !allDay) {
               XYRect caretFillRect = (XYRect)(new Object(this._widthToDesc, y, this._field.getFont().getBounds(' '), height));
               if (rowHasFocus && isDrawingFocus) {
                  this.drawHilightRect(graphics, caretFillRect);
               } else {
                  graphics.clear(caretFillRect);
               }
            } else if (this._field.isMultiSelectInProgress() && isDrawingFocus && hilightRect.contains(descFillRect)) {
               this.drawHilightRect(graphics, descFillRect);
            }

            int iconWidth = 0;
            if (this._displayInformationIcons) {
               iconWidth = this.getIconWidth(this._icons, this._indices);
            }

            if (this._displayLines) {
               int oldColor = graphics.getColor();
               graphics.setColor(this.LINE_COLOUR);
               graphics.drawLine(this._widthToDesc, y, this._widthToDesc, y + currentDayLineHeight * max + padding.bottom + padding.top);
               graphics.setStipple(1431655765);
               int yLine = y + currentDayLineHeight * max - 1 + padding.bottom + padding.top;
               graphics.drawLine(this._widthToDesc, yLine, width, yLine);
               graphics.setColor(oldColor);
               graphics.setStipple(-1);
            }

            if (startOfAppointment || allDay) {
               int availableDescriptionWidth = this._widthOfDesc - iconWidth - (this._padding.right + this._padding.left);
               if (availableDescriptionWidth < 0) {
                  availableDescriptionWidth = 0;
               }

               if (summaryText != null) {
                  int dayLineHeight = currentDayLineHeight;
                  int yCurr = y + dayLineHeight + padding.bottom;
                  if (shortenedOffset >= 0) {
                     max--;
                  }

                  byte[] lengths = wrapInfo._lengths;
                  int offset = 0;
                  int length = 0;
                  yCurr = y + padding.top;
                  int summaryPadding = padding.left;
                  if (this._displayLines) {
                     summaryPadding++;
                  }

                  int oldColor = graphics.getColor();
                  if (hilightRect.contains(this._widthToDesc, y, this._widthOfDesc, height) && this._field.isMultiSelectInProgress() && isDrawingFocus) {
                     graphics.setColor(this.HILIGHT_FOREGROUND_COLOUR);
                  } else if (allDay && !entryToDisplay.isInThePast()) {
                     if (this.supportAdvancedThemeing()) {
                        graphics.setColor(
                           hilightRect.contains(this._widthToDesc, y, this._widthOfDesc, height) && isDrawingFocus
                              ? this.HILIGHT_FOREGROUND_COLOUR
                              : this.ALL_DAY_COLOUR
                        );
                     } else {
                        graphics.setColor(this.ALL_DAY_COLOUR);
                     }
                  }

                  for (int i = 0; i < max; i++) {
                     int descriptionWidth = availableDescriptionWidth;
                     if (i == 0) {
                        length = firstLineLength & 255;
                     } else if (isTwoPassLayout) {
                        length = lengths[i - 1] & 255;
                        descriptionWidth = this._widthOfDesc - (this._padding.right + this._padding.left);
                     } else {
                        length = lengths[i] & 255;
                     }

                     graphics.drawText(summaryText, offset, length, this._widthToDesc + summaryPadding, yCurr + diacriticSize, 0, descriptionWidth);
                     offset += length;
                     yCurr += dayLineHeight;
                  }

                  if (shortenedOffset >= 0) {
                     graphics.drawText(
                        summaryText,
                        offset,
                        summaryText.length() - offset,
                        this._widthToDesc + summaryPadding,
                        yCurr + diacriticSize,
                        64,
                        availableDescriptionWidth
                     );
                  }

                  graphics.setColor(oldColor);
               }

               if (iconWidth > 0) {
                  int iconStart = width - iconWidth - padding.right;
                  int numIcons = 0;

                  for (int i = 0; i < this._icons.length; i++) {
                     IconCollection iconCollection = this._icons[i];
                     int[] indices = this._indices[i];
                     numIcons = indices.length;

                     for (int j = 0; j < numIcons; j++) {
                        int currentIconWidth = iconCollection.getWidth(Integer.MAX_VALUE, this._minDayLineHeight);
                        iconCollection.paint(graphics, iconStart, y + padding.top, currentIconWidth, this._minDayLineHeight, indices[j]);
                        iconStart += currentIconWidth + 1;
                     }
                  }

                  Array.resize(this._icons, 0);
                  Array.resize(this._indices, 0);
               }
            }

            graphics.popContext();
            graphics.popContext();
         }
      }
   }

   @Override
   public String getAccessibleName() {
      return null;
   }

   @Override
   public void focusMoved(Object field, int amount, int status, int time, int currentlySelectedIndex) {
      this._preferredTimeSelection = 0;
   }

   @Override
   public String getEmptyString(Object field) {
      return null;
   }

   protected static DayList$Transition[] makeRoomForTransitions(int minimumSize, DayList$Transition[] transitions) {
      int oldSize = -1;
      if (transitions == null) {
         oldSize = 0;
         transitions = new DayList$Transition[minimumSize];
      } else if (minimumSize >= transitions.length) {
         minimumSize += 10;
         oldSize = transitions.length;
         Array.resize(transitions, minimumSize);
      }

      if (oldSize >= 0) {
         for (int i = oldSize; i < minimumSize; i++) {
            transitions[i] = new DayList$Transition();
         }
      }

      return transitions;
   }

   private Object getSelectedObject(boolean middleAndEnd) {
      int selection = this._field.getSelectedIndex();
      if (selection >= 0 && selection < this._list.getLength()) {
         DayList$Transition temp = this._list.getAt(selection);
         return middleAndEnd || temp._transitionType != 18 && temp._transitionType != 19 ? temp._calElement : null;
      } else {
         return null;
      }
   }

   private boolean rowHasFocus(Object field, int y, int width, int height) {
      XYRect focusRect = (XYRect)(new Object());
      ((Field)field).getFocusRect(focusRect);
      return focusRect.contains(0, y, width, height);
   }

   private void drawSeparatorTransition(DayList$Transition entryToDisplay, Graphics graphics, Object field, int y, int width, int height) {
      ThemeAttributeSet attributeSetToUse = entryToDisplay.isInThePast() ? this._themeAttributesPastHeader : this._themeAttributesHeader;
      if (entryToDisplay._displayTime == null) {
         this._buf.setLength(0);
         this._calEx.setTimeLong(entryToDisplay._timeInMillis);
         this.formatSeparatorDate(this._cal, this._buf);
         entryToDisplay._displayTime = this._buf.toString();
      }

      graphics.pushRegion(0, y, width, height, 0, 0);
      this.applyThemeAttributesToGraphics(graphics, (Field)field, attributeSetToUse);
      String datesep = entryToDisplay._displayTime;
      graphics.drawText(datesep, 0, datesep.length(), 0, 0, 4, width);
      graphics.popContext();
   }

   private void drawFreeTimeTransition(DayList$Transition entryToDisplay, Graphics graphics, Object field, int y, int width, int height) {
      ThemeAttributeSet attributeSetToUse = entryToDisplay.isInThePast()
         ? this._themeAttributesPastFreeTimeSeperators
         : this._themeAttributesFreeTimeSeperators;
      graphics.pushRegion(0, y, width, height, 0, 0);
      this.applyThemeAttributesToGraphics(graphics, (Field)field, attributeSetToUse);
      this._calEx.setTimeLong(entryToDisplay._timeInMillis);
      String timeText = TimeStringCache.getString(this._cal);
      graphics.drawText(timeText, 0, timeText.length(), this._padding.left, 0, 5, this._maxTimeWidth);
      String displayString = "";
      if (CalendarOptions.getOptions().showEndTime()) {
         this._calEx.setTimeLong(entryToDisplay._endTimeInMillis);
         displayString = ((StringBuffer)(new Object()))
            .append(displayString)
            .append("- ")
            .append(TimeStringCache.getString(this._cal))
            .append(" ")
            .append(this._rb.getString(647))
            .toString();
      } else {
         long startTime = entryToDisplay._timeInMillis;
         long endTime = entryToDisplay._endTimeInMillis;
         displayString = ((StringBuffer)(new Object())).append(displayString).append(EventUtilities.getFreeTimeDescription(startTime, endTime)).toString();
      }

      graphics.drawText(displayString, 0, displayString.length(), this._widthToDesc + this._padding.left, 0, 0, width - this._widthToDesc - this._padding.right);
      graphics.popContext();
   }

   private void applyThemeAttributesToGraphics(Graphics graphics, Field ffield, ThemeAttributeSet attributeSetToUse) {
      if (attributeSetToUse != null) {
         ffield.setThemeAttributesSpecial(attributeSetToUse, graphics);
         ffield.setThemeAttributesSpecial(null, null);
      }
   }

   private void layoutWorker(Object field, int width, int height) {
      Font font = this.getField().getFont();
      if (this.supportAdvancedThemeing()) {
         if (ThemeManager.getGeneration() != this._themeGeneration) {
            this._themeGeneration = ThemeManager.getGeneration();
            this.applyTheme();
         }

         if (this._themeAttributesAppointments != null) {
            Font themeFont = this._themeAttributesAppointments.getFont();
            if (themeFont != null) {
               font = themeFont;
            }
         }
      }

      this._widthPerBar = this._displayTimeBars && !this._timeBarsDisabledDueToSpaceLimitations ? 6 : 0;
      font.getMetrics(this._fontMetrics);
      this._minDayLineHeight = this._fontMetrics.iHeight + (this._fontMetrics.iLeadingAbove << 1);
      if (Graphics.isColor() && this._minDayLineHeight < 14) {
         this._minDayLineHeight = 14;
      }

      this._maxTimeWidth = this.calculateMaxTimeWidth(font) + 1;
      this._widthToDesc = this._maxSimultaneousBars * this._widthPerBar + this._maxTimeWidth + 3 + 1;
      this._widthOfDesc = width - this._widthToDesc - (Graphics.SCREEN_HAS_BORDER ? 1 : 0);
      this._sharedFonts[0] = font;
   }

   DayList(boolean multiSelectAllowed) {
      this._padding = NO_PADDING;
      this._accessibleStateSet = 1;
      this._field = this.createCalendarViewListField(this, multiSelectAllowed);
   }

   private void drawTimeBars(Graphics graphics, char[] barString, int[] barColours, int x, int y, int lineHeight) {
      for (int i = 0; i < barString.length; i++) {
         graphics.setColor(barColours[i]);
         char chr = barString[i];
         if (chr == 9484) {
            int xx = i * this._widthPerBar + x;
            int yy = y + lineHeight / 2;
            graphics.drawLine(xx, yy, xx + 5, yy);
            graphics.fillRect(xx, yy, 3, (lineHeight >> 1) + 1);
         } else if (chr == 9474) {
            int xx = i * this._widthPerBar + x;
            graphics.fillRect(xx, y, 3, lineHeight);
         } else if (chr == 9492) {
            int xx = i * this._widthPerBar + x;
            int yy = y + lineHeight / 2;
            graphics.fillRect(xx, y, 3, lineHeight >> 1);
            graphics.drawLine(xx, yy, xx + 5, yy);
         }
      }
   }

   private void drawHilightRect(Graphics graphics, XYRect hilightRect) {
      int oldColor = graphics.getColor();
      graphics.setColor(this.HILIGHT_BACKGROUND_COLOUR);
      graphics.fillRect(hilightRect.x, hilightRect.y, hilightRect.width, hilightRect.height);
      graphics.setColor(oldColor);
   }

   private int getLineHeight(Edit$Helper wrapInfo, int line) {
      int ascentPlusLeading = wrapInfo._heights[line] & 255;
      int descent = wrapInfo._heights[line] >> 8 & 0xFF;
      return ascentPlusLeading + descent + this._fontMetrics.iLeadingAbove;
   }

   private void updateBars() {
      if (this._displayTimeBars && !this._timeBarsDisabledDueToSpaceLimitations) {
         this._maxSimultaneousBars = 0;
         Object[] objects = new Object[0];
         char[] bars = new char[0];
         char[] extraLineBars = new char[0];
         int[] extraLineBarColours = new int[0];
         int[] colours = new int[0];
         int numTransitions = this._numTransitions;
         this._firstNonEmptyTransition = -1;
         this._firstAllDayTransition = -1;

         for (int i = 0; i < numTransitions; i++) {
            DayList$Transition curr = this._list.getAt(i);
            byte type = curr._transitionType;
            boolean foundEventBar = false;
            boolean copyExtraLines = false;
            int numBars;
            int firstEmpty = numBars = bars.length;

            for (int j = numBars - 1; j >= 0; j--) {
               char currentBar = bars[j];
               if (currentBar == 9484) {
                  bars[j] = 9474;
               }

               if (currentBar == 9492) {
                  bars[j] = '\uf460';
                  extraLineBars[j] = '\uf460';
                  extraLineBarColours[j] = colours[j];
               }

               if ((type == 19 || type == 18) && objects[j] == curr._calElement) {
                  bars[j] = 9492;
               }

               if (bars[j] == '\uf460') {
                  if (!foundEventBar) {
                     Array.resize(bars, j);
                     Array.resize(extraLineBars, j);
                     Array.resize(extraLineBarColours, j);
                     Array.resize(objects, j);
                     Array.resize(colours, j);
                  }

                  firstEmpty = j;
               } else {
                  foundEventBar = true;
               }
            }

            numBars = bars.length;
            if (numBars > 24) {
               numBars = 24;
            }

            boolean allDay = type == 2;
            if (type == 16 || type == 17 || type == 32 || allDay) {
               if (numBars <= 24 && !allDay) {
                  copyExtraLines = true;
                  if (firstEmpty >= numBars) {
                     firstEmpty = numBars++;
                     Array.resize(extraLineBars, numBars);
                     Array.resize(extraLineBarColours, numBars);
                     Array.resize(bars, numBars);
                     Array.resize(objects, numBars);
                     Array.resize(colours, numBars);
                     if (numBars > 24) {
                        numBars = 24;
                     }
                  }

                  if (type == 32) {
                     bars[firstEmpty] = '\uf460';
                     extraLineBars[firstEmpty] = '\uf460';
                  } else {
                     bars[firstEmpty] = 9484;
                     extraLineBars[firstEmpty] = 9474;
                  }

                  objects[firstEmpty] = curr._calElement;
                  int colour = curr._colour == -1 ? this.TIME_BAR_COLOUR : curr._colour;
                  colours[firstEmpty] = colour;
                  extraLineBarColours[firstEmpty] = colour;
               }

               if (allDay) {
                  if (this._firstAllDayTransition < 0) {
                     this._firstAllDayTransition = i;
                  }
               } else if (this._firstNonEmptyTransition < 0) {
                  this._firstNonEmptyTransition = i;
               }
            }

            if (numBars > 0) {
               curr._barString = new char[numBars];
               curr._barColours = new int[numBars];
               System.arraycopy(bars, 0, curr._barString, 0, numBars);
               System.arraycopy(colours, 0, curr._barColours, 0, numBars);
               if (copyExtraLines) {
                  curr._extraLinesBarString = new char[numBars];
                  curr._extraLinesBarColours = new int[numBars];
                  System.arraycopy(extraLineBars, 0, curr._extraLinesBarString, 0, numBars);
                  System.arraycopy(extraLineBarColours, 0, curr._extraLinesBarColours, 0, numBars);
               }
            } else {
               curr._barString = null;
               curr._barColours = null;
            }

            if (numBars > this._maxSimultaneousBars) {
               this._maxSimultaneousBars = numBars;
            }
         }
      }
   }

   private Object[] getListItems() {
      Object[] ret = new Object[this.getNumberOfItems()];

      for (int i = ret.length - 1; i >= 0; i--) {
         ret[i] = this._list.getAt(i);
      }

      return ret;
   }
}
