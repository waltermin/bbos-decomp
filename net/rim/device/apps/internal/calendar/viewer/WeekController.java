package net.rim.device.apps.internal.calendar.viewer;

import java.util.Vector;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.framework.model.DescriptionProvider;
import net.rim.device.internal.ui.Edit$Helper;
import net.rim.device.internal.ui.RichText;

final class WeekController extends CalendarViewController implements GlobalEventListener {
   private int[] _sharedOffsets = new int[]{0, 0, -804651006, 0, -1, 51, 527827200, 16810638};
   private final byte[] _sharedBytes = new byte[]{0};
   private Font[] _sharedFonts = new Object[1];
   private int _subjectHeight;
   private int _locationHeight;
   private int _timeHeight;
   private StringBuffer _stringBuffer = (StringBuffer)(new Object());
   private WeekField _weekField = new WeekField();
   private WeekController$LayoutLabelField _subjectField = new WeekController$LayoutLabelField(null, 1152921504606847040L);
   private WeekController$LayoutLabelField _locationField = new WeekController$LayoutLabelField(null, 1152921504606847040L);
   private WeekController$LayoutLabelField _timeField = new WeekController$LayoutLabelField(null, 1152921504606847040L);
   private Vector _eventVector = (Vector)(new Object());
   private static final int MIN_FONT_HEIGHT_PT = 6;

   public WeekController(CalendarApp calendarUIApplication, CalendarActions calActions) {
      super(calendarUIApplication, calActions, new WeekVerbManager(calActions), true, true);
      this.setDelayedHeaderRendering(true);
   }

   @Override
   protected final void initializeAdditionalFields() {
      this._weekField.init(this);
   }

   @Override
   protected final void uninitializeAdditionalFields() {
      this._weekField.uninit();
   }

   @Override
   protected final void addAdditionalFields(Screen screen) {
      this.setFonts();
      Tag tag = Tag.create("week-details");
      this._subjectField.setTag(tag);
      this._locationField.setTag(tag);
      this._timeField.setTag(tag);
      screen.add(this._weekField);
      screen.add(this._subjectField);
      screen.add(this._locationField);
      screen.add(this._timeField);
      this._weekField.setFocus();
   }

   @Override
   protected final long getSelectedStartTime() {
      return this._weekField.getSelectedStartTime();
   }

   @Override
   protected final long getSelectedEndTime() {
      return this._weekField.getSelectedEndTime();
   }

   @Override
   protected final Object getSelectedObject() {
      return this._weekField.getSelectedObject();
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
      this._weekField.loadWeek(time, this._eventVector);
      return new WeekController$DoUIRunnable(this, time, updateSelectedDate, object, this._eventVector);
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

   private final void setFonts() {
      Font normal = Font.getDefault().derive(0, 6, 3);
      this._subjectField.setFont(normal);
      this._locationField.setFont(normal);
      this._timeField.setFont(normal);
      this._weekField.setFont(normal);
      this._sharedFonts[0] = normal;
      this._timeHeight = normal.getHeight();
      this._subjectHeight = this._timeHeight;
      this._locationHeight = this._timeHeight;
   }

   @Override
   public final void initialize() {
      super.initialize();
      this.getApplication().addGlobalEventListener(this);
   }

   @Override
   public final void uninitialize() {
      super.uninitialize();
      this.getApplication().removeGlobalEventListener(this);
   }

   @Override
   public final void display(int previousView, boolean returnToPreviousView) {
      CalendarOptions calendarOptions = CalendarOptions.getOptions();
      int firstDOW = calendarOptions.getFirstDayOfWeek() - 1;
      this._weekField.setInitialDOWs(firstDOW, firstDOW);
      int startOfDay = calendarOptions.getDayStart();
      int endOfDay = calendarOptions.getDayEnd();
      if (endOfDay < startOfDay + 3600000) {
         endOfDay = startOfDay + 3600000;
      }

      this._weekField.setStartAndEndOfDay(startOfDay / 3600000, (endOfDay - 1) / 3600000 + 1);
      super.display(previousView, returnToPreviousView);
   }

   @Override
   public final void optionsChanged(int changedOptions) {
      CalendarOptions calendarOptions = CalendarOptions.getOptions();
      if ((changedOptions & CalendarOptions.FIRST_DOW) != 0) {
         this._weekField.setInitialDOWs(calendarOptions.getFirstDayOfWeek() - 1, calendarOptions.getFirstDayOfWeek() - 1);
      } else {
         if ((changedOptions & CalendarOptions.START_OF_DAY) != 0 || (changedOptions & CalendarOptions.END_OF_DAY) != 0) {
            this._weekField.setStartAndEndOfDay(calendarOptions.getDayStart() / 3600000, (calendarOptions.getDayEnd() - 1) / 3600000 + 1);
         }
      }
   }

   @Override
   public final void selectedDateChanged(long selectedDate) {
      this.updateSelectedDate(selectedDate);
   }

   private final int measureHeight(String text, int width) {
      if (text != null && text.length() != 0) {
         Edit$Helper wrapInfo = RichText.calculateLengths(width, text, this._sharedOffsets, this._sharedBytes, this._sharedFonts);
         return (wrapInfo._heights[0] & 0xFF) + (wrapInfo._heights[0] >> 8 & 0xFF);
      } else {
         return this._sharedFonts[0].getHeight();
      }
   }

   @Override
   public final void selectedEventChanged(Object event) {
      if (event == null) {
         this._subjectField.setText(null);
         this._locationField.setText(null);
         this._timeField.setText(null);
      } else {
         DescriptionProvider dp = (DescriptionProvider)event;
         if (this._weekField.hasLowResolution()) {
            this._subjectField.setText(dp.getStringForField(-4581712257088750184L));
         } else {
            int totalHeight = this._locationHeight + this._subjectHeight + this._timeHeight + this._weekField.getHeight() + this.getHeaderHeight();
            boolean isOverflow = totalHeight > Display.getHeight();
            String subject = dp.getStringForField(5649235763655597796L);
            String location = dp.getStringForField(9164664086580876244L);
            String time = dp.getStringForField(-8797898085576394050L);
            boolean needLayout = false;
            int height = this.measureHeight(subject, this._weekField.getWidth());
            if (height != this._subjectHeight && (isOverflow || height > this._subjectHeight)) {
               this._subjectHeight = height;
               needLayout = true;
            }

            height = this.measureHeight(location, this._weekField.getWidth());
            if (height != this._locationHeight && (isOverflow || height > this._locationHeight)) {
               this._locationHeight = height;
               needLayout = true;
            }

            totalHeight = this._locationHeight + this._subjectHeight + this._timeHeight + this._weekField.getHeight() + this.getHeaderHeight();
            if (totalHeight > Display.getHeight()) {
               this._stringBuffer.setLength(0);
               this._stringBuffer.append(time);
               this._stringBuffer.append(' ');
               this._stringBuffer.append(subject);
               this._subjectField.setText(this._stringBuffer.toString());
               this._timeField.setText(null);
            } else {
               this._subjectField.setText(subject);
               this._timeField.setText(time);
            }

            this._locationField.setText(location);
            if (needLayout) {
               this._subjectField.forceUpdateLayout();
            }
         }
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      super.eventOccurred(guid, data0, data1, object0, object1);
      if (guid == -4394903006263251010L) {
         this.setFonts();
      }
   }
}
