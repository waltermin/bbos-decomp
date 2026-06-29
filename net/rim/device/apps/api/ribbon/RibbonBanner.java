package net.rim.device.apps.api.ribbon;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;

public class RibbonBanner {
   protected static final long GUID;
   public static final int STANDARD;
   public static final int SUMMARY;
   public static final int SUMMARY_DEFAULT;
   public static final int SIGNAL_AND_BATTERY;
   public static final int TIME_AND_DATE;
   public static final int TIME_ONLY;
   public static final int DATE_ONLY;
   public static final int SIGNAL_AND_INDICATORS;
   public static final int GPS_MODE;
   public static final int STANDARD_TWO_LINE;
   public static final int SUMMARY_DEFAULT_TWO_LINE;

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
