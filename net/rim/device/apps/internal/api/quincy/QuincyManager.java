package net.rim.device.apps.internal.api.quincy;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.RIMGlobalMessagePoster;

public class QuincyManager {
   protected static final long GUID = 3652488048280724215L;
   public static final long EXTERNAL_APP_REPORT_REQUEST = -7981312083560453157L;
   public static final long LOGWORTHY_REPORT_REQUEST = 2888237357036234703L;
   public static final long RADIO_LOGWORTHY_REPORT_REQUEST = -2816799803471967993L;

   private static void postQuincyEvent(long guid, int d0, int d1, Object o0, Object o1) {
      RIMGlobalMessagePoster.postGlobalEvent(guid, d0, d1, o0, o1);
   }

   public static void sendUncaughtException(String subject) {
      postQuincyEvent(-7981312083560453157L, 0, 0, subject, null);
   }

   public static void sendJavaLogworthy(String subject) {
      postQuincyEvent(2888237357036234703L, 0, 0, subject, null);
   }

   public static void sendRadioLogworthy(String subject, Object appMsg) {
      postQuincyEvent(-2816799803471967993L, 0, 0, subject, appMsg);
   }

   public static QuincyManager getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (QuincyManager)ar.get(3652488048280724215L);
   }

   public boolean sendReport(Report report) {
      return this.sendReport(report, true, false);
   }

   public boolean sendReport(Report _1, boolean _2, boolean _3) {
      throw null;
   }

   public Report getReport(long _1, int _3) {
      throw null;
   }
}
