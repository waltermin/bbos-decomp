package net.rim.device.apps.internal.freebusy;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Attendee;
import net.rim.device.cldc.util.CalendarExtensions;

class FreeBusyField extends Field {
   private Theme _theme = ThemeManager.getActiveTheme();
   private int _attendeesOnScreen = 0;
   private int _fieldHeight;
   private int _fieldWidth;
   private int _pixelsPerAttendeeColumn;
   private int _pixelPerTimeColumn;
   private int _pixelsPerRowHeight;
   private int _numberOfAttendees;
   private TimeIntervalColumn[] _timeIntervals;
   private TimeIntervalColumn[] _timeIntervalsPrevious;
   private TimeIntervalColumn[] _timeIntervalsNext;
   private int _timeIntervalFocus;
   private int _timeIntervalStart;
   private int _timeIntervalEnd;
   private int _attendeeWithFocus;
   private int _attendeeStart;
   private int _attendeeEnd;
   private int _dayStart = 32400000;
   private int _dayEnd = 6120000;
   private Calendar _cal = Calendar.getInstance();
   private CalendarExtensions _calEx = (CalendarExtensions)(new Object());
   private AttendeeList _attendeeList;
   private Date _originalMeetingStartDate;
   private Date _originalMeetingEndDate;
   private Date _highlightedDate;
   private FreeBusyController _callback;
   private boolean _fieldHasFocus;
   private static final int NUM_TIME_INTERVAL_COLUMNS;
   private static final int NUM_SUB_TIME_INTERVALS;
   private static final int NUM_ATTENDEE_COLUMNS;
   private static final int ATTENDEE_TIME_WIDTH_RATIO;
   private static final int MINUTES_PER_INTERVAL;
   private static final int MINUTES_PER_SUB_INTERVAL;
   private static final int MAX_ATTENDEES_ON_SCREEN;
   private static final int EXTRA_ROWS_NEEDED;
   private static final int COLUMN_SEPARATOR_WIDTH;
   private static final int ROW_SEPARATOR_HEIGHT;

   FreeBusyField(Enumeration attendees, long meetingStartDate, long meetingEndDate, FreeBusyController callback) {
      super(18014398509481984L);
      this._callback = callback;
      this._attendeeList = new AttendeeList(attendees);
      this._originalMeetingStartDate = (Date)(new Object(meetingStartDate));
      this._originalMeetingEndDate = (Date)(new Object(meetingEndDate));
      this._highlightedDate = (Date)(new Object(meetingStartDate));
      this._numberOfAttendees = this._attendeeList.size();
      this._timeIntervals = new TimeIntervalColumn[24];
      this._timeIntervalsPrevious = new TimeIntervalColumn[24];
      this._timeIntervalsNext = new TimeIntervalColumn[24];
      this._dayStart = CalendarOptions.getOptions().getDayStart();
      this._dayEnd = CalendarOptions.getOptions().getDayEnd();
      this.focusOnHighlightedDate();
      this._attendeeWithFocus = 0;
      this._attendeeStart = 0;
      this._attendeesOnScreen = Math.min(this._numberOfAttendees, 6);
      this._attendeeEnd = this._attendeeStart + this._attendeesOnScreen - 1;
      if (this._attendeeEnd >= this._numberOfAttendees) {
         this._attendeeEnd = this._numberOfAttendees - 1;
      }
   }

   private void focusOnHighlightedDate() {
      Calendar day = Calendar.getInstance();
      day.setTime(this._highlightedDate);
      day.set(11, 0);
      day.set(12, 0);
      day.set(13, 0);
      day.set(14, 0);
      this._calEx.setTimeLong(day.getTime().getTime());
      Calendar intervalTime = Calendar.getInstance();
      intervalTime.setTime((Date)(new Object(this._calEx.getTimeLong())));

      for (int i = 0; i < 24; i++) {
         intervalTime.set(11, i);
         this._timeIntervals[i] = new TimeIntervalColumn(intervalTime.getTime().getTime(), i, this._numberOfAttendees);
      }

      this._calEx.roll(5, -1);
      intervalTime.setTime((Date)(new Object(this._calEx.getTimeLong())));

      for (int i = 0; i < 24; i++) {
         intervalTime.set(11, i);
         this._timeIntervalsPrevious[i] = new TimeIntervalColumn(intervalTime.getTime().getTime(), i, this._numberOfAttendees);
      }

      this._calEx.roll(5, 2);
      intervalTime.setTime((Date)(new Object(this._calEx.getTimeLong())));

      for (int i = 0; i < 24; i++) {
         intervalTime.set(11, i);
         this._timeIntervalsNext[i] = new TimeIntervalColumn(intervalTime.getTime().getTime(), i, this._numberOfAttendees);
      }

      this._calEx.roll(5, -1);
      Calendar meetingStartCalendar = Calendar.getInstance();
      meetingStartCalendar.setTime((Date)(new Object(this._highlightedDate.getTime())));
      this._timeIntervalFocus = meetingStartCalendar.get(11);
      this._timeIntervalStart = this._timeIntervalFocus - 1;
      this._timeIntervalEnd = this._timeIntervalStart + 4 - 1;
      this._timeIntervals[this._timeIntervalFocus].setFocus(true);
      this.invalidate();
   }

