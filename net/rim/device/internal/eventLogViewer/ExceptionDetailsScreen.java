package net.rim.device.internal.eventLogViewer;

import net.rim.vm.EventLog;
import net.rim.vm.TraceBack;

final class ExceptionDetailsScreen extends BaseDetailsScreen {
   public ExceptionDetailsScreen(EventLoggerContents contents) {
      super(contents);
   }

   @Override
   protected final String getNameFieldString(int eventHandle) {
      Object backtrace = EventLog.getData(eventHandle);
      return TraceBack.getMessage(backtrace, 0);
   }

   @Override
   public final String getEventSummary(int eventHandle) {
      StringBuffer strBuf = (StringBuffer)(new Object(64));
      strBuf.append(TraceBack.getMessage(EventLog.getData(eventHandle), 0));
      return strBuf.toString();
   }

   @Override
   public final void displayEventDetails(int eventHandle) {
      this.setTitle(super._contents._rb.getString(50));
      super.displayEventDetails(eventHandle);
   }

   @Override
   protected final String getDataFieldString(int eventHandle) {
      Object backTrace = EventLog.getData(eventHandle);
      StringBuffer strBuf = (StringBuffer)(new Object(128));
      int j = 1;

      while (true) {
         String msg = TraceBack.getMessage(backTrace, j);
         if (msg == null) {
            strBuf.setLength(strBuf.length() - 1);
            return strBuf.toString();
         }

         strBuf.append(msg);
         strBuf.append('\n');
         j++;
      }
   }
}
