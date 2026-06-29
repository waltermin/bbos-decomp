package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.TextMetrics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleContextFactory;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.internal.system.InternalServices;

final class MonthField extends Field {
   private boolean _showTitle;
   private int _cellWidth;
   private int _cellHeight;
   private int _heightOfCells;
   private int _widthOfCells;
   private int _leftOfCells;
   private int _heightOfAll;
   private int _titleHeight;
   private int _dowHeaderHeight;
   private int _daysInMonth;
   private int _daysInLastMonth;
   private int _daysInNextMonth;
   private int _currentDay;
   private int _currentMonth = -1;
   private int _DOWOfFDOM;
   private int _startingDOW;
   private byte[] _busyPatternArray;
   private byte[] _busyPatternArrayPreviousMonth;
   private byte[] _busyPatternArrayNextMonth;
   private byte[] _propertiesArray;
   private byte[] _propertiesArrayPreviousMonth;
   private byte[] _propertiesArrayNextMonth;
   private int _selectedDay;
   private char[] _sharedDateString = new char[2];
   private boolean _cachedValuesCurrent;
   private int _firstDayOffset;
   private CalendarViewController _callback;
   private String[] _dow = CommonResources.getStringArray(116);
   private String[] _longMonths = CalendarApp._rb.getStringArray(262);
   private Theme _theme;
   private Font _fontNormal;
   private Font _fontBold;
   private Font _fontTitle;
   TextMetrics _textMetrics = new TextMetrics();
   private long _lastMonthChange;
   private int _numOfAccessibleItems = 2;
   private static final Tag BASE_TAG = Tag.create("month");
   private static final Tag TITLE_TAG = Tag.create("month-title");
   private static final Tag DAYSOFWEEK_TAG = Tag.create("month-daysofweek");
   private static final Tag DATE_TAG = Tag.create("month-date");
   private static final Tag TODAY_TAG = Tag.create("month-today");
   private static final Tag WEEKDAY_TAG = Tag.create("month-weekday");
   private static final Tag WEEKEND_TAG = Tag.create("month-weekend");
   private static final Tag PREVIOUS_DAY_TAG = Tag.create("month-previous-day");
   private static final Tag NEXT_DAY_TAG = Tag.create("month-next-day");
   public static final byte EMPHASIZED_MASK = 1;
   private static final int MAX_ROWS = 7;
   private static final int DAYS_IN_WEEK = 7;
   private static final int ROUNDING = 6;
   private static final int BUSY_INDICATOR_PAD = 2;
   private static final int DATE_PAD = 2;
   private static final int _cellVerticalBorderWidth = 1;
   private static final int _cellHorizontalBorderHeight = 1;
   private static final int TITLE_OFFSET = 0;
   private static final char ZERO = '0';
   private static final int DASHED_LINE_STIPPLE = 1431655765;
   private static final int SOLID_LINE_STIPPLE = -1;

   public MonthField(CalendarViewController callback) {
      super(3476778912330022912L);
      this._callback = callback;
      this.setTag(BASE_TAG);
   }

   @Override
   protected final void applyFont() {
      super.applyFont();
      this._fontNormal = this.getFont().derive(0);
      this._fontBold = this._fontNormal.derive(1);
      int titleHeight = this._cellHeight - 2;
      if (Graphics.isColor()) {
         titleHeight -= this._fontBold.getLeading();
      }

      this._fontTitle = this._fontBold.derive(this._fontBold.getStyle(), titleHeight);
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      this._theme = ThemeManager.getActiveTheme();
      this.applyFont();
   }

   public final void setTitleDisplay(boolean showTitle) {
      this._showTitle = showTitle;
      this.invalidate();
   }

   public final void setMonthInfo(int daysInMonth, byte[] busyPatternArray, byte[] propertiesArray) {
      this._daysInMonth = daysInMonth;
      this._busyPatternArray = busyPatternArray;
      this._propertiesArray = propertiesArray;
      this.setCurrentDayInMonth(this._currentDay);
      this.setSelectedDay(this._selectedDay);
      this._cachedValuesCurrent = false;
      this.invalidate();
   }

