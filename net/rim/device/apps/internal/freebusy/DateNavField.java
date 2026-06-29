package net.rim.device.apps.internal.freebusy;

import java.util.Calendar;
import java.util.Date;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.cldc.util.CalendarExtensions;

class DateNavField extends Field {
   CalendarOptions _calendarOptions = CalendarOptions.getOptions();
   private int _navBarHeight;
   private int _navBarCellWidth;
   private int _navBarWidth;
   private int _arrowWidth;
   private int _totalNavBarWidth;
   private int _firstDOW = CalendarOptions.getOptions().getFirstDayOfWeek();
   private int _selectedDay = -1;
   private long _selectedTime = 0;
   private int _focusIndex = -1;
   private String[] _dow = CommonResources.getStringArray(116);
   private int _dayIndicatorSelectedColor;
   private int _dayIndicatorSelectedColorBackground;
   private Calendar _scratchCalendar;
   private static final Tag DAY_INDICATOR_SELECTED_TAG = Tag.create("day-indicator-selected");
   private static final int PREVIOUS_DAY_CONTROL = -1;
   private static final int NEXT_DAY_CONTROL = 7;

   DateNavField(long selectedTime) {
      super(18014398509481984L);
      this._selectedTime = selectedTime;
      this.computeLayout(this.getFont());
      this.applyTheme();
      this.setSelectedDate(selectedTime);
   }

   @Override
   protected void applyTheme() {
      super.applyTheme();
      Theme theme = ThemeManager.getActiveTheme();
      this.setThemeAttributesSpecial(theme.getAttributeSet(DAY_INDICATOR_SELECTED_TAG), null);
      this._dayIndicatorSelectedColor = ThemeAttributeSet.getColor(this, 1);
      this._dayIndicatorSelectedColorBackground = ThemeAttributeSet.getColor(this, 0);
      this.setThemeAttributesSpecial(null, null);
   }

   private void computeLayout(Font font) {
      this._navBarHeight = font.getHeight(0);
      if (this._navBarHeight % 2 == 0) {
         this._navBarHeight++;
      }

      int maxWidth = 0;

      for (int i = 0; i < 7; i++) {
         char currentCharacter = this._dow[i].charAt(0);
         int charWidth = font.getBounds(currentCharacter);
         if (charWidth > maxWidth) {
            maxWidth = charWidth;
         }
      }

      this._navBarCellWidth = maxWidth + 2;
      this._arrowWidth = this._navBarHeight / 2;
      this._navBarWidth = 7 * this._navBarCellWidth;
      this._totalNavBarWidth = this._navBarWidth + 2 * (this._arrowWidth + 1);
   }

   void setFirstDOW(int firstDOW) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   void setSelectedDate(long selectedDate) {
      Calendar cal = this.getScratchCalendar();
      CalendarExtensions calEx = (CalendarExtensions)cal;
      calEx.setTimeLong(selectedDate);
      this._selectedDay = cal.get(7);
      this._selectedTime = selectedDate;
      this.invalidate();
   }

   private Calendar getScratchCalendar() {
      if (this._scratchCalendar == null) {
         this._scratchCalendar = Calendar.getInstance();
         this._scratchCalendar.setTime((Date)(new Object(this._selectedTime)));
      }

      return this._scratchCalendar;
   }

   private void invokeFocusedControl() {
      Calendar cal = this.getScratchCalendar();
      CalendarExtensions calEx = (CalendarExtensions)cal;
      if (this._focusIndex != -1 && this._focusIndex != 7) {
         int startOfWeekDOW = CalendarOptions.getOptions().getFirstDayOfWeek() - 1;
         int anchorDateDOW = cal.get(7) - 1;
         int deltaToRelativeDOW = (7 + anchorDateDOW - startOfWeekDOW) % 7 - this._focusIndex;
         calEx.add(5, -deltaToRelativeDOW);
      } else {
         calEx.add(5, this._focusIndex == -1 ? -1 : 1);
      }

      long dateToGoTo = calEx.getTimeLong();
      System.out.println(((StringBuffer)(new Object("Date to go to: "))).append(dateToGoTo).toString());
      this.goToDate(dateToGoTo);
   }

   private void goToDate(long date) {
      Manager manager = this.getManager();
      if (manager instanceof FreeBusyHeader) {
         FreeBusyHeader header = (FreeBusyHeader)manager;
         header.goToDate(date);
      }
   }

   @Override
   public void setFont(Font font) {
      super.setFont(font);
      this.computeLayout(font);
   }

