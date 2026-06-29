package net.rim.device.internal.system;

import net.rim.device.api.system.ApplicationRegistry;

public class CoverageInfoInternal {
   public static final int COVERAGE_NONE = 0;
   public static final int COVERAGE_CARRIER = 1;
   public static final int COVERAGE_MDS = 2;
   public static final int COVERAGE_BIS_B = 4;
   public static final int COVERAGE_STATUS_CHANGED = 0;
   protected static final long ID = -809192429028495755L;

   public static CoverageInfoInternal getInstance() {
      CoverageInfoInternal info = (CoverageInfoInternal)ApplicationRegistry.getApplicationRegistry().waitFor(-809192429028495755L);
      info.ensureInitialized();
      return info;
   }

   protected void ensureInitialized() {
      throw null;
   }

   public int getCoverage(int _1, boolean _2) {
      throw null;
   }

   public boolean isCoverageSufficient(int _1, int _2, boolean _3) {
      throw null;
   }
}
