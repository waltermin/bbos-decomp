package net.rim.device.apps.internal.freebusy;

import java.util.Calendar;
import java.util.Date;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.FieldPosition;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.text.TextRect;

class MeetingField extends Field {
   private long _startDate;
   private long _endDate;
   private TextRect _labelText = (TextRect)(new Object(this));
   private TextRect _timeText = null;
   private TextRect _dateText = null;

   MeetingField(String labelText, long startDate, long endDate) {
      this._startDate = startDate;
      this._endDate = endDate;
      this._labelText.setText(labelText);
      int textStyle = this.getFieldStyle();
      StringBuffer startTimeText = (StringBuffer)(new Object());
      StringBuffer dateText = (StringBuffer)(new Object());
      this._timeText = (TextRect)(new Object(this, startTimeText, textStyle));
      this._dateText = (TextRect)(new Object(this, dateText, textStyle));
      this.calculateTextOutput();
   }

   public boolean isFocuasable() {
      return false;
   }

   @Override
   public void layout(int width, int height) {
      this.setExtent(width, Math.min(height, Font.getDefault().getHeight()));
   }

   private void calculateTextOutput() {
      Calendar startTime = Calendar.getInstance();
      startTime.setTime((Date)(new Object(this._startDate)));
      Calendar endTime = Calendar.getInstance();
      endTime.setTime((Date)(new Object(this._endDate)));
      StringBuffer dateBuffer = (StringBuffer)(new Object());
      dateBuffer.append("DOW ");
      DateFormat df = DateFormat.getInstance(48);
      df.format(startTime, dateBuffer, (FieldPosition)(new Object(7)));
      this._dateText = (TextRect)(new Object(this, dateBuffer, this.getFieldStyle()));
      df = DateFormat.getInstance(7);
      StringBuffer timeBuffer = (StringBuffer)(new Object());
      df.format(startTime, timeBuffer, (FieldPosition)(new Object(6)));
      timeBuffer.append(" - ");
      df.format(endTime, timeBuffer, (FieldPosition)(new Object(6)));
      this._timeText = (TextRect)(new Object(this, timeBuffer, this.getFieldStyle()));
      this._labelText.layout(Font.getDefault().getAdvance(this._labelText.getTextString()), Font.getDefault().getHeight());
      this._dateText.layout(Font.getDefault().getAdvance(this._dateText.getTextString()), Font.getDefault().getHeight());
      this._timeText.layout(Font.getDefault().getAdvance(this._timeText.getTextString()), Font.getDefault().getHeight());
      this._dateText.setPosition(this._labelText.getWidth() + 1, 0);
      this._timeText.setPosition(this._dateText.getWidth() + this._labelText.getWidth() + 4, 0);
   }

   @Override
   public void paint(Graphics graphics) {
      this._labelText.paintSelf(graphics);
      this._dateText.paintSelf(graphics);
      this._timeText.paintSelf(graphics);
      if (this._startDate == this._endDate) {
         System.out.println("zero duration");
      }
   }

   public void setStartDate(long startDate) {
      this.setStartDate(startDate, Integer.MIN_VALUE);
   }

   private void setStartDate(long startDate, int context) {
      this._startDate = startDate;
      this.calculateTextOutput();
      this.fieldChangeNotify(context);
   }

   public void setEndDate(long endDate) {
      this.setEndDate(endDate, Integer.MIN_VALUE);
   }

   private void setEndDate(long endDate, int context) {
      this._endDate = endDate;
      this.calculateTextOutput();
      this.fieldChangeNotify(context);
   }
}