   public final void setPreviousMonthInfo(int daysInLastMonth, byte[] busyPatternArray, byte[] propertiesArray) {
      this._daysInLastMonth = daysInLastMonth;
      this._busyPatternArrayPreviousMonth = busyPatternArray;
      this._propertiesArrayPreviousMonth = propertiesArray;
      this.invalidate();
   }

   public final void setNextMonthInfo(int daysInNextMonth, byte[] busyPatternArray, byte[] propertiesArray) {
      this._daysInNextMonth = daysInNextMonth;
      this._busyPatternArrayNextMonth = busyPatternArray;
      this._propertiesArrayNextMonth = propertiesArray;
      this.invalidate();
   }

   public final void setCurrentDayInMonth(int currentDayInMonth) {
      this._currentDay = currentDayInMonth;
      if (this._currentDay >= this._daysInMonth) {
         this._currentDay = this._daysInMonth - 1;
      }

      this.invalidate();
   }

   public final void setMonth(int month) {
      this._currentMonth = month;
      if (this._currentMonth > 11) {
         this._currentMonth = 11;
      } else if (this._currentMonth < 0) {
         this._currentMonth = 0;
      }

      this.invalidate();
   }

   public final void setDOWOfFirstDayOfMonth(int DOWOfFDOM) {
      this._DOWOfFDOM = DOWOfFDOM;
      if (this._DOWOfFDOM > 6) {
         this._DOWOfFDOM = 6;
      } else if (this._DOWOfFDOM < 0) {
         this._DOWOfFDOM = 0;
      }

      this._cachedValuesCurrent = false;
      this.invalidate();
   }

   public final void setStartingDOW(int dayOfWeek) {
      this._startingDOW = dayOfWeek;
      if (this._startingDOW > 6) {
         this._startingDOW = 6;
      } else if (this._startingDOW < 0) {
         this._startingDOW = 0;
      }

      this._cachedValuesCurrent = false;
      this.invalidate();
   }

   public final void setSelectedDay(int day) {
      this._selectedDay = day;
      if (this._selectedDay >= this._daysInMonth) {
         this._selectedDay = this._daysInMonth - 1;
      } else if (this._selectedDay < 0) {
         this._selectedDay = 0;
      }

      this.invalidate();
   }

   public final int getSelectedDay() {
      return this._selectedDay;
   }

   private final int rowFromDayOffset(int dayOffset) {
      return (dayOffset + this._firstDayOffset) / 7;
   }

   private final int colFromDayOffset(int dayOffset) {
      return (dayOffset + this._firstDayOffset) % 7;
   }

   private final void calcCache() {
      if (!this._cachedValuesCurrent) {
         this._firstDayOffset = (this._DOWOfFDOM - this._startingDOW + 7) % 7;
         this._cachedValuesCurrent = true;
      }
   }

   @Override
   protected final void onFocus(int direction) {
      if (direction > 0) {
         this._selectedDay = 0;
      } else {
         if (direction < 0) {
            this._selectedDay = this._daysInMonth - 1;
         }
      }
   }

   @Override
   public final int getAccessibleRole() {
      return 29;
   }

   @Override
   public final int getAccessibleChildCount() {
      return this._numOfAccessibleItems;
   }

