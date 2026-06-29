package net.rim.device.internal.system;

public final class LcdPowerSaving {
   private int _partialDisplaySupported;
   private int _topLineFixed = 1;
   private int _verticalResolution;
   private int _horizontalResolution;
   private int _outOfAreaColourDepth;
   private int _reserved;
   private int _partialColourSupported;
   private int _partialColourDepth;
   private int _reserved1;
   private int _reserved2;
   private int _reserved3;
   public static final int PARTIAL_DISPLAY_MODE = 0;
   public static final int PARTIAL_COLOUR_MODE = 1;
   public static final int SLEEP_MODE = 2;
   private static LcdPowerSaving _singleton = new LcdPowerSaving();

   private LcdPowerSaving() {
      getPowerSavingFeatures(this);
   }

   public static final boolean partialDisplaySupported() {
      return _singleton._partialDisplaySupported != 0;
   }

   public static final boolean topLineFixed() {
      return _singleton._topLineFixed != 0;
   }

   public static final int getVerticalResolution() {
      return _singleton._verticalResolution;
   }

   public static final int getHorizontalResolution() {
      return _singleton._horizontalResolution;
   }

   public static final int getOutOfAreaColourDepth() {
      return _singleton._outOfAreaColourDepth;
   }

   public static final boolean partialColourSupported() {
      return _singleton._partialColourSupported != 0;
   }

   public static final int getPartialColourDepth() {
      return _singleton._partialColourDepth;
   }

   private static final native void getPowerSavingFeatures(LcdPowerSaving var0);

   public static final native boolean setupPartialDisplayMode(int var0, int var1, int var2, int var3, int var4);

   public static final native boolean setPowerSavingMode(int var0, boolean var1);
}
