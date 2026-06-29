package net.rim.device.apps.api.ribbon;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;

public class RibbonBanner {
   protected static final long GUID = -6860171651452498676L;
   public static final int STANDARD = 1;
   public static final int SUMMARY = 2;
   public static final int SUMMARY_DEFAULT = 3;
   public static final int SIGNAL_AND_BATTERY = 4;
   public static final int TIME_AND_DATE = 5;
   public static final int TIME_ONLY = 6;
   public static final int DATE_ONLY = 7;
   public static final int SIGNAL_AND_INDICATORS = 8;
   public static final int GPS_MODE = 9;
   public static final int STANDARD_TWO_LINE = 10;
   public static final int SUMMARY_DEFAULT_TWO_LINE = 11;

   public static RibbonBanner getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (RibbonBanner)ar.waitForStartup(-6860171651452498676L);
   }

   public Field getRibbonBanner() {
      throw null;
   }

   public Field getStatusBanner(String _1, int _2) {
      throw null;
   }

   public void unregisterBanner(Field _1) {
      throw null;
   }

   public void setStatusBannerTitle(Field _1, String _2) {
      throw null;
   }

   public String getStatusBannerTitle(Field _1) {
      throw null;
   }
}