   public void setHighlightedDate(long newDate) {
      this._highlightedDate = (Date)(new Object(newDate));
      this.focusOnHighlightedDate();
      this.updateHeaderDate(newDate);
   }

   @Override
   protected void layout(int width, int height) {
      this._fieldHeight = height;
      this._fieldWidth = width;
      this._pixelPerTimeColumn = width / 6;
      this._pixelsPerAttendeeColumn = width * 2 / 6;
      this._pixelsPerRowHeight = this.getFont().getHeight();
      this._fieldHeight = Math.min(height, Display.getHeight());
      this._fieldHeight = this._pixelsPerRowHeight * (this._attendeesOnScreen + 2);
      this._fieldWidth = Display.getWidth();
      this.setExtent(this._fieldWidth, this._fieldHeight);
      if (width > Display.getWidth() || height > Display.getHeight()) {
         Dialog.alert("Screen layout too large, larger than screen size");
      }
   }

   private boolean scaleGrid() {
      int totalRows = Math.min(this._numberOfAttendees, 6) + 2;
      this._fieldHeight = Math.min(this._pixelsPerRowHeight * totalRows, Display.getHeight());
      this.setExtent(this._fieldWidth, this._fieldHeight);
      return true;
   }

   @Override
   protected void paint(Graphics graphics) {
      int rowIndex = 0;
      Attendee[] attendees = this._attendeeList.getAttendees();
      graphics.setColor(13882323);
      graphics.fillRect(0, rowIndex * this._pixelsPerRowHeight, this._pixelsPerAttendeeColumn, this._pixelsPerRowHeight);
      graphics.setColor(0);
      graphics.drawRect(0, rowIndex * this._pixelsPerRowHeight, this._pixelsPerAttendeeColumn, this._pixelsPerRowHeight);
      graphics.drawText("Availability", 0, rowIndex * this._pixelsPerRowHeight, 4, this._pixelsPerAttendeeColumn);
      rowIndex++;
      graphics.setColor(4620980);
      graphics.fillRect(0, rowIndex * this._pixelsPerRowHeight, this._pixelsPerAttendeeColumn, this._pixelsPerRowHeight);
      graphics.setColor(0);
      graphics.drawRect(0, rowIndex * this._pixelsPerRowHeight, this._pixelsPerAttendeeColumn, this._pixelsPerRowHeight);
      graphics.drawText("All Attendees", 0, rowIndex * this._pixelsPerRowHeight, 4, this._pixelsPerAttendeeColumn);
      System.out.println(((StringBuffer)(new Object("Painting Number of attendees: "))).append(this._numberOfAttendees).toString());
      rowIndex++;

      for (int i = this._attendeeStart; i <= this._attendeeEnd; i++) {
         graphics.setColor(4620980);
         graphics.fillRect(0, rowIndex * this._pixelsPerRowHeight, this._pixelsPerAttendeeColumn, this._pixelsPerRowHeight);
         graphics.setColor(0);
         graphics.drawRect(0, rowIndex * this._pixelsPerRowHeight, this._pixelsPerAttendeeColumn, this._pixelsPerRowHeight);
         graphics.drawText(attendees[i].getAddress().toString(), 0, rowIndex * this._pixelsPerRowHeight, 4, this._pixelsPerAttendeeColumn);
         if (this._attendeeWithFocus == i && this._fieldHasFocus) {
            graphics.setColor(16711680);
            graphics.drawRect(0, rowIndex * this._pixelsPerRowHeight, this._pixelsPerAttendeeColumn, this._pixelsPerRowHeight);
         }

         rowIndex++;
      }

      int columnIndex = 0;
      rowIndex = 0;

      for (int i = this._timeIntervalStart; i <= this._timeIntervalEnd; i++) {
         graphics.setColor(13882323);
         graphics.fillRect(
            (columnIndex + 2) * this._pixelPerTimeColumn, rowIndex * this._pixelsPerRowHeight, this._pixelPerTimeColumn, this._pixelsPerRowHeight
         );
         graphics.setColor(0);
         graphics.drawRect(
            (columnIndex + 2) * this._pixelPerTimeColumn, rowIndex * this._pixelsPerRowHeight, this._pixelPerTimeColumn, this._pixelsPerRowHeight
         );
         String displayHour = "";
         if (i < 0) {
            displayHour = this._timeIntervalsPrevious[i + 24].getDisplayHour();
         } else if (i > 23) {
            displayHour = this._timeIntervalsNext[i - 24].getDisplayHour();
         } else {
            displayHour = this._timeIntervals[i].getDisplayHour();
         }

         graphics.drawText(displayHour, (columnIndex + 2) * this._pixelPerTimeColumn, rowIndex * this._pixelsPerRowHeight, 0, this._pixelPerTimeColumn);
         if (this._timeIntervalFocus == i && this._fieldHasFocus) {
            graphics.invert(
               (columnIndex + 2) * this._pixelPerTimeColumn, rowIndex * this._pixelsPerRowHeight, this._pixelPerTimeColumn, this._pixelsPerRowHeight
            );
         }

         columnIndex++;
      }

      columnIndex = 0;
      int firstAttendeeRow = ++rowIndex;

      for (int i = this._timeIntervalStart; i <= this._timeIntervalEnd; i++) {
         rowIndex = firstAttendeeRow;

         for (int j = 0; j < 1; j++) {
            graphics.setColor(14745599);
            graphics.fillRect(
               (columnIndex + 2) * this._pixelPerTimeColumn, (rowIndex + j) * this._pixelsPerRowHeight, this._pixelPerTimeColumn, this._pixelsPerRowHeight
            );
            graphics.setColor(0);
            graphics.drawRect(
               (columnIndex + 2) * this._pixelPerTimeColumn, (rowIndex + j) * this._pixelsPerRowHeight, this._pixelPerTimeColumn, this._pixelsPerRowHeight
            );
            int[] FBData = null;
            if (i < 0) {
               FBData = this._timeIntervalsPrevious[i + 24].getDataAtAttendee(j).getFBData();
            } else if (i > 23) {
               FBData = this._timeIntervalsNext[i - 24].getDataAtAttendee(j).getFBData();
            } else {
               FBData = this._timeIntervals[i].getDataAtAttendee(j).getFBData();
            }

            for (int k = 0; k < 4; k++) {
               graphics.setColor(FreeBusyConstants.getColor(FBData[k]));
               int posX = (columnIndex + 2) * this._pixelPerTimeColumn;
               int subIntervalPosX = k * this._pixelPerTimeColumn / 4;
               graphics.fillRect(posX + subIntervalPosX, rowIndex * this._pixelsPerRowHeight, this._pixelPerTimeColumn / 4, this._pixelsPerRowHeight);
            }

            if (this._timeIntervalFocus == i && this._fieldHasFocus) {
               graphics.setColor(16711680);
               graphics.drawRect(
                  (columnIndex + 2) * this._pixelPerTimeColumn, (rowIndex + j) * this._pixelsPerRowHeight, this._pixelPerTimeColumn, this._pixelsPerRowHeight
               );
               long date = 0;
               boolean hasFocus = false;
               if (i < 0) {
                  hasFocus = this._timeIntervalsPrevious[i + 24].isFocus();
                  date = this._timeIntervalsPrevious[i + 24].getDate();
               } else if (i > 23) {
                  hasFocus = this._timeIntervalsNext[i - 24].isFocus();
                  date = this._timeIntervalsNext[i - 24].getDate();
               } else {
                  hasFocus = this._timeIntervals[i].isFocus();
                  date = this._timeIntervals[i].getDate();
               }

               if (hasFocus) {
                  this._highlightedDate.setTime(date);
               }
            }
         }

         for (int j = this._attendeeStart; j <= this._attendeeEnd; j++) {
            rowIndex++;
            graphics.setColor(14745599);
            graphics.fillRect(
               (columnIndex + 2) * this._pixelPerTimeColumn, rowIndex * this._pixelsPerRowHeight, this._pixelPerTimeColumn, this._pixelsPerRowHeight
            );
            graphics.setColor(0);
            graphics.drawRect(
               (columnIndex + 2) * this._pixelPerTimeColumn, rowIndex * this._pixelsPerRowHeight, this._pixelPerTimeColumn, this._pixelsPerRowHeight
            );
            int[] FBData = null;
            if (i < 0) {
               FBData = this._timeIntervalsPrevious[i + 24].getDataAtAttendee(j + 1).getFBData();
            } else if (i > 23) {
               FBData = this._timeIntervalsNext[i - 24].getDataAtAttendee(j + 1).getFBData();
            } else {
               FBData = this._timeIntervals[i].getDataAtAttendee(j + 1).getFBData();
            }

            for (int k = 0; k < 4; k++) {
               graphics.setColor(FreeBusyConstants.getColor(FBData[k]));
               int posX = (columnIndex + 2) * this._pixelPerTimeColumn;
               int subIntervalPosX = k * this._pixelPerTimeColumn / 4;
               graphics.fillRect(posX + subIntervalPosX, rowIndex * this._pixelsPerRowHeight, this._pixelPerTimeColumn / 4, this._pixelsPerRowHeight);
            }

            if (this._timeIntervalFocus == i && this._fieldHasFocus) {
               graphics.setColor(16711680);
               graphics.drawRect(
                  (columnIndex + 2) * this._pixelPerTimeColumn, rowIndex * this._pixelsPerRowHeight, this._pixelPerTimeColumn, this._pixelsPerRowHeight
               );
            }
         }

         columnIndex++;
      }
   }