   @Override
   public final AccessibleContext getAccessibleChildAt(int index) {
      int selectedDay = this.getSelectedDay();
      if (selectedDay >= this._daysInMonth) {
         selectedDay -= this._daysInMonth;
      }

      if (selectedDay < 0) {
         selectedDay += this._daysInLastMonth;
      }

      switch (index) {
         case 0:
         default:
            return new AccessibleContextFactory(
               Integer.toString(selectedDay + 1) + " " + StringUtilities.removeChars(this._longMonths[this._currentMonth], " "), 28, 4, this
            );
         case 1:
            if (this._daysInMonth > selectedDay && 0 <= selectedDay) {
               if (this.getSelectedDay() >= this._daysInMonth) {
                  if (this._busyPatternArrayNextMonth[selectedDay] != 0) {
                     return new AccessibleContextFactory("Busy", 28, 4, this);
                  }
               } else if (this.getSelectedDay() < 0) {
                  if (this._busyPatternArrayPreviousMonth[selectedDay] != 0) {
                     return new AccessibleContextFactory("Busy", 28, 4, this);
                  }
               } else if (this._busyPatternArray[selectedDay] != 0) {
                  return new AccessibleContextFactory("Busy", 28, 4, this);
               }

               return new AccessibleContextFactory("Free", 28, 4, this);
            }
         case -1:
            return new AccessibleContextFactory("", 28, 4, this);
      }
   }

   @Override
   public final void getFocusRect(XYRect rect) {
      if (this._selectedDay <= this._daysInMonth && this._selectedDay >= 0) {
         this.calcCache();
         int xStart = this.colFromDayOffset(this._selectedDay) * this._cellWidth + this._leftOfCells;
         int yStart = this.rowFromDayOffset(this._selectedDay) * this._cellHeight - 1;
         if (this._showTitle) {
            yStart += this._titleHeight + this._dowHeaderHeight + 1;
         }

         rect.set(xStart + 1, yStart + 1, this._cellWidth - 1, this._cellHeight - 1);
      }
   }

   @Override
   protected final void layout(int width, int height) {
      this._cellWidth = (width - 1 - 2) / 7;
      if (this._showTitle) {
         this._cellHeight = height / 8;
      } else {
         this._cellHeight = height / 7;
      }

      if (!Graphics.isColor()) {
         this._cellHeight &= -2;
      }

      this._widthOfCells = 7 * this._cellWidth + 1;
      this._leftOfCells = width - this._widthOfCells >> 1;
      this._heightOfCells = 7 * this._cellHeight;
      this._heightOfAll = this._heightOfCells;
      if (this._showTitle) {
         this._heightOfAll = this._heightOfAll + this._cellHeight;
      }

      this.adjustFont(this._cellHeight, this._cellWidth);
      this._dowHeaderHeight = this._fontBold.getHeight() + 1;

      for (int i = 0; i < 7; i++) {
         this._fontBold.measureText(this._dow[i], 0, 1, null, this._textMetrics);
         if (-this._textMetrics.iBoundsTlY + this._fontTitle.getDescent() > this._dowHeaderHeight) {
            this._dowHeaderHeight = -this._textMetrics.iBoundsTlY + this._fontTitle.getDescent();
         }
      }

      this._fontTitle.measureText("Å", 0, 1, null, this._textMetrics);
      this._titleHeight = Math.max(-this._textMetrics.iBoundsTlY + this._fontTitle.getDescent(), 2 * this._cellHeight - this._dowHeaderHeight - 1);
      this.setExtent(width, this._heightOfAll);
   }

