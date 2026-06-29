package net.rim.device.api.bugreport;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ControlledAccess;
import net.rim.vm.TraceBack;

public class BugReportManager {
   public static final long GUID;
   private static BugReportable _appReportable;
   private static String _reportLocation;
   private static boolean _screenshotAllowed = true;

   protected BugReportManager() {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(1));
   }

   public static BugReport createReport() {
      BugReportManager brm = (BugReportManager)ApplicationRegistry.getApplicationRegistry().get(-483760535199085048L);
      return brm != null ? brm.createReportInternal() : null;
   }

   public BugReport createReportInternal() {
      throw null;
   }

   public static BugReportable getApplicationReportable() {
      return _appReportable;
   }

   public static String getReportLocation() {
      return _reportLocation;
   }

   public static boolean isScreenshotAllowed() {
      return _screenshotAllowed;
   }

   public static void setApplicationReportable(BugReportable reportable) {
      _appReportable = reportable;
   }

   public static void setReportLocation(String reportLocation) {
      _reportLocation = reportLocation;
   }

   public static void setScreenshotAllowed(boolean screenshotAllowed) {
      _screenshotAllowed = screenshotAllowed;
   }
}
