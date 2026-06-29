package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.cldc.util.CalendarExtensions;

final class InPlaceEventDialog$StartAndDurationField extends Field {
   private Calendar _cal = Calendar.getInstance();
   private CalendarExtensions _calEx = (CalendarExtensions)this._cal;
   private long _start;
   private long _duration;
   private StringBuffer _startString = new StringBuffer();
   private StringBuffer _endString = new StringBuffer();
   private int _maxTimeWidth;
   private int _widthPerBar;
   private int _heightPerLine;
   private DateFormat _tf = DateFormat.getInstance(7);
   private static final char NO_EVENT_BAR_CHAR = '\uf460';
   private static final char BEGIN_EVENT_BAR_CHAR = '┌';
   private static final char MIDDLE_EVENT_BAR_CHAR = '│';
   private static final char END_EVENT_BAR_CHAR = '└';
   private static final int MIN_HEIGHT_PER_LINE = 8;

   public final void setStart(long start) {
      this._start = start;
      this.timeOrDurationUpdated();
   }

   public final long getStart() {
      return this._start;
   }

   public final void setDuration(long dur) {
      this._duration = dur;
      this.timeOrDurationUpdated();
   }

   public final long getDuration() {
      return this._duration;
   }

   private final void timeOrDurationUpdated() {
      this._calEx.setTimeLong(this._start);
      this._startString.setLength(0);
      this._tf.format(this._cal, this._startString, null);
      this._calEx.setTimeLong(this._start + this._duration);
      this._endString.setLength(0);
      this._tf.format(this._cal, this._endString, null);
      this.invalidate();
   }

   @Override
   public final int getPreferredWidth() {
      if (this._maxTimeWidth <= 0) {
         Calendar cal = this._cal;
         DateTimeUtilities.zeroCalendarTime(cal);
         String timeStr = TimeStringCache.getString(cal);
         Font font = this.getFont();
         this._widthPerBar = font.getBounds('\uf460');
         this._maxTimeWidth = font.getBounds(timeStr) + 1;
      }

      return this._widthPerBar + this._maxTimeWidth + 3;
   }

   @Override
   protected final void layout(int width, int height) {
      this._heightPerLine = this.getFont().getHeight();
      if (this._heightPerLine == 0) {
         this._heightPerLine = 8;
      }

      this.setExtent(this.getPreferredWidth(), height / this._heightPerLine * this._heightPerLine);
   }

   @Override
   protected final void paint(Graphics graphics) {
      if (this._heightPerLine == 0) {
         this._heightPerLine = 8;
      }

      int numVerticalLines = this.getHeight() / this._heightPerLine - 2;
      int x = 0;
      int y = 0;
      graphics.drawText('┌', x, y, 0, this._widthPerBar);
      x = this._widthPerBar;
      graphics.drawText(this._startString, 0, this._startString.length(), x, y, 5, this._maxTimeWidth);
      int var7 = 0;
      y += this._heightPerLine;

      for (int i = 0; i < numVerticalLines; i++) {
         graphics.drawText('│', var7, y, 0, this._widthPerBar);
         y += this._heightPerLine;
      }

      graphics.drawText('└', var7, y, 0, this._widthPerBar);
      var7 = this._widthPerBar;
      graphics.drawText(this._endString, 0, this._endString.length(), var7, y, 5, this._maxTimeWidth);
   }
}
