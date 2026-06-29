package net.rim.device.internal.bluetooth;

public interface HandsfreeGatewayListener extends BluetoothListener {
   int HOLD_ACTION_RELEASE_HELD_CALLS = 0;
   int HOLD_ACTION_RELEASE_ACTIVE_CALLS = 1;
   int HOLD_ACTION_HOLD_ACTIVE_CALLS = 2;
   int HOLD_ACTION_ADD_HELD_CALL = 3;
   int HOLD_ACTION_EXPLICIT_TRANSFER = 4;
   int FEATURE_ECHO_NOISE = 1;
   int FEATURE_CALL_WAITING = 2;
   int FEATURE_CLI_PRESENTATION = 4;
   int FEATURE_VOICE_RECOGNITION = 8;
   int FEATURE_VOLUME_CONTROL = 16;

   void handsfreeIncomingConnection(byte[] var1);

   void handsfreeConnected(int var1);

   void handsfreeDisconnected();

   void handsfreeAudioConnected(int var1);

   void handsfreeAudioDisconnected();

   void handsfreeAnswerCall();

   void handsfreeHangupCall();

   void handsfreeHoldCall(int var1, int var2);

   void handsfreeSpeakerVolumeChange(int var1);

   void handsfreeDialNumber(String var1);

   void handsfreeDialMemory(String var1);

   void handsfreeRedial();

   void handsfreeEnableEventReporting(boolean var1);

   void handsfreeEnableCallWaitingReporting(boolean var1);

   void handsfreeEnableCallerIDReporting(boolean var1);

   void handsfreeSendDTMF(int var1);

   void handsfreeFeatures(int var1);

   void handsfreeSendIndicators();

   void handsfreeUnknownATData(String var1);

   void handsfreeDisableNREC();

   void handsfreeSendHoldInfo();

   void handsfreeVoiceRecog(boolean var1);
}
