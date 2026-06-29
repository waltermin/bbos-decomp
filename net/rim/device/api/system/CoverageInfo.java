package net.rim.device.api.system;

import net.rim.device.internal.system.CoverageInfoInternal;
import net.rim.device.internal.system.EventDispatchManager;

public class CoverageInfo {
   public static final int COVERAGE_NONE = 0;
   public static final int COVERAGE_CARRIER = 1;
   public static final int COVERAGE_MDS = 2;
   public static final int COVERAGE_BIS_B = 4;

   private CoverageInfo() {
   }

   public static void addListener(CoverageStatusListener listener) {
      addListener(Application.getApplication(), listener);
   }

   public static void addListener(Application app, CoverageStatusListener listener) {
      CoverageInfoInternal.getInstance();
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         if (dispatchManager.getDispatcher(9) == null) {
            dispatchManager.setDispatcher(9, new CoverageInfo$CoverageInfoEventDispatcher(null));
         }
      }

      app.addListener(9, listener);
   }

   public static void removeListener(CoverageStatusListener listener) {
      removeListener(Application.getApplication(), listener);
   }

   public static void removeListener(Application app, CoverageStatusListener listener) {
      app.removeListener(9, listener);
   }

   public static int getCoverageStatus() {
      return getCoverageStatus(RadioInfo.getSupportedWAFs(), true);
   }

   public static int getCoverageStatus(int wafs, boolean considerSerialBypass) {
      return CoverageInfoInternal.getInstance().getCoverage(wafs, considerSerialBypass);
   }

   public static boolean isCoverageSufficient(int coverageType) {
      return isCoverageSufficient(coverageType, RadioInfo.getSupportedWAFs(), true);
   }

   public static boolean isCoverageSufficient(int coverageType, int wafs, boolean considerSerialBypass) {
      return CoverageInfoInternal.getInstance().isCoverageSufficient(coverageType, wafs, considerSerialBypass);
   }

   public static boolean isOutOfCoverage() {
      return isOutOfCoverage(RadioInfo.getSupportedWAFs(), true);
   }

   public static boolean isOutOfCoverage(int wafs, boolean considerSerialBypass) {
      return getCoverageStatus(wafs, considerSerialBypass) == 0;
   }
}
