package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.resources.Resource;

public final class CallTunes {
   private short[][][] _tunes = new short[9][][];
   private byte[][][] _polyTunes = new byte[9][][];
   static final long GUID = -7841402171854956200L;
   private static CallTunes _instance;
   public static final int TUNE_CALL_ENDED = 0;
   public static final int TUNE_CALL_FAILED = 1;
   public static final int TUNE_CALL_WAITING = 2;
   public static final int TUNE_MUTE_ON = 3;
   public static final int TUNE_MUTE_OFF = 4;
   public static final int TUNE_PRIVACY_GAINED = 5;
   public static final int TUNE_PRIVACY_LOST = 6;
   public static final int TUNE_EMERGENCY_CALL = 7;
   public static final int TUNE_USSD_ALERT = 8;
   private static final int TUNE_COUNT = 9;

   private CallTunes() {
      short[] endTune = new short[]{600, 300, 14, -12280};
      short[] callFailedTune = new short[]{
         950,
         333,
         1400,
         333,
         1800,
         333,
         0,
         1000,
         950,
         333,
         1400,
         333,
         1800,
         333,
         0,
         1000,
         950,
         333,
         1400,
         333,
         1800,
         333,
         2,
         -12280,
         440,
         250,
         6,
         -12280,
         1500,
         300,
         0,
         400,
         1500,
         300,
         4,
         -12280,
         1500,
         150,
         1800,
         150,
         256,
         28486,
         26380,
         -24032
      };
      short[] callWaitingTune = new short[]{440, 250, 6, -12280};
      short[] muteOnTune = new short[]{1500, 150, 1800, 150, 256, 28486, 26380, -24032};
      short[] muteOffTune = new short[]{1800, 150, 1500, 150, 2, -12278, 10, 0};
      short[] emergencyTune = new short[]{
         1400, 125, 0, 125, 1400, 125, 0, 125, 1400, 125, 0, 125, 1400, 125, 4, -12278, 128, 0, 64, 0, 32, 0, 16, 0, 4, -12280, 659, 100
      };
      short[] privacyTune = new short[]{1500, 300, 0, 400, 1500, 300, 4, -12280, 1500, 150, 1800, 150};
      short[] ussdTune = new short[]{659, 100, 880, 125, 5, -12278, 154, 0};
      this.set(0, endTune);
      this.set(1, callFailedTune);
      this.set(2, callWaitingTune);
      this.set(3, muteOnTune);
      this.set(4, muteOffTune);
      this.set(5, privacyTune);
      this.set(6, privacyTune);
      this.set(7, emergencyTune);
      this.set(8, ussdTune);
      Resource resources = Resource.getResourceClass();
      this.setPoly(8, resources.getResource("tunes/TuneUSSDAlert.mid"));
      this.setPoly(7, resources.getResource("tunes/TuneEmergencyCall.mid"));
   }

   private static final CallTunes getInstance() {
      if (_instance == null) {
         ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
         synchronized (reg) {
            _instance = (CallTunes)reg.get(-7841402171854956200L);
            if (_instance == null) {
               _instance = new CallTunes();
               reg.put(-7841402171854956200L, _instance);
            }
         }
      }

      return _instance;
   }

   public static final short[] getTune(int type) {
      return getInstance().get(type);
   }

   public static final byte[] getPolyTune(int type) {
      return getInstance().getPoly(type);
   }

   private final void set(int type, short[] tone) {
      this._tunes[type] = (short[][])tone;
   }

   private final void setPoly(int type, byte[] tone) {
      this._polyTunes[type] = (byte[][])tone;
   }

   private final short[] get(int type) {
      return (short[])this._tunes[type];
   }

   private final byte[] getPoly(int type) {
      return (byte[])this._polyTunes[type];
   }
}
