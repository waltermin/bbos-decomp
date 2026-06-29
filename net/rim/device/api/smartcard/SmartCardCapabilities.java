package net.rim.device.api.smartcard;

public class SmartCardCapabilities {
   private int _protocols;
   private int _clockRateFactor;
   private int _baudRateFactor;
   public static final int PROTOCOL_T0;
   public static final int PROTOCOL_T1;
   public static final int PROTOCOL_T2;
   public static final int PROTOCOL_T3;
   public static final int PROTOCOL_T4;
   public static final int PROTOCOL_T5;
   public static final int PROTOCOL_T6;
   public static final int PROTOCOL_T7;
   public static final int PROTOCOL_T8;
   public static final int PROTOCOL_T9;
   public static final int PROTOCOL_T10;
   public static final int PROTOCOL_T11;
   public static final int PROTOCOL_T12;
   public static final int PROTOCOL_T13;
   public static final int PROTOCOL_T14;
   public static final int PROTOCOL_T15;
   public static final int FI_0;
   public static final int FI_1;
   public static final int FI_2;
   public static final int FI_3;
   public static final int FI_4;
   public static final int FI_5;
   public static final int FI_6;
   public static final int FI_7;
   public static final int FI_8;
   public static final int FI_9;
   public static final int FI_10;
   public static final int FI_11;
   public static final int FI_12;
   public static final int FI_13;
   public static final int FI_14;
   public static final int FI_15;
   public static final int DI_0;
   public static final int DI_1;
   public static final int DI_2;
   public static final int DI_3;
   public static final int DI_4;
   public static final int DI_5;
   public static final int DI_6;
   public static final int DI_7;
   public static final int DI_8;
   public static final int DI_9;
   public static final int DI_10;
   public static final int DI_11;
   public static final int DI_12;
   public static final int DI_13;
   public static final int DI_14;
   public static final int DI_15;

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