   @Override
   public void getFocusRect(XYRect rect) {
      if (this._focusIndex != -1 && this._focusIndex != 7) {
         rect.x = this._arrowWidth + this._focusIndex * this._navBarCellWidth + 1;
         rect.y = 1;
         rect.height = this._navBarHeight - 1;
         rect.width = this._navBarCellWidth - 1;
         int selectedIndex = (this._selectedDay - this._firstDOW + 7) % 7;
         if (this._focusIndex == selectedIndex) {
            rect.y++;
            rect.height -= 2;
         }
      } else {
         rect.x = 0;
         rect.y = 1;
         rect.height = this._navBarHeight - 1;
         rect.width = this._arrowWidth;
         if (this._focusIndex == 7) {
            rect.x = this._arrowWidth + this._navBarWidth + 1;
            return;
         }
      }
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      super.moveFocus(amount, status, time);
      if ((status & 1) != 0) {
         return 0;
      }

      int unusedScroll = 0;
      this._focusIndex += amount;
      if (this._focusIndex > 7) {
         unusedScroll = this._focusIndex - 7;
         this._focusIndex = 7;
      } else if (this._focusIndex < -1) {
         unusedScroll = this._focusIndex - -1;
         this._focusIndex = -1;
      }

      return unusedScroll;
   }

   @Override
   protected void moveFocus(int x, int y, int status, int time) {
      x -= this._arrowWidth;
      if (x < 0) {
         this._focusIndex = -1;
      } else if (x >= this._navBarCellWidth * 7) {
         this._focusIndex = 7;
      } else {
         this._focusIndex = x / this._navBarCellWidth;
      }
   }

   @Override
   protected void onFocus(int direction) {
      if (direction > 0) {
         this._focusIndex = -1;
      } else {
         if (direction < 0) {
            this._focusIndex = 7;
         }
      }
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      this.invokeFocusedControl();
      return true;
   }

   @Override
   protected boolean stylusTap(int x, int y, int status, int time) {
      this.invokeFocusedControl();
      return true;
   }

   @Override
   protected boolean stylusDoubleTap(int x, int y, int status, int time) {
      this.invokeFocusedControl();
      return true;
   }

   @Override
   protected boolean keyChar(char character, int status, int time) {
      if (character == 27) {
         return false;
      }

      if (character != ' ' && character != '\n') {
         return false;
      }

      this.invokeFocusedControl();
      return true;
   }

   @Override
   protected void layout(int width, int height) {
      if (this._firstDOW != this._calendarOptions.getFirstDayOfWeek()) {
         this.setFirstDOW(this._calendarOptions.getFirstDayOfWeek());
      }

      this.setExtent(width, height);
   }

   @Override
   public int getPreferredWidth() {
      return this._totalNavBarWidth;
   }

   @Override
   public int getPreferredHeight() {
      return this._navBarHeight + 2;
   }

   @Override
   protected void paint(Graphics g) {
      int x = 0;
      int availableWidth = Display.getWidth();
      Font font = Font.getDefault();
      int midPoint = this._navBarHeight / 2;
      int arrowBottom = midPoint * 2;
      if (arrowBottom > this._navBarHeight) {
         arrowBottom -= 2;
      }

      g.drawLine(x, midPoint, x + this._arrowWidth, 0);
      g.drawLine(x, midPoint, x + this._arrowWidth, arrowBottom);
      x += this._arrowWidth;
      int selectedDayX = -1;
      int selectedCell = (this._selectedDay - this._firstDOW + 7) % 7;
      if (this._selectedDay >= 1) {
         selectedDayX = x + selectedCell * this._navBarCellWidth;
      }

      if (selectedDayX >= 0 && Graphics.isColor()) {
         int original = g.getColor();
         int originalAlpha = g.getGlobalAlpha();
         g.setColor(this._dayIndicatorSelectedColorBackground);
         g.setGlobalAlpha(128);
         g.rop(-95, selectedDayX + 1, 1, this._navBarCellWidth - 1, this._navBarHeight - 1, null, 0, 0);
         g.setGlobalAlpha(originalAlpha);
         g.setColor(original);
      }

      g.drawLine(x, 0, x + this._navBarWidth, 0);
      g.drawLine(x, this._navBarHeight, x + this._navBarWidth, this._navBarHeight);
      int currentDOW = this._firstDOW - 1;
      currentDOW %= 7;

      for (int i = 0; i <= 7; i++) {
         g.drawLine(x, 1, x, this._navBarHeight);
         if (i < 7) {
            char currentCharacter = this._dow[currentDOW].charAt(0);
            int charWidth = font.getBounds(currentCharacter);
            int offset = (this._navBarCellWidth >> 1) - (charWidth >> 1) + (Graphics.isColor() ? 0 : 1);
            int original = g.getColor();
            if (this._selectedDay != -1 && selectedCell == i) {
               g.setColor(this._dayIndicatorSelectedColor);
            }

            g.drawText(currentCharacter, x + offset, 1, 0, availableWidth);
            g.setColor(original);
            currentDOW = (currentDOW + 1) % 7;
         }

         x += this._navBarCellWidth;
      }

      x -= this._navBarCellWidth;
      g.drawLine(x + this._arrowWidth, midPoint, x, 0);
      g.drawLine(x + this._arrowWidth, midPoint, x, arrowBottom);
      if (selectedDayX >= 0 && !Graphics.isColor()) {
         g.invert(selectedDayX + 1, 1, this._navBarCellWidth - 1, this._navBarHeight - 1);
      }
   }
}
