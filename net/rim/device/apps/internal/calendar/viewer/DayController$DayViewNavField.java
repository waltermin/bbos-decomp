package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.cldc.util.CalendarExtensions;

final class DayController$DayViewNavField extends Field {
   private int _navBarHeight;
   private int _navBarCellWidth;
   private int _navBarWidth;
   private int _arrowWidth;
   private int _totalNavBarWidth;
   private int _firstDOW;
   private int _selectedDay;
   private int _focusIndex;
   private final int _numChildCount;
   private String[] _dow;
   private final DayController this$0;
   private static final int PREVIOUS_DAY_CONTROL;
   private static final int NEXT_DAY_CONTROL;

   DayController$DayViewNavField(DayController _1) {
      super(18014398509481984L);
      this.this$0 = _1;
      this._firstDOW = CalendarOptions.getOptions().getFirstDayOfWeek();
      this._selectedDay = -1;
      this._focusIndex = -1;
      this._numChildCount = 1;
      this._dow = CommonResources.getStringArray(116);
      this.computeLayout(this.getFont());
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      this.setThemeAttributesSpecial(null, null);
   }

   private final void computeLayout(Font font) {
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

   final void setFirstDOW(int firstDOW) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final void setSelectedDate(long selectedDate) {
      Calendar cal = this.this$0.getScratchCalendar();
      CalendarExtensions calEx = (CalendarExtensions)cal;
      calEx.setTimeLong(selectedDate);
      this._selectedDay = cal.get(7);
      this.invalidate();
   }

   private final void invokeFocusedControl() {
      Calendar cal = this.this$0.getScratchCalendar();
      CalendarExtensions calEx = (CalendarExtensions)cal;
      calEx.setTimeLong(this.this$0._tod.getStartOfList());
      if (this._focusIndex != -1 && this._focusIndex != 7) {
         int startOfWeekDOW = CalendarOptions.getOptions().getFirstDayOfWeek() - 1;
         int anchorDateDOW = cal.get(7) - 1;
         int deltaToRelativeDOW = (7 + anchorDateDOW - startOfWeekDOW) % 7 - this._focusIndex;
         calEx.add(5, -deltaToRelativeDOW);
      } else {
         calEx.add(5, this._focusIndex == -1 ? -1 : 1);
      }

      long dateToGoTo = calEx.getTimeLong();
      this.this$0.loadViewContents(dateToGoTo, (byte)0, null, (byte)0, true, true, (byte)0);
   }

   @Override
   public final void setFont(Font font) {
      super.setFont(font);
      this.computeLayout(font);
   }

   @Override
   public final void onUnfocus() {
      this._focusIndex = -1;
      super.onUnfocus();
   }

   @Override
   public final void getFocusRect(XYRect rect) {
      if (this._focusIndex != -1 && this._focusIndex != 7) {
         rect.x = this._arrowWidth + this._focusIndex * this._navBarCellWidth + 1;
         rect.y = 1;
         rect.height = this._navBarHeight - 1;
         rect.width = this._navBarCellWidth - 1;
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
   public final int getAccessibleRole() {
      return 29;
   }

   @Override
   public final int getAccessibleChildCount() {
      return 1;
   }

   @Override
   public final AccessibleContext getAccessibleChildAt(int index) {
      switch (index) {
         case 0:
            int currentDOW = (this._firstDOW - 1 + this._focusIndex) % 7;
            String currentCharacter = this._dow[currentDOW];
            return (AccessibleContext)(new Object(currentCharacter, 28, 4, this));
         default:
            return (AccessibleContext)(new Object("", 28, 4, this));
      }
   }

   @Override
   public final int moveFocus(int amount, int status, int time) {
      super.moveFocus(amount, status, time);
      if ((status & 1) != 0) {
         this.this$0.moveToDifferentDay(amount);
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

      if (Ui.isTTSEnabled()) {
         super.accessibleEventOccurred(6, new Object(1), new Object(2), this);
      }

      return unusedScroll;
   }

   @Override
   protected final void moveFocus(int x, int y, int status, int time) {
      x -= this._arrowWidth;
      if (x < 0) {
         this._focusIndex = -1;
      } else if (x >= this._navBarCellWidth * 7) {
         this._focusIndex = 7;
      } else {
         this._focusIndex = x / this._navBarCellWidth;
      }

      if (Ui.isTTSEnabled()) {
         super.accessibleEventOccurred(6, new Object(1), new Object(2), this);
      }
   }

   @Override
   protected final void onFocus(int direction) {
      if (direction > 0) {
         this._focusIndex = -1;
      } else {
         if (direction < 0) {
            this._focusIndex = 7;
         }
      }
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      this.invokeFocusedControl();
      return true;
   }

   @Override
   protected final boolean stylusTap(int x, int y, int status, int time) {
      this.invokeFocusedControl();
      return true;
   }

   @Override
   protected final boolean stylusDoubleTap(int x, int y, int status, int time) {
      this.invokeFocusedControl();
      return true;
   }

   @Override
   protected final boolean keyChar(char character, int status, int time) {
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
   protected final void layout(int width, int height) {
      if (this._firstDOW != this.this$0._calendarOptions.getFirstDayOfWeek()) {
         this.setFirstDOW(this.this$0._calendarOptions.getFirstDayOfWeek());
      }

      this.setExtent(width, height);
   }

   @Override
   public final int getPreferredWidth() {
      return this._totalNavBarWidth;
   }

   @Override
   public final int getPreferredHeight() {
      return this._navBarHeight + 2;
   }

   @Override
   protected final void paint(Graphics g) {
      if (g.isDrawingStyleSet(8)) {
         this.paintFocusText(g);
      } else {
         int x = 0;
         int availableWidth = this.getWidth();
         Font font = this.getFont();
         int midPoint = this._navBarHeight >> 1;
         int arrowBottom = midPoint << 1;
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

         g.drawLine(x, 0, x + this._navBarWidth, 0);
         g.drawLine(x, this._navBarHeight, x + this._navBarWidth, this._navBarHeight);
         int currentDOW = this._firstDOW - 1;
         currentDOW %= 7;

         for (int i = 0; i <= 7; i++) {
            g.drawLine(x, 1, x, this._navBarHeight);
            int original = g.getColor();
            ThemeAttributeSet attr = null;
            if (selectedCell == i && this._selectedDay != -1) {
               attr = ThemeManager.getActiveTheme().getAttributeSet(DayController.DAY_INDICATOR_SELECTED_TAG);
            } else {
               attr = ThemeManager.getActiveTheme().getAttributeSet(DayController.DAY_INDICATOR_TAG);
            }

            if (i < 7) {
               g.pushContext(x + 1, 1, this._navBarCellWidth - 1, this._navBarHeight - 1, 0, 0);
               if (attr != null) {
                  attr.applyToGraphics(g);
                  Font attrFont = attr.getFont();
                  if (attrFont != null) {
                     g.setFont(attrFont.derive(font.getStyle(), font.getHeight()));
                  }

                  g.clear();
               }

               char currentCharacter = this._dow[currentDOW].charAt(0);
               int charWidth = font.getBounds(currentCharacter);
               int offset = (this._navBarCellWidth >> 1) - (charWidth >> 1) + (Graphics.isColor() ? 0 : 1);
               g.drawText(currentCharacter, x + offset, 1, 0, availableWidth);
               g.popContext();
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

   protected final void paintFocusText(Graphics g) {
      int x = 0;
      if (this._focusIndex != -1 && this._focusIndex != 7) {
         if (0 <= this._focusIndex && this._focusIndex <= 6) {
            Font font = this.getFont();
            int currentDOW = (this._firstDOW - 1 + this._focusIndex) % 7;
            x = this._arrowWidth + this._navBarCellWidth * this._focusIndex;
            char currentCharacter = this._dow[currentDOW].charAt(0);
            int charWidth = font.getBounds(currentCharacter);
            int offset = (this._navBarCellWidth >> 1) - (charWidth >> 1) + (Graphics.isColor() ? 0 : 1);
            g.drawText(currentCharacter, x + offset, 1, 0, this.getWidth());
         }
      } else {
         int midPoint = this._navBarHeight >> 1;
         int arrowBottom = midPoint << 1;
         if (arrowBottom > this._navBarHeight) {
            arrowBottom -= 2;
         }

         if (this._focusIndex == -1) {
            g.drawLine(x, midPoint, x + this._arrowWidth, 0);
            g.drawLine(x, midPoint, x + this._arrowWidth, arrowBottom);
         } else {
            x = this._arrowWidth + this._navBarCellWidth * 7;
            g.drawLine(x + this._arrowWidth, midPoint, x, 0);
            g.drawLine(x + this._arrowWidth, midPoint, x, arrowBottom);
         }
      }
   }
}
