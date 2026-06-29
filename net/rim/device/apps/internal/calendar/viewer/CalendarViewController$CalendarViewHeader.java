package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.Arrays;
import net.rim.device.internal.i18n.InternalCalendarUtilities;

final class CalendarViewController$CalendarViewHeader extends Manager {
   private Tag TAG;
   private int _headerHeight;
   private int _currentTimeWidth;
   private int _selectedDateWidth;
   private int _statusAreaPadding;
   private boolean _showWeekNumber;
   private DateFormat _currentTimeFormat;
   private DateFormat _selectedDateFormat;
   private StringBuffer _currentTimeBuf;
   private CalendarViewController$NavDateField _selectedDate;
   private LabelField _currentTime;
   private LabelField _currentWeekNumber;
   private int _weekNum;
   private Field _navField;
   private Font _textFont;
   private Font _navFont;
   private CalendarViewController$CalendarViewHeader$UpdateDateHeader _updateDateRunnable;
   private boolean _delayedRenderingEnabled;
   boolean _initialLayoutComplete;
   private int _width;
   private int _height;
   private final CalendarViewController this$0;
   private static final int HEADER_UPDATE_THRESHOLD = 100;
   private static final int MAXIMUM_NAV_FONT_SIZE = 8;
   private static final int MAXIMUM_TEXT_FONT_SIZE = 10;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   CalendarViewController$CalendarViewHeader(CalendarViewController _1, boolean showWeekNumber) {
      super(0);
      this.this$0 = _1;
      this.TAG = Tag.create("title");
      this._statusAreaPadding = Graphics.isColor() ? 0 : 7;
      this._currentTimeFormat = DateFormat.getInstance(7);
      this._currentTimeBuf = (StringBuffer)(new Object());
      this._currentTime = (LabelField)(new Object(null, 1152921504606847044L));
      this._currentWeekNumber = null;
      this._weekNum = -1;
      this._updateDateRunnable = new CalendarViewController$CalendarViewHeader$UpdateDateHeader(this, this);
      this._initialLayoutComplete = false;
      this._showWeekNumber = showWeekNumber;
      this._selectedDateFormat = DateFormat.getInstance(48);
      this._selectedDate = new CalendarViewController$NavDateField(_1, this._selectedDateFormat);
      this._currentWeekNumber = (LabelField)(new Object(null, 4294967300L));
      boolean var7 = false /* VF: Semaphore variable */;

      label53:
      try {
         var7 = true;
         if (Graphics.isColor() && Display.getWidth() > 180) {
            this._textFont = Font.getDefault();
            this._navFont = Font.getDefault();
            if (this._textFont.getHeight(2) > 10) {
               this._textFont = Font.getDefault().derive(0, 10, 2);
            }

            if (this._navFont.getHeight(2) > 8) {
               this._navFont = Font.getDefault().derive(0, 8, 2);
               var7 = false;
            } else {
               var7 = false;
            }
         } else {
            this._textFont = FontFamily.forName(FontFamily.FAMILY_SYSTEM).getFont(0, 1000, 4194306);
            this._navFont = FontFamily.forName(FontFamily.FAMILY_SYSTEM).getFont(0, 800, 4194306);
            var7 = false;
         }
      } finally {
         if (var7) {
            Font originalFont = this.getFont();
            FontFamily fontFamily = originalFont.getFontFamily();
            this._textFont = fontFamily.getFont(0, 1000, 4194306);
            this._navFont = fontFamily.getFont(0, 800, 4194306);
            break label53;
         }
      }

      this._selectedDate.setFont(this._textFont);
      this._currentTime.setFont(this._textFont);
      this._currentWeekNumber.setFont(this._textFont);
      this._headerHeight = this.calcHeaderHeight();
      this.add(this._selectedDate);
      if (this._showWeekNumber) {
         this.add(this._currentWeekNumber);
      }

      this.add(this._currentTime);
      this.setNavField(new CalendarViewController$SimpleNavField(_1));
      this.setTag(this.TAG);
   }

