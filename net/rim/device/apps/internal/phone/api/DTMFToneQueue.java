package net.rim.device.apps.internal.phone.api;

import net.rim.device.api.system.Phone;
import net.rim.device.api.ui.Keypad;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.model.PhoneNumberServices;
import net.rim.device.internal.system.AudioInternal;
import net.rim.device.internal.system.InternalServices;

public final class DTMFToneQueue extends Thread implements PhoneEventListener {
   private int _callId;
   private boolean _aborted;
   private boolean _holdTone;
   private boolean _firstEmptyQueueSinceAddingTones;
   private int _failureDelay;
   private long _waitUntilReleaseStartTime;
   private long _waitUntilReleaseEndTime;
   private StringBuffer _queue = (StringBuffer)(new Object());
   private int _index;
   private StringBuffer _queueHidden = (StringBuffer)(new Object());
   private int _indexHidden;
   private static final int DTMF_EVENT = 1146375494;
   private static final int LOG_DTMF_START_TONE_FAILED = 1398035014;
   private static final int LOG_DTMF_ADD_TONE_FAILED = 1094992966;
   private static final int LOG_DTMF_ABORT = 1094865492;
   private static final int LOG_DTMF_INVALID_TONE = 1229870668;
   private static final int LOG_DTMF_ECHO_TONE = 1162037327;
   private static final int LOG_DTMF_SEND_TONE = 1397050948;
   private static final int LOG_DTMF_DELAY = 1145848153;
   private static final byte INVALID_DTMF_BYTE = -1;
   private static final int INVALID_DTMF_AUDIO_TONE = -13;
   private static final long DEFAULT_TONE_LENGTH = PhoneUtilities.idenTypeNetwork() ? 200 : 100;
   private static final long MAX_TONE_LENGTH = 250L;
   private static final long DEFAULT_GAP_LENGTH = 130L;
   private static final long DTMF_RETRY_LENGTH = 40L;
   private static final long MAX_DTMF_RETRY_LENGTH = 1000L;
   private static final long HOLD_MAXIMUM = 2000L;
   private static final long HOLD_MINIMUM = 120L;
   private static final int DELAY_MAXIMUM = 600;

   public DTMFToneQueue(int callId) {
      this._callId = callId;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      VoiceServices.addPhoneEventListener(this);
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         this.sendAllTones();
         var3 = false;
      } finally {
         if (var3) {
            VoiceServices.removePhoneEventListener(this);
         }
      }