   @Override
   protected final void paint(Graphics graphics) {
      XYRect clip = graphics.getClippingRect();
      ThemeAttributeSet attr = this._theme.getAttributeSet(BASE_TAG);
      int width = this.getWidth();
      int cellBorderColor = 0;
      if (Graphics.isColor() && attr != null) {
         attr.applyToGraphics(graphics);
         graphics.clear();
         cellBorderColor = attr.getColor(1);
      }

      this.calcCache();
      int cellWidth = this._cellWidth;
      int cellHeight = this._cellHeight;
      int yPos = 0;
      int xPos = this._leftOfCells + 1;
      yPos += this._titleHeight;
      if (this._showTitle && clip.y < yPos) {
         graphics.pushContext(0, 0, width, this._titleHeight, 0, 0);
         attr = this._theme.getAttributeSet(TITLE_TAG);
         if (Graphics.isColor() && attr != null) {
            attr.applyToGraphics(graphics);
            graphics.clear();
         }

         graphics.setFont(this._fontTitle);
         if (this._currentMonth != -1) {
            String text = this._longMonths[this._currentMonth];
            this._fontTitle.measureText(text, 0, text.length(), null, this._textMetrics);
            int y = yPos - (this._textMetrics.iBoundsBrY + this._textMetrics.iBoundsTlY) >> 1;
            graphics.drawText(text, xPos, y, 12, this._widthOfCells);
         }

         graphics.popContext();
      }

      if (clip.y < yPos + cellHeight) {
         attr = this._theme.getAttributeSet(DAYSOFWEEK_TAG);
         xPos = this._leftOfCells;
         int counter = this._startingDOW;
         int textColor = 0;
         graphics.pushContext(xPos, yPos, 7 * cellWidth + 1, this._dowHeaderHeight, 0, 0);
         if (Graphics.isColor() && attr != null) {
            attr.applyToGraphics(graphics);
            textColor = attr.getColor(1);
            graphics.clear();
         }

         for (int i = 0; i < 7; i++) {
            if (counter != 6 && counter != 0) {
               graphics.setFont(this._fontNormal);
            } else {
               graphics.setFont(this._fontBold);
            }

            int y = yPos + (this._dowHeaderHeight >> 1) + 2;
            graphics.setColor(textColor);
            graphics.drawText(this._dow[counter].charAt(0), xPos, y, 36, cellWidth);
            graphics.setColor(cellBorderColor);
            graphics.drawRect(xPos, yPos, cellWidth + 1, cellHeight);
            counter = ++counter % 7;
            xPos += cellWidth;
         }

         graphics.popContext();
      }

      yPos += this._dowHeaderHeight;
      char[] dateString = this._sharedDateString;
      int temp = cellWidth - 2;
      xPos = this._leftOfCells;
      yPos += this.rowFromDayOffset(0) * cellHeight;
      xPos = this._leftOfCells;
      yPos += this.rowFromDayOffset(0) * cellHeight;

      for (int i = 0; i < this.colFromDayOffset(0); i++) {
         dateString[0] = (char)(48 + (this._daysInLastMonth - this.colFromDayOffset(0) + i + 1) / 10);
         dateString[1] = (char)(48 + (this._daysInLastMonth - this.colFromDayOffset(0) + i + 1) % 10);
         attr = this._theme.getAttributeSet(PREVIOUS_DAY_TAG);
         graphics.pushContext(xPos, yPos, cellWidth, cellHeight, 0, 0);
         if (Graphics.isColor() && attr != null) {
            attr.applyToGraphics(graphics);
            graphics.clear();
         }

         if (this._propertiesArrayPreviousMonth != null && (this._propertiesArrayPreviousMonth[this._daysInLastMonth - this.colFromDayOffset(0) + i] & 1) != 0) {
            graphics.setFont(this._fontBold);
         } else {
            graphics.setFont(this._fontNormal);
         }

         if (dateString[0] == '0') {
            graphics.drawText(dateString, 1, 1, xPos, yPos + 2, 5, temp);
         } else {
            graphics.drawText(dateString, 0, 2, xPos, yPos + 2, 5, temp);
         }

         int startY = -1;
         int bit = 128;
         int factor = cellHeight > 20 ? 2 : 1;

         for (int bitPosition = 0; bitPosition < 9; bitPosition++) {
            if (this._busyPatternArrayPreviousMonth != null
               && (this._busyPatternArrayPreviousMonth[this._daysInLastMonth - this.colFromDayOffset(0) + i] & bit) == bit
               && bitPosition < 8) {
               if (startY == -1) {
                  startY = bitPosition;
               }
            } else if (startY != -1) {
               graphics.fillRect(xPos + 2 + 1, yPos + startY * factor, factor * 2, (bitPosition - startY) * factor);
               startY = -1;
            }

            bit >>= 1;
         }

         graphics.popContext();
         graphics.setColor(cellBorderColor);
         graphics.drawRect(xPos, yPos, cellWidth + 1, cellHeight + 1);
         xPos += cellWidth;
      }

      xPos = this.colFromDayOffset(0) * cellWidth + this._leftOfCells;
      yPos += this.rowFromDayOffset(0) * cellHeight;
      byte[] busy = this._busyPatternArray;
      byte[] prop = this._propertiesArray;
      int borderlessWidth = this._widthOfCells - 1;
      if (Graphics.isColor()) {
         graphics.setStipple(-1);
      } else {
         graphics.setStipple(1431655765);
      }

      int dow = this._startingDOW + this.colFromDayOffset(0);
      if (dow >= 7) {
         dow -= 7;
      }

      int max = this._daysInMonth;

      for (int i = 0; i < max; i++) {
         if (clip.intersects(xPos + 1, yPos + 1, cellWidth - 1, cellHeight - 1)) {
            if (this._currentDay == i) {
               attr = this._theme.getAttributeSet(TODAY_TAG);
               if (!Graphics.isColor()) {
                  graphics.setStipple(-1);
                  graphics.drawRect(xPos, yPos, cellWidth + 1, cellHeight + 1);
                  graphics.setStipple(1431655765);
               }
            } else if (dow != 0 && dow != 6) {
               attr = this._theme.getAttributeSet(WEEKDAY_TAG);
            } else {
               attr = this._theme.getAttributeSet(WEEKEND_TAG);
            }

            graphics.pushContext(xPos + 1, yPos + 1, cellWidth - 1, cellHeight - 1, 0, 0);
            if (Graphics.isColor()) {
               if (this._selectedDay == i) {
                  this.setThemeAttributesSpecial(attr, graphics);
               } else if (attr != null) {
                  attr.applyToGraphics(graphics);
               }

               graphics.clear();
            }

            if ((prop[i] & 1) != 0) {
               graphics.setFont(this._fontBold);
            } else {
               graphics.setFont(this._fontNormal);
            }

            dateString[0] = (char)(48 + (i + 1) / 10);
            dateString[1] = (char)(48 + (i + 1) % 10);
            if (this._currentDay != i) {
               attr = this._theme.getAttributeSet(DATE_TAG);
            }

            if (attr != null && Graphics.isColor()) {
               graphics.setColor(attr.getColor(1));
            }

            if (dateString[0] == '0') {
               graphics.drawText(dateString, 1, 1, xPos, yPos + 2, 5, temp);
            } else {
               graphics.drawText(dateString, 0, 2, xPos, yPos + 2, 5, temp);
            }

            int startY = -1;
            int bit = 128;
            int factor = cellHeight > 20 ? 2 : 1;

            for (int bitPosition = 0; bitPosition < 9; bitPosition++) {
               if (busy != null && (busy[i] & bit) == bit && bitPosition < 8) {
                  if (startY == -1) {
                     startY = bitPosition;
                  }
               } else if (startY != -1) {
                  graphics.fillRect(xPos + 2 + 1, yPos + startY * factor, factor * 2, (bitPosition - startY) * factor);
                  startY = -1;
               }

               bit >>= 1;
            }

            graphics.popContext();
            graphics.setColor(cellBorderColor);
            int padding = 1;
            if (yPos + cellHeight + padding > this.getHeight()) {
               padding = this.getHeight() - yPos - cellHeight;
            }

            graphics.drawRect(xPos, yPos, cellWidth + 1, cellHeight + padding);
         }

         if (dow == 6) {
            dow = 0;
         } else {
            dow++;
         }

         xPos += cellWidth;
         if (xPos >= borderlessWidth) {
            yPos += cellHeight;
            xPos = this._leftOfCells;
         }
      }

      for (int i = 0; i < this._daysInNextMonth; i++) {
         dateString[0] = (char)(48 + (i + 1) / 10);
         dateString[1] = (char)(48 + (i + 1) % 10);
         attr = this._theme.getAttributeSet(NEXT_DAY_TAG);
         graphics.pushContext(xPos, yPos, cellWidth + 1, cellHeight + 1, 0, 0);
         if (Graphics.isColor() && attr != null) {
            attr.applyToGraphics(graphics);
            graphics.clear();
         }

         if (this._propertiesArrayNextMonth != null && (this._propertiesArrayNextMonth[i] & 1) != 0) {
            graphics.setFont(this._fontBold);
         } else {
            graphics.setFont(this._fontNormal);
         }

         if (dateString[0] == '0') {
            graphics.drawText(dateString, 1, 1, xPos, yPos + 2, 5, temp);
         } else {
            graphics.drawText(dateString, 0, 2, xPos, yPos + 2, 5, temp);
         }

         int startY = -1;
         int bit = 128;
         int factor = cellHeight > 20 ? 2 : 1;

         for (int bitPosition = 0; bitPosition < 9; bitPosition++) {
            if (this._busyPatternArrayNextMonth != null && (this._busyPatternArrayNextMonth[i] & bit) == bit && bitPosition < 8) {
               if (startY == -1) {
                  startY = bitPosition;
               }
            } else if (startY != -1) {
               graphics.fillRect(xPos + 2 + 1, yPos + startY * factor, factor * 2, (bitPosition - startY) * factor);
               startY = -1;
            }

            bit >>= 1;
         }

         graphics.popContext();
         graphics.setColor(cellBorderColor);
         int padding = 1;
         if (yPos + cellHeight + padding > this.getHeight()) {
            padding = this.getHeight() - yPos - cellHeight;
         }

         graphics.drawRect(xPos, yPos, cellWidth + 1, cellHeight + padding);
         xPos += cellWidth;
         if (xPos >= borderlessWidth) {
            yPos += cellHeight;
            if (yPos + cellHeight - 1 > this.getHeight()) {
               return;
            }

            xPos = this._leftOfCells;
         }
      }
   }

