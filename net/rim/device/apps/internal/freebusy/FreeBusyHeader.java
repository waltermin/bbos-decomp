package net.rim.device.apps.internal.freebusy;

import java.util.Calendar;
import java.util.Date;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.FieldPosition;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.theme.Tag;

class FreeBusyHeader extends Manager {
   private Tag TAG = Tag.create("title");
   private long _highlightedDate;
   private long _currentTime;
   private DateNavField _navField;
   private LabelField _selectedDateField = (LabelField)(new Object(null, 4294967300L));
   private LabelField _currentTimeField = (LabelField)(new Object("time", 1152921504606847044L));
   private int _statusAreaPadding = 0;
   private int _selectedDateWidth = 0;
   private int _currentTimeWidth = 0;
   private int _headerHeight = 0;
   private Font _textFont = Font.getDefault();
   private Font _navFont = Font.getDefault();
   FreeBusyController _callback = null;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   FreeBusyHeader(long date, FreeBusyController callback) {
      super(2814749767106560L);
      this._callback = callback;
      this._highlightedDate = date;
      this._currentTime = System.currentTimeMillis();
      this._navField = new DateNavField(date);
      boolean var8 = false /* VF: Semaphore variable */;

      label31:
      try {
         var8 = true;
         if (Graphics.isColor() && Display.getWidth() > 180) {
            this._textFont = Font.getDefault().derive(0, 1000, 4194306);
            this._navFont = Font.getDefault().derive(0, 800, 4194306);
            var8 = false;
         } else {
            this._textFont = FontFamily.forName(FontFamily.FAMILY_SYSTEM).getFont(0, 1000, 4194306);
            this._navFont = FontFamily.forName(FontFamily.FAMILY_SYSTEM).getFont(0, 800, 4194306);
            var8 = false;
         }
      } finally {
         if (var8) {
            Font originalFont = this.getFont();
            FontFamily fontFamily = originalFont.getFontFamily();
            this._textFont = fontFamily.getFont(0, 1000, 4194306);
            this._navFont = fontFamily.getFont(0, 800, 4194306);
            break label31;
         }
      }

      this._selectedDateField.setFont(this._textFont);
      this._currentTimeField.setFont(this._textFont);
      this._navField.setFont(this._navFont);
      this._headerHeight = this.calcHeaderHeight();
      this.computeDates();
      this.add(this._selectedDateField);
      this.add(this._currentTimeField);
      this.add(this._navField);
      this.setTag(this.TAG);
   }

   private int calcHeaderHeight() {
      return Math.max(this._selectedDateField.getPreferredHeight(), this.getTextFontHeight() + 2) + 1;
   }

   public int getTextFontHeight() {
      return this._textFont.getHeight(0);
   }

   private void computeDates() {
      Calendar selectedTime = Calendar.getInstance();
      selectedTime.setTime((Date)(new Object(this._highlightedDate)));
      StringBuffer highlightedDateBuffer = (StringBuffer)(new Object());
      DateFormat df = DateFormat.getInstance(48);
      df.format(selectedTime, highlightedDateBuffer, (FieldPosition)(new Object(5)));
      this._selectedDateField.setText(highlightedDateBuffer.toString());
      this._selectedDateWidth = this._selectedDateField.getPreferredWidth();
      Calendar currentTime = Calendar.getInstance();
      currentTime.setTime((Date)(new Object(System.currentTimeMillis())));
      StringBuffer currentTimeBuffer = (StringBuffer)(new Object());
      df = DateFormat.getInstance(6);
      df.format(currentTime, currentTimeBuffer, (FieldPosition)(new Object(10)));
      this._currentTimeField.setText(currentTimeBuffer.toString());
      this._currentTimeWidth = this._currentTimeField.getPreferredWidth();
   }

   @Override
   protected void sublayout(int width, int height) {
      int availableWidth = width - this._statusAreaPadding;
      int fontHeight = this._textFont.getHeight();
      this.setExtent(width, Math.min(this._headerHeight, 2 * fontHeight));
      int selectedDateWidth = this._selectedDateWidth;
      int currentX = 0;
      int dateTime_Y = Math.max(this._headerHeight - this._selectedDateField.getPreferredHeight() >> 1, 2);
      this.setPositionChild(this._selectedDateField, 0, dateTime_Y);
      this.layoutChild(this._selectedDateField, selectedDateWidth, this._headerHeight);
      int navFieldWidth = this._navField != null ? this._navField.getPreferredWidth() : 0;
      int rowsInDateField = this._selectedDateField.getExtent().height / this._selectedDateField.getFont().getHeight();
      if (rowsInDateField > 1 && navFieldWidth == 0) {
         selectedDateWidth = this._selectedDateField.getPreferredWidth();
         this.layoutChild(this._selectedDateField, selectedDateWidth, this._headerHeight);
      }

      availableWidth -= selectedDateWidth;
      currentX += selectedDateWidth;
      if (navFieldWidth != 0) {
         currentX = width - this._statusAreaPadding - navFieldWidth;
         int y = Math.min(Math.max(this._headerHeight - fontHeight >> 1, 1), dateTime_Y);
         this.setPositionChild(this._navField, currentX, y);
         this.layoutChild(this._navField, navFieldWidth, this._headerHeight);
         availableWidth -= navFieldWidth;
         this.setPositionChild(this._currentTimeField, currentX - this._currentTimeWidth, dateTime_Y);
         this.layoutChild(this._currentTimeField, this._currentTimeWidth, this._headerHeight);
      } else {
         currentX = width - this._statusAreaPadding - this._currentTimeWidth;
         this.setPositionChild(this._currentTimeField, currentX, dateTime_Y);
         this.layoutChild(this._currentTimeField, this._currentTimeWidth, this._headerHeight);
      }
   }

   public void goToDate(long date) {
      this._callback.updateHighlightedDate(date);
   }

   public void updateDate(long newDate) {
      this._highlightedDate = newDate;
      this.computeDates();
      this._navField.setSelectedDate(newDate);
      this.invalidate();
   }
}