      VoiceServices.removePhoneEventListener(this);
   }

   public final synchronized boolean startTone(char tone) {
      if (this.isAborted()) {
         Out.p(1146375494, 1398035014);
         return false;
      }

      if (this.isEmpty()) {
         this._holdTone = true;
      }

      this._queue.append(tone);
      this.notify();
      return true;
   }

   public final synchronized void stopTone() {
      if (this._holdTone) {
         long duration = InternalServices.getUptime() - this._waitUntilReleaseStartTime;
         if (duration < 120) {
            this._waitUntilReleaseEndTime = this._waitUntilReleaseStartTime + 120;
         } else {
            this._holdTone = false;
         }
      }

      this.notify();
   }

   public final synchronized boolean appendTone(char tone) {
      return this.appendTone(tone, false);
   }

   public final synchronized boolean appendTone(char tone, boolean programmatic) {
      if (this.isAborted()) {
         Out.p(1146375494, 1094992966);
         return false;
      }

      if (programmatic) {
         this._queueHidden.append(tone);
      } else {
         this._queue.append(tone);
      }

      this._holdTone = false;
      this._firstEmptyQueueSinceAddingTones = true;
      this.notify();
      return true;
   }

   public final synchronized void abort() {
      this._aborted = true;
      this.notify();
   }

   private final boolean isAborted() {
      return this._aborted;
   }

   public final String getTones() {
      return this._queue.length() == 0 ? null : this._queue.toString();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void sendAllTones() {
      while (!this.isAborted()) {
         try {
            char tone = this.getNextTone();
            if (tone != '\uffff') {
               byte dtmfTone = mapKeyToDTMFTone(tone);
               int echoTone = mapDTMFToneToAudioTone(dtmfTone);
               if (echoTone >= 0) {
                  while (!this.isAborted()) {
                     boolean sent = this.doTone(dtmfTone);
                     if (sent) {
                        this.pause(130);
                        break;
                     }
                  }
               }
            }
         } catch (Throwable var6) {
            Out.p(((StringBuffer)(new Object("DTMFQueue exception: "))).append(e).toString());
            continue;
         }
      }
   }

   private static final int mapDTMFToneToAudioTone(byte dtmfTone) {
      try {
         return AudioInternal.mapDTMFToneToAudioTone(dtmfTone);
      } finally {
         Out.p(1146375494, 1229870668, dtmfTone);
         return -1;
      }
   }

   private final boolean doTone(byte dtmfTone) {
      int callId = this._callId;
      if (callId == 0) {
         callId = Phone.getInstance().getActiveCallId();
      }

      boolean sent = VoiceServices.startDTMF(callId, dtmfTone);
      if (sent) {
         if (this._holdTone) {
            this.waitUntilReleased();
            VoiceServices.stopDTMF(callId);
         } else {
            this.pause(DEFAULT_TONE_LENGTH);
            VoiceServices.stopDTMF(callId);
         }
      }

      if (!sent) {
         if (this._failureDelay < 1000) {
            this.pause(40);
            this._failureDelay = (int)((long)this._failureDelay + 40);
            return sent;
         } else {
            System.out.println("DTMF start timeout");
            this._failureDelay = 0;
            return true;
         }
      } else {
         if (this._failureDelay > 0) {
            Out.p(1146375494, 1145848153, this._failureDelay);
            this._failureDelay = 0;
         }

         return sent;
      }
   }

   private final boolean isEmpty() {
      return this._indexHidden == this._queueHidden.length() && this._index == this._queue.length();
   }

   private final synchronized char getNextTone() {
      while (!this.isAborted() && this.isEmpty()) {
         try {
            if (this._firstEmptyQueueSinceAddingTones) {
               this._firstEmptyQueueSinceAddingTones = false;
               VoiceServices.broadcastEvent(170000);
            }

            this.wait();
         } finally {
            continue;
         }
      }

      if (this._indexHidden < this._queueHidden.length()) {
         char tone = this._queueHidden.charAt(this._indexHidden++);
         if (Character.isUpperCase(tone)) {
            tone = PhoneNumberServices.getMnemonicKeyMapping(tone);
         }

         return tone;
      } else if (this._index < this._queue.length()) {
         char tone = this._queue.charAt(this._index++);
         if (Character.isUpperCase(tone)) {
            tone = PhoneNumberServices.getMnemonicKeyMapping(tone);
         }

         return tone;
      } else {
         return '\uffff';
      }
   }

   private final synchronized void waitUntilReleased() {
      this._waitUntilReleaseStartTime = InternalServices.getUptime();
      this._waitUntilReleaseEndTime = this._waitUntilReleaseStartTime + 2000;

      while (this._holdTone && !this.isAborted() && this.isEmpty()) {
         long waitTime = Math.min(2000, this._waitUntilReleaseEndTime - InternalServices.getUptime());
         if (waitTime <= 0) {
            break;
         }

         try {
            this.wait(waitTime);
         } finally {
            continue;
         }
      }

      long duration = InternalServices.getUptime() - this._waitUntilReleaseStartTime;
      if (duration < 120) {
         this.pause(120 - duration);
      }
   }

   private final void pause(long time) {
      if (time > 0) {
         try {
            Thread.sleep(time);
         } finally {
            return;
         }
      }
   }

   @Override
   public final void phoneEventNotify(int eventId, int callId, Object context) {
      switch (eventId) {
         case 1002:
         case 1006:
         case 3004:
         case 201010:
            if (callId == this._callId) {
               this.abort();
            }
      }
   }

   public static final byte mapKeyToDTMFTone(char dtmfKey) {
      byte toneToSend = -1;
      switch (dtmfKey) {
         case '#':
            return (byte)dtmfKey;
         case '*':
            return (byte)dtmfKey;
         default:
            char digitKey = dtmfKey;
            if (!Character.isDigit(dtmfKey)) {
               digitKey = Keypad.getAltedChar(dtmfKey);
            }

            return (byte)digitKey;
      }
   }
}