   public final void setDelayedRendering(boolean val) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      int result = 0;
      if (!Trackball.isSupported() || (status & 131072) <= 0) {
         result = super.moveFocus(amount, status, time);
      } else if (amount > 0) {
         result = amount;
      }

      return result;
   }

   final int getWeekNumberFieldPreferredWidth() {
      int result = 0;
      if (this._showWeekNumber) {
         int weeknum = 100;
         String oldValue = this._currentWeekNumber.getText();
         String value = MessageFormat.format(CalendarApp._rb.getString(626), new Object[]{Integer.toString(weeknum)});
         this._currentWeekNumber.setText(value);
         result = this._currentWeekNumber.getPreferredWidth();
         this._currentWeekNumber.setText(oldValue);
      }

      return result;
   }

   @Override
   protected final void applyFont() {
      super.applyFont();
      this._initialLayoutComplete = false;
      int width = Display.getWidth();
      int headerHeight = this.calcHeaderHeight();
      if (headerHeight > this._headerHeight) {
         this.invalidate();
         this._headerHeight = headerHeight;
      } else {
         int availableWidth = width - this._statusAreaPadding;
         int lastFontHeight = -1;
         char widestNumber = 0;
         int widestValue = 0;

         for (char i = '0'; i < ':'; i++) {
            this.this$0._tempBuffer.setLength(0);
            this.this$0._tempBuffer.append(i);
            int charwidth = this._textFont.getAdvance(this.this$0._tempBuffer, 0, this.this$0._tempBuffer.length());
            if (charwidth > widestValue) {
               widestNumber = i;
               widestValue = charwidth;
            }
         }

         StringBuffer timeStringBuffer = (StringBuffer)(new Object());
         timeStringBuffer.setLength(0);
         timeStringBuffer.append(widestNumber);
         timeStringBuffer.append(widestNumber);
         timeStringBuffer.append(':');
         timeStringBuffer.append(widestNumber);
         timeStringBuffer.append(widestNumber);
         if (Graphics.isColor() && !InternalCalendarUtilities.is24Hour()) {
            int amWidth = 0;
            this.this$0._tempBuffer.setLength(0);
            this.this$0._calEx.setTimeLong(0);
            this._currentTimeFormat.format(this.this$0._calEx, this.this$0._tempBuffer, null);
            char amChar = this.this$0._tempBuffer.charAt(this.this$0._tempBuffer.length() - 1);
            this.this$0._tempBuffer.setLength(0);
            this.this$0._tempBuffer.append(amChar);
            amWidth = this._textFont.getAdvance(this.this$0._tempBuffer, 0, this.this$0._tempBuffer.length());
            this.this$0._tempBuffer.setLength(0);
            this.this$0._calEx.setTimeLong(43200000);
            this._currentTimeFormat.format(this.this$0._calEx, this.this$0._tempBuffer, null);
            char pmChar = this.this$0._tempBuffer.charAt(this.this$0._tempBuffer.length() - 1);
            this.this$0._tempBuffer.setLength(0);
            this.this$0._tempBuffer.append(pmChar);
            int pmWidth = this._textFont.getAdvance(this.this$0._tempBuffer, 0, this.this$0._tempBuffer.length());
            if (amWidth < pmWidth) {
               timeStringBuffer.append(pmChar);
            } else {
               timeStringBuffer.append(amChar);
            }
         }

         StringBuffer dateBuffer = (StringBuffer)(new Object());
         dateBuffer.setLength(0);
         dateBuffer.append("WWW ");
         dateBuffer.append(widestNumber);
         dateBuffer.append(widestNumber);
         dateBuffer.append(',');
         dateBuffer.append(' ');
         dateBuffer.append(widestNumber);
         dateBuffer.append(widestNumber);
         dateBuffer.append(widestNumber);
         dateBuffer.append(widestNumber);
         Font newFont = this._textFont;
         Font oldFont = this._textFont;

         while (true) {
            int spaceLeft = availableWidth;
            newFont.getMetrics(this.this$0._fontMetrics);
            this._currentTimeWidth = newFont.getAdvance(timeStringBuffer, 0, timeStringBuffer.length()) + 2;
            spaceLeft -= this._currentTimeWidth;
            this._selectedDateWidth = newFont.getAdvance(dateBuffer, 0, dateBuffer.length());
            spaceLeft -= this._selectedDateWidth;
            spaceLeft -= this.getWeekNumberFieldPreferredWidth();
            if (this._navField != null) {
               spaceLeft -= this._navField.getPreferredWidth();
            }

            if (spaceLeft < 0) {
               newFont = this.reduceFontSize(newFont);
               int navFontHeight = Math.min(newFont.getHeight(2), 8);
               Font f = newFont.derive(0, navFontHeight, 2);
               this._navField.setFont(f);
            }

            if (newFont == null) {
               break;
            }

            oldFont = newFont;
            if (lastFontHeight == newFont.getHeight()) {
               break;
            }

            lastFontHeight = newFont.getHeight();
         }

         if (oldFont != this._textFont) {
            this._textFont = oldFont;
            this._currentTime.setFont(this._textFont);
            this._selectedDate.setFont(this._textFont);
            this._currentWeekNumber.setFont(this._textFont);
            int navFontHeight = Math.min(this._textFont.getHeight(2), 8);
            Font f = this._textFont.derive(0, navFontHeight, 2);
            this._navField.setFont(f);
         }

         this._selectedDateWidth++;
         this.invalidate();
      }
   }

   final void setCurrentTime(Calendar currentTime) {
      int oldTimeWidth = this._currentTime.getPreferredWidth();
      this._currentTimeBuf.setLength(0);
      this._currentTimeFormat.format(currentTime, this._currentTimeBuf, null);
      if (!Graphics.isColor() && !InternalCalendarUtilities.is24Hour()) {
         this._currentTimeBuf.setLength(this._currentTimeBuf.length() - 1);
      }

      this._currentTime.setText(this._currentTimeBuf);
      if (this._currentTime.getPreferredWidth() > oldTimeWidth) {
         this.sublayout(this._width, this._height);
         this.invalidate();
      }
   }

   final synchronized void setSelectedDate(long selectedDate) {
      Application app = this.this$0.getApplication();
      if (app != null) {
         this._updateDateRunnable.setSelectedDate(selectedDate);
         if (this._delayedRenderingEnabled) {
            if (!this._updateDateRunnable.getPending()) {
               this._updateDateRunnable.setPending(true);
               if (app.invokeLater(this._updateDateRunnable, 100, false) == -1) {
                  this._updateDateRunnable.setPending(false);
                  this._updateDateRunnable.run();
                  return;
               }
            }
         } else {
            this._updateDateRunnable.run();
         }
      }
   }

   final void setNavField(Field navField) {
      if (this._navField != null) {
         this.delete(this._navField);
      }

      this._navField = navField;
      this._navField.setFont(this._navFont);
      this.add(this._navField);
      this._selectedDate.setFocusable(this._navField.isFocusable());
   }

   @Override
   public final int getPreferredWidth() {
      int preferredWidth = this._selectedDate.getPreferredWidth() + this._currentTime.getPreferredWidth();
      if (this._navField != null) {
         preferredWidth += this._navField.getPreferredWidth();
      }

      if (this._showWeekNumber) {
         preferredWidth += this.getWeekNumberFieldPreferredWidth();
      }

      return preferredWidth;
   }

   @Override
   public final int getPreferredHeight() {
      return this._headerHeight;
   }

   private final int calcHeaderHeight() {
      return Math.max(this._selectedDate.getPreferredHeight(), this.getTextFontHeight() + 2) + 1;
   }

   public final int getTextFontHeight() {
      return this._textFont.getHeight(0);
   }

   @Override
   protected final boolean incrementalLayout(int index, int added, int deleted) {
      if (this._initialLayoutComplete && index < this.getFieldCount() && added == 1 && deleted == 1) {
         this.sublayout(this._width, this._height);
         this.invalidate();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected final void sublayout(int width, int height) {
      this._width = width;
      this._height = height;
      int availableWidth = width - this._statusAreaPadding;
      int requiredWidth = availableWidth;

      for (int whileCount = 0; requiredWidth >= availableWidth && whileCount < 10; whileCount++) {
         int var17 = 1;
         var17 += this._selectedDate.getPreferredWidth();
         requiredWidth = var17 + this._currentTime.getPreferredWidth();
         if (this._navField != null) {
            requiredWidth += this._navField.getPreferredWidth();
         }

         if (this._showWeekNumber) {
            requiredWidth += this._currentWeekNumber.getPreferredWidth();
         }

         if (requiredWidth >= availableWidth) {
            this.reduceFieldFontSizes();
         }
      }

      this.setExtent(width, this._headerHeight);
      int fontHeight = this._textFont.getHeight();
      int selectedDateWidth = Math.max(this._selectedDate.getPreferredWidth(), this._selectedDateWidth);
      int currentX = 0;
      int dateTime_Y = Math.max(this._headerHeight - this._selectedDate.getPreferredHeight() >> 1, 2);
      this.setPositionChild(this._selectedDate, 0, dateTime_Y);
      this.layoutChild(this._selectedDate, selectedDateWidth, this._headerHeight);
      availableWidth -= selectedDateWidth;
      int navFieldWidth = this._navField != null ? this._navField.getPreferredWidth() : 0;
      if (navFieldWidth == 0) {
         currentX = width - this._statusAreaPadding - this._currentTime.getPreferredWidth();
         if (this._showWeekNumber) {
            int weekXPos = Math.min(
               currentX - this._currentWeekNumber.getPreferredWidth(), this.getWidth() / 2 - this._currentWeekNumber.getPreferredWidth() / 2
            );
            weekXPos = Math.max(weekXPos, selectedDateWidth);
            this.setPositionChild(this._currentWeekNumber, weekXPos, (this._headerHeight + 1) / 2 - fontHeight / 2);
            this.layoutChild(this._currentWeekNumber, this._currentWeekNumber.getPreferredWidth(), this._headerHeight);
            availableWidth -= this._currentWeekNumber.getPreferredWidth();
         }

         this.setPositionChild(this._currentTime, currentX, dateTime_Y);
         this.layoutChild(this._currentTime, this._currentTime.getPreferredWidth(), this._headerHeight);
         availableWidth -= this._currentTimeWidth;
      } else {
         currentX = width - this._statusAreaPadding - navFieldWidth;
         int y = Math.min(Math.max(this._headerHeight - fontHeight >> 1, 1), dateTime_Y);
         this.setPositionChild(this._navField, currentX, y);
         this.layoutChild(this._navField, navFieldWidth, this._headerHeight);
         availableWidth -= navFieldWidth;
         int currentTimeXPos = Math.min(currentX - this._currentTime.getPreferredWidth(), this.getWidth() / 2 - this._currentTime.getPreferredWidth() / 2);
         currentTimeXPos = Math.max(currentTimeXPos, selectedDateWidth);
         this.setPositionChild(this._currentTime, currentTimeXPos, dateTime_Y);
         this.layoutChild(this._currentTime, this._currentTime.getPreferredWidth(), this._headerHeight);
         availableWidth -= this._currentTime.getPreferredWidth();
      }

      this._initialLayoutComplete = true;
   }

   private final void reduceFieldFontSizes() {
      if (this._navField != null) {
         this._navField.setFont(this.reduceFontSize(this._navField.getFont()), false);
      }

      this._selectedDate.setFont(this.reduceFontSize(this._selectedDate.getFont()), false);
      this._currentTime.setFont(this.reduceFontSize(this._currentTime.getFont()), false);
      this._currentWeekNumber.setFont(this.reduceFontSize(this._currentWeekNumber.getFont()), false);
   }

   protected final Font reduceFontSize(Font font) {
      Font newFont = null;
      int fontHeight = font.getHeight();
      FontFamily fontFamily = font.getFontFamily();
      int[] availableHeights = fontFamily.getHeights();
      int index = Arrays.binarySearch(availableHeights, fontHeight, 0, availableHeights.length);
      if (index < 0) {
         index = -(index + 1);
      }

      while (index > 0 && availableHeights[index] >= fontHeight) {
         index--;
      }

      return index < 0 ? font : fontFamily.getFont(font.getStyle(), availableHeights[index]);
   }
}
