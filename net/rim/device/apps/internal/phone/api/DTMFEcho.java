package net.rim.device.apps.internal.phone.api;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.ui.Keypad;
import net.rim.device.apps.internal.phone.model.PhoneNumberServices;
import net.rim.device.internal.system.AudioInternal;
import net.rim.device.internal.system.InternalServices;

public final class DTMFEcho extends Thread {
   private StringBuffer _toneBuffer;
   private long _toneStartTime;
   private long _toneStopTime;
   private long _audioTimeout;
   private static final long GUID = -6523280153139351520L;
   private static final int MIN_ECHO_TIME = 120;
   private static final int MAX_ECHO_TIME = 2000;
   private static final int DEFAULT_ECHO_TIME = 120;
   private static final int DEFAULT_ECHO_GAP = 50;
   private static final int AUDIO_CHANNEL_TIMEOUT = 10000;
   private static final int AUDIO_CHANNEL_WARMUP_TIME = 100;

   private static final DTMFEcho getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      DTMFEcho instance = (DTMFEcho)ar.get(-6523280153139351520L);
      if (instance != null && !instance.isAlive()) {
         ar.remove(-6523280153139351520L);
         instance = null;
      }

      if (instance == null) {
         try {
            instance = new DTMFEcho();
            ar.put(-6523280153139351520L, instance);
            instance._audioTimeout = InternalServices.getUptime() + 10000;
            instance.start();
            return instance;
         } finally {
            instance = (DTMFEcho)ar.get(-6523280153139351520L);
            return instance;
         }
      } else {
         return instance;
      }
   }

   @Override
   public final void run() {
      this.manageTimeouts();
   }

   private final synchronized void manageTimeouts() {
      while (true) {
         if (this._toneStartTime != 0) {
            long toneDelta = this._toneStopTime - InternalServices.getUptime();
            if (toneDelta > 0 && toneDelta <= 2000) {
               try {
                  this.wait(toneDelta);
               } finally {
                  continue;
               }
            } else {
               this.internalStopToneImmediately();
            }
         } else if (this.hasMoreBufferedTones()) {
            long gapDelta = this._toneStopTime + 50 - InternalServices.getUptime();
            if (gapDelta > 0 && gapDelta <= 50) {
               try {
                  this.wait(gapDelta);
               } finally {
                  continue;
               }
            } else {
               char tone = this.getNextBufferedTone();
               if (tone != 0) {
                  this.internalStartTone(tone, 120);
               }
            }
         } else {
            long audioDelta = this._audioTimeout - InternalServices.getUptime();
            if (audioDelta <= 0 || audioDelta > 10000) {
               return;
            }

            try {
               this.wait(audioDelta);
            } finally {
               continue;
            }
         }
      }
   }

   public static final void echoTones(String tones) {
      DTMFEcho echo = getInstance();
      echo.internalAppendTones(tones);
   }

   public static final void echoTone(char tone) {
      DTMFEcho echo = getInstance();
      echo.cancelBufferedTones();
      echo.internalStartTone(tone, 120);
   }

   public static final void startTone(char tone) {
      DTMFEcho echo = getInstance();
      echo.cancelBufferedTones();
      echo.internalStartTone(tone, 2000);
   }

   public static final void stopTone() {
      DTMFEcho echo = getInstance();
      echo.internalStopTone();
   }

   private final synchronized void internalStartTone(char tone, int duration) {
      int echoTone = mapToAudioTone(tone);
      if (echoTone != -1) {
         this.internalStopToneImmediately();
         AudioRouter.getInstance().startTone(echoTone);
         long curTime = InternalServices.getUptime();
         this._toneStartTime = curTime;
         this._toneStopTime = curTime + duration;
         this._audioTimeout = curTime + 10000;
         this.notify();
      }
   }

   private final synchronized void internalStopTone() {
      if (this._toneStartTime != 0) {
         long curTime = InternalServices.getUptime();
         long toneMinimumTimeout = this._toneStartTime + 120;
         if (curTime < toneMinimumTimeout) {
            this._toneStopTime = toneMinimumTimeout;
         } else {
            this.internalStopToneImmediately();
         }

         this.notify();
      }
   }

   private final synchronized void internalStopToneImmediately() {
      if (this._toneStartTime != 0) {
         AudioRouter.getInstance().stopTone();
         this._toneStartTime = 0;
         this._toneStopTime = InternalServices.getUptime();
      }
   }

   private final synchronized void internalAppendTones(String tones) {
      if (this._toneBuffer == null) {
         this._toneBuffer = (StringBuffer)(new Object());
      }

      this._toneBuffer.append(tones);
      this.notify();
   }

   private final void cancelBufferedTones() {
      this._toneBuffer = null;
   }

   private final synchronized boolean hasMoreBufferedTones() {
      return this._toneBuffer != null && this._toneBuffer.length() > 0;
   }

   private final synchronized char getNextBufferedTone() {
      if (this._toneBuffer != null && this._toneBuffer.length() > 0) {
         char tone = this._toneBuffer.charAt(0);
         this._toneBuffer.deleteCharAt(0);
         return tone;
      } else {
         return '\u0000';
      }
   }

   private static final int mapToAudioTone(char tone) {
      if (Character.isUpperCase(tone)) {
         tone = PhoneNumberServices.getMnemonicKeyMapping(tone);
      }

      try {
         switch (tone) {
            case '#':
            case '*':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
               return AudioInternal.mapDTMFToneToAudioTone((byte)tone);
            default:
               tone = Keypad.getAltedChar(tone);
               return AudioInternal.mapDTMFToneToAudioTone((byte)tone);
         }
      } finally {
         ;
      }
   }
}