   @Override
   public void focusChangeNotify(int action) {
      if (action == 1) {
         this._fieldHasFocus = true;
      } else if (action == 3) {
         this._fieldHasFocus = false;
      }

      super.focusChangeNotify(action);
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      int amountRemaining = amount;
      if ((status & 65537) == 0) {
         if (amount > 0) {
            System.out.println("Scrolling down");
            if (this._attendeeWithFocus < this._numberOfAttendees - 1) {
               this._attendeeWithFocus++;
               if (this._attendeeWithFocus > this._attendeeEnd) {
                  this._attendeeEnd = this._attendeeWithFocus;
                  this._attendeeStart = this._attendeeEnd - this._attendeesOnScreen + 1;
               }

               amountRemaining = 0;
               this.invalidate();
            } else {
               System.out.println(((StringBuffer)(new Object())).append(amountRemaining).append(" scroll left over").toString());
            }
         } else if (amount < 0) {
            System.out.println("Scrolling up");
            if (this._attendeeWithFocus > 0) {
               this._attendeeWithFocus--;
               if (this._attendeeWithFocus < this._attendeeStart) {
                  this._attendeeStart = this._attendeeWithFocus;
                  this._attendeeEnd = this._attendeeStart + this._attendeesOnScreen - 1;
               }

               amountRemaining = 0;
               this.invalidate();
            } else {
               System.out.println(((StringBuffer)(new Object())).append(amountRemaining).append(" scroll left over").toString());
            }
         }

         return amountRemaining;
      } else {
         if (amount < 0) {
            System.out.println("Scrolling left");
            this._timeIntervals[this._timeIntervalFocus].setFocus(false);
            if (this._timeIntervalFocus == this._timeIntervalStart) {
               this._timeIntervalStart--;
            }

            this._timeIntervalFocus--;
            if (this._timeIntervalFocus < 0) {
               this._timeIntervalFocus = 23;
               if (this._timeIntervalStart < 0) {
                  this._timeIntervalStart += 24;
                  this._timeIntervalsNext = this._timeIntervals;
                  this._timeIntervals = this._timeIntervalsPrevious;
                  this._timeIntervalsPrevious = this.createNewTimeInterval(this._timeIntervalsPrevious, -1);
               }
            }

            this._timeIntervalEnd = this._timeIntervalStart + 4 - 1;
            this._timeIntervals[this._timeIntervalFocus].setFocus(true);
            this.invalidate();
         } else if (amount > 0) {
            System.out.println("Scrolling right");
            this._timeIntervals[this._timeIntervalFocus].setFocus(false);
            if (this._timeIntervalFocus == this._timeIntervalEnd) {
               this._timeIntervalEnd++;
            }

            this._timeIntervalFocus++;
            if (this._timeIntervalFocus > 23) {
               this._timeIntervalFocus = 0;
               if (this._timeIntervalEnd > 23) {
                  this._timeIntervalEnd -= 24;
                  this._timeIntervalsPrevious = this._timeIntervals;
                  this._timeIntervals = this._timeIntervalsNext;
                  this._timeIntervalsNext = this.createNewTimeInterval(this._timeIntervalsNext, 1);
               }
            }

            this._timeIntervalStart = this._timeIntervalEnd - 4 + 1;
            this._timeIntervals[this._timeIntervalFocus].setFocus(true);
            this.invalidate();
         }

         this.updateHeaderDate(this._timeIntervals[this._timeIntervalFocus].getDate());
         return 0;
      }
   }

   private TimeIntervalColumn[] createNewTimeInterval(TimeIntervalColumn[] reference, int offset) {
      this._calEx.setTimeLong(reference[0].getDate());
      this._calEx.roll(5, offset);
      long date = this._calEx.getTimeLong();
      TimeIntervalColumn[] timeInterval = new TimeIntervalColumn[24];

      for (int i = 0; i < 24; i++) {
         Calendar intervalTime = Calendar.getInstance();
         intervalTime.setTime((Date)(new Object(date)));
         intervalTime.set(11, i);
         timeInterval[i] = new TimeIntervalColumn(intervalTime.getTime().getTime(), i, this._numberOfAttendees);
      }

      return timeInterval;
   }

   private void updateHeaderDate(long date) {
      this._callback.updateHeaderDate(date);
   }
}
