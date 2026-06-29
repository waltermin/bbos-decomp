package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;

public final class Timing {
   private static boolean _timingInfo;
   private static boolean _dataRendered;
   private static boolean _stopTiming;
   private static boolean _dataRetrieved;
   private static long[] _startTime = new long[7];
   private static long[] _endTime = new long[7];
   private static final String[] action = new String[]{
      "Requesting dentries", "Drawing map", "Generating labels", "Drawing labels", "GPS Tracking", "Driver Assist", "Drawing overlays"
   };
   public static final int TIME_TO_REQUEST_PATHS = 0;
   public static final int TIME_TO_DRAW_PATHS = 1;
   public static final int TIME_TO_GENERATE_LABELS = 2;
   public static final int TIME_TO_DRAW_LABELS = 3;
   public static final int TIME_TO_DO_GPS_TRACKING = 4;
   public static final int TIME_TO_DO_DRIVER_ASSIST = 5;
   public static final int TIME_TO_DRAW_OVERLAYS = 6;

   public final void toggleTimer() {
      _timingInfo = !_timingInfo;
   }

   public final void timingStart() {
      _stopTiming = false;
      _dataRetrieved = false;
      _dataRendered = false;

      for (int i = 0; i < 7; i++) {
         _startTime[i] = 0;
         _endTime[i] = 0;
      }
   }

   public final void dataRendered() {
      _dataRendered = true;
   }

   public final void dataRetrieved() {
      _dataRetrieved = true;
   }

   public final boolean isRenderingDone() {
      return _dataRendered;
   }

   public final void stopTiming() {
      _stopTiming = true;
   }

   public final void startTimer(int index) {
      if (_timingInfo && !_stopTiming) {
         _startTime[index] = System.currentTimeMillis();
      }
   }

   public final void endTimer(int index) {
      if (_timingInfo && !_stopTiming) {
         _endTime[index] = System.currentTimeMillis();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void displayTimingInfo(Graphics graphics) {
      if (_timingInfo && _dataRetrieved && _dataRendered) {
         String typeface = "BBCapitals";
         int fontHeight = 11;
         Font newFont = null;

         label146:
         try {
            newFont = FontFamily.forName(typeface).getFont(0, fontHeight, 0, 2, 0);
         } catch (Throwable var20) {
            e.printStackTrace();
            break label146;
         }

         graphics.setFont(newFont);
         int colour = graphics.getColor();
         int alpha = graphics.getGlobalAlpha();
         int numLines = 7;
         int height = 15 * (numLines + 2);
         int y = (graphics.getClippingRect().height - height) / 2;
         graphics.setColor(0);
         graphics.setGlobalAlpha(64);
         graphics.fillRoundRect(49, y + 4, 230, height, 8, 8);
         graphics.setColor(16777215);
         graphics.setGlobalAlpha(192);
         graphics.fillRoundRect(45, y, 230, height, 8, 8);
         y += 2;
         graphics.setGlobalAlpha(255);
         graphics.setColor(0);
         long firstStartTime = 0;
         long lastEndTime = 0;
         int sum = 0;

         for (int i = 0; i < _startTime.length; i++) {
            if (_startTime[i] != 0) {
               firstStartTime = _startTime[i];
               break;
            }
         }

         for (int i = _startTime.length - 1; i >= 0; i--) {
            if (_endTime[i] != 0) {
               lastEndTime = _endTime[i];
               break;
            }
         }

         int totalTime = (int)(lastEndTime - firstStartTime);
         if (_startTime[1] < _endTime[0]) {
            _startTime[1] = _endTime[0];
         }

         if (_endTime[1] < _endTime[0]) {
            _endTime[1] = _endTime[0];
         }

         for (int i = 0; i < _startTime.length; i++) {
            int time;
            if (_endTime[i] != 0 && _startTime[i] != 0) {
               time = (int)(_endTime[i] - _startTime[i]);
            } else {
               time = 0;
            }

            if (time < 0) {
               time = 0;
            }

            sum += time;
            int percentage;
            if (totalTime > 0) {
               percentage = 100 * time / totalTime;
            } else {
               percentage = 0;
            }

            graphics.drawText(((StringBuffer)(new Object())).append(action[i]).append(":").toString(), 50, y);
            graphics.drawText(((StringBuffer)(new Object(""))).append(time).append(" ms").toString(), 175, y);
            graphics.drawText(((StringBuffer)(new Object(" ("))).append(percentage).append("%)").toString(), 230, y);
            y += 15;
         }

         int other = totalTime - sum;
         if (other < 0) {
            other = 0;
         }

         int percentage;
         if (totalTime > 0) {
            percentage = 100 * other / totalTime;
         } else {
            percentage = 0;
         }

         graphics.drawText("Other:", 50, y);
         graphics.drawText(((StringBuffer)(new Object(""))).append(other).append(" ms").toString(), 175, y);
         graphics.drawText(((StringBuffer)(new Object(" ("))).append(percentage).append("%)").toString(), 230, y);
         y += 15;
         graphics.setColor(16711680);
         graphics.drawText("Total time:", 50, y);
         graphics.drawText(((StringBuffer)(new Object(""))).append(totalTime).append(" ms").toString(), 175, y);
         graphics.drawText(" (100%)", 230, y);
         graphics.setColor(0);
         graphics.setColor(colour);
         graphics.setGlobalAlpha(alpha);
      }
   }
}
