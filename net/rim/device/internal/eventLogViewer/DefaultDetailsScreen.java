package net.rim.device.internal.eventLogViewer;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.NumberUtilities;
import net.rim.vm.EventLog;

final class DefaultDetailsScreen extends BaseDetailsScreen {
   public DefaultDetailsScreen(EventLoggerContents contents) {
      super(contents);
   }

   @Override
   public final String getEventSummary(int eventHandle) {
      StringBuffer strBuf = new StringBuffer(64);
      byte[] data = EventLog.getData(eventHandle);
      if (data.length == 4) {
         int value = EventLogger.getInt(data);
         NumberUtilities.appendNumber(strBuf, value, 10);
         strBuf.append(" - 0x");
         NumberUtilities.appendNumber(strBuf, value, 16);
      } else {
         for (int aIndex = 0; aIndex != data.length; aIndex++) {
            int num = data[aIndex] & 255;
            NumberUtilities.appendNumber(strBuf, num, 16, 2);
            strBuf.append(' ');
         }
      }

      return strBuf.toString();
   }

   @Override
   public final void displayEventDetails(int eventHandle) {
      this.setTitle(super._contents._rb.getString(51));
      super.displayEventDetails(eventHandle);
   }

   @Override
   protected final String getDataFieldString(int eventHandle) {
      StringBuffer strBuf = new StringBuffer(16);
      byte[] data = EventLog.getData(eventHandle);
      if (data.length == 4) {
         int value = EventLogger.getInt(data);
         strBuf.append('0');
         strBuf.append('x');
         NumberUtilities.appendNumber(strBuf, value, 16);
         strBuf.append(' ');
         strBuf.append('<');
         NumberUtilities.appendNumber(strBuf, value, 10);
         strBuf.append('>');
      } else {
         for (int i = 0; i < data.length; i++) {
            int num = data[i] & 255;
            NumberUtilities.appendNumber(strBuf, num, 16, 2);
            strBuf.append(' ');
         }

         strBuf.setLength(strBuf.length() - 1);
      }

      return strBuf.toString();
   }
}
