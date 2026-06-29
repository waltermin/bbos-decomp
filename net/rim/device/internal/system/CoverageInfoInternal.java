package net.rim.device.internal.system;

import net.rim.device.api.system.ApplicationRegistry;

public class CoverageInfoInternal {
   public static final int COVERAGE_NONE;
   public static final int COVERAGE_CARRIER;
   public static final int COVERAGE_MDS;
   public static final int COVERAGE_BIS_B;
   public static final int COVERAGE_STATUS_CHANGED;
   protected static final long ID;

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
