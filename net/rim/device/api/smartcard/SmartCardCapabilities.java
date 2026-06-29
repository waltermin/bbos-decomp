package net.rim.device.api.smartcard;

public class SmartCardCapabilities {
   private int _protocols;
   private int _clockRateFactor;
   private int _baudRateFactor;
   public static final int PROTOCOL_T0 = 1;
   public static final int PROTOCOL_T1 = 2;
   public static final int PROTOCOL_T2 = 4;
   public static final int PROTOCOL_T3 = 8;
   public static final int PROTOCOL_T4 = 16;
   public static final int PROTOCOL_T5 = 32;
   public static final int PROTOCOL_T6 = 64;
   public static final int PROTOCOL_T7 = 128;
   public static final int PROTOCOL_T8 = 256;
   public static final int PROTOCOL_T9 = 512;
   public static final int PROTOCOL_T10 = 1024;
   public static final int PROTOCOL_T11 = 2048;
   public static final int PROTOCOL_T12 = 4096;
   public static final int PROTOCOL_T13 = 8192;
   public static final int PROTOCOL_T14 = 16384;
   public static final int PROTOCOL_T15 = 32768;
   public static final int FI_0 = 1;
   public static final int FI_1 = 2;
   public static final int FI_2 = 4;
   public static final int FI_3 = 8;
   public static final int FI_4 = 16;
   public static final int FI_5 = 32;
   public static final int FI_6 = 64;
   public static final int FI_7 = 128;
   public static final int FI_8 = 256;
   public static final int FI_9 = 512;
   public static final int FI_10 = 1024;
   public static final int FI_11 = 2048;
   public static final int FI_12 = 256;
   public static final int FI_13 = 512;
   public static final int FI_14 = 1024;
   public static final int FI_15 = 2048;
   public static final int DI_0 = 1;
   public static final int DI_1 = 2;
   public static final int DI_2 = 4;
   public static final int DI_3 = 8;
   public static final int DI_4 = 16;
   public static final int DI_5 = 32;
   public static final int DI_6 = 64;
   public static final int DI_7 = 128;
   public static final int DI_8 = 256;
   public static final int DI_9 = 512;
   public static final int DI_10 = 1024;
   public static final int DI_11 = 2048;
   public static final int DI_12 = 256;
   public static final int DI_13 = 512;
   public static final int DI_14 = 1024;
   public static final int DI_15 = 2048;

   public SmartCardCapabilities(int protocolsSupported) {
      this._protocols = protocolsSupported;
      this._clockRateFactor = 3;
      this._baudRateFactor = 2;
   }

   public SmartCardCapabilities(int protocolsSupported, int clockRateFactor, int baudRateFactor) {
      this._protocols = protocolsSupported;
      this._clockRateFactor = clockRateFactor;
      this._baudRateFactor = baudRateFactor;
   }

   public int getSupportedProtocols() {
      return this._protocols;
   }

   public int getSupportedBaudRateAdjustmentFactors() {
      return this._baudRateFactor;
   }

   public int getSupportedClockRateConversionFactors() {
      return this._clockRateFactor;
   }
}
