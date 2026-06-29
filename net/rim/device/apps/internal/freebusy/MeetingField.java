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
   private TextRect _labelText = new TextRect(this);
   private TextRect _timeText = null;
   private TextRect _dateText = null;

   MeetingField(String labelText, long startDate, long endDate) {
      this._startDate = startDate;
      this._endDate = endDate;
      this._labelText.setText(labelText);
      int textStyle = this.getFieldStyle();
      StringBuffer startTimeText = new StringBuffer();
      StringBuffer dateText = new StringBuffer();
      this._timeText = new TextRect(this, startTimeText, textStyle);
      this._dateText = new TextRect(this, dateText, textStyle);
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
      startTime.setTime(new Date(this._startDate));
      Calendar endTime = Calendar.getInstance();
      endTime.setTime(new Date(this._endDate));
      StringBuffer dateBuffer = new StringBuffer();
      dateBuffer.append("DOW ");
      DateFormat df = DateFormat.getInstance(48);
      df.format(startTime, dateBuffer, new FieldPosition(7));
      this._dateText = new TextRect(this, dateBuffer, this.getFieldStyle());
      df = DateFormat.getInstance(7);
      StringBuffer timeBuffer = new StringBuffer();
      df.format(startTime, timeBuffer, new FieldPosition(6));
      timeBuffer.append(" - ");
      df.format(endTime, timeBuffer, new FieldPosition(6));
      this._timeText = new TextRect(this, timeBuffer, this.getFieldStyle());
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
