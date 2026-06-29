package net.rim.device.api.itpolicy;

public interface CommonPolicy {
   int LOCK_OWNER_INFO = 1;
   int IT_POLICY_NOTIFICATION = 2;
   int BES_VERSION = 3;
   int CONFIRM_ON_SEND = 4;
   int SET_OWNER_INFO = 5;
   int SET_OWNER_NAME = 6;
   int DISABLE_MMS = 7;
   int DISABLE_VAD = 8;
   int DISABLE_KPTT = 9;
   int DISABLE_VOICE_NOTE_RECORDING = 10;
   byte LOCK_OWNER_INFO_DEFAULT = 0;
   boolean IT_POLICY_NOTIFICATION_DEFAULT = false;
   boolean DISABLE_MMS_DEFAULT = false;
   boolean DISABLE_VAD_DEFAULT = false;
   boolean DISABLE_KPTT_DEFAULT = false;
   boolean DISABLE_VOICE_NOTE_RECORDING_DEFAULT = false;
   byte LOCK_OWNER_INFO_LOCK_INFO = 1;
   byte LOCK_OWNER_INFO_LOCK_NAME = 2;
}
