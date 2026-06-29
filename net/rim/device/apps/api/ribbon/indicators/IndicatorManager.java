package net.rim.device.apps.api.ribbon.indicators;

import net.rim.device.api.system.ApplicationRegistry;

public class IndicatorManager {
   private static IndicatorManager _indicatorManager;
   protected static long GUID = 1134955130671297159L;

   public static IndicatorManager getInstance() {
      if (_indicatorManager == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _indicatorManager = (IndicatorManager)ar.waitForStartup(GUID);
      }

      return _indicatorManager;
   }

   public void addIndicator(Indicator _1) {
      throw null;
   }

   public void removeIndicator(Indicator _1) {
      throw null;
   }

   public void resetIndicatorAreas() {
      throw null;
   }

   public void updateIndicators() {
      throw null;
   }

   public void suspendIndicatorUpdates() {
      throw null;
   }

   public void resumeIndicatorUpdates() {
      throw null;
   }

   public boolean isIndicatorUpdatingSuspended() {
      throw null;
   }
}
