package net.rim.wica.runtime.messaging.internal.util;

import net.rim.wica.runtime.logging.Logger;
import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.internal.outbound.OutboundQueue;

public final class InternalLogger {
   public static final void logOutboundQueueFull(long wicletId) {
   }

   public static final void logOutgoingMessage(Message m, OutboundQueue q) {
   }

   public static final void logOutgoingMessageProcessed(Message m, OutboundQueue q, int pendingMessageCount) {
   }

   public static final void logWicletMessage(Message m, int position) {
   }

   public static final void logWicletMessageBundle(Message[] m, int position) {
   }

   public static final void logIncomingQueueFull(long wicletId) {
   }

   public static final void logWicletMessageRemoved(Message m, int numPending) {
   }

   public static final void logSystemMessage(Message m) {
   }

   public static final void logSystemMessageProcessed(Message m, int numPending) {
   }

   public static final void logServiceMessage(Message m) {
   }

   public static final void logServiceMessageProcessed(Message m) {
   }

   public static final void logUnkownEvent(Object o, int event) {
      Logger.log(o.toString(), "Unknown event", 2, event);
   }

   public static final void logBadEvent(Object o, int event) {
      Logger.log(o.toString(), "Unknown or null event parameter", 2, event);
   }

   public static final void logWarning(Object o, String warning, Throwable t, Object param) {
      log(o, warning, t, param, 3);
   }

   public static final void logError(Object o, String error, Throwable t, Object param) {
      log(o, error, t, param, 2);
   }

   private static final void log(Object o, String message, Throwable t, Object param, int logType) {
      StringBuffer s = (StringBuffer)(new Object());
      if (message != null) {
         s.append(message).append(", ");
      }

      if (t != null) {
         s.append("Exception: ").append(t.toString()).append(", ");
      }

      if (param != null) {
         label36:
         try {
            s.append(param.toString());
         } finally {
            break label36;
         }
      }

      Logger.log(o == null ? "Messaging" : o.toString(), s.toString(), logType);
   }

   public static final void logResponse(Object o, String url, int responseCode) {
   }
}