   @Override
   public final int moveFocus(int amount, int status, int time) {
      int targetDay = this._selectedDay;
      if ((status & 131073) != 0) {
         amount *= 7;
      }

      targetDay += amount;
      if (targetDay >= 0 && targetDay < this._daysInMonth) {
         this._selectedDay = targetDay;
         amount = 0;
      } else if (InternalServices.getUptime() - this._lastMonthChange > 500) {
         if (amount < -14) {
            amount = -14;
         } else if (amount > 14) {
            amount = 14;
         }

         amount = this._callback.moveFocus(this, amount);
         this._selectedDay = targetDay;
         this._lastMonthChange = InternalServices.getUptime();
      } else {
         amount = 0;
      }

      if (Ui.isTTSEnabled()) {
         super.accessibleEventOccurred(6, new Integer(1), new Integer(2), this);
      }

      return amount;
   }

   @Override
   public final void moveFocus(int x, int y, int status, int time) {
      int dayOfWeek = x / this._cellWidth;
      int weekOfMonth = y / this._cellHeight - (this._showTitle ? 2 : 1);
      int newDayIndex = weekOfMonth * 7 + dayOfWeek - this._firstDayOffset;
      this.moveFocus(newDayIndex - this._selectedDay, status, time);
   }

   @Override
   public final boolean stylusTap(int x, int y, int status, int time) {
      return true;
   }

   private final void adjustFont(int cellHeight, int cellWidth) {
      Font normal = Font.getDefault();
      int newHeight;
      if (cellWidth > cellHeight * 15 / 10) {
         newHeight = cellHeight - 2;
      } else {
         newHeight = cellHeight * 3 / 5;
      }

      normal = normal.derive(normal.getStyle(), newHeight);
      this.setFont(normal, false);
      this.applyFont();
   }

   static {
      FontRegistry.loadFont("MonthFont_10.cbtf", "net_rim_bb_calendar_app", "MonthFont");
   }
}
