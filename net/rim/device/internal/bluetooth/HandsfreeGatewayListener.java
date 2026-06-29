package net.rim.device.internal.bluetooth;

public interface HandsfreeGatewayListener extends BluetoothListener {
   int HOLD_ACTION_RELEASE_HELD_CALLS;
   int HOLD_ACTION_RELEASE_ACTIVE_CALLS;
   int HOLD_ACTION_HOLD_ACTIVE_CALLS;
   int HOLD_ACTION_ADD_HELD_CALL;
   int HOLD_ACTION_EXPLICIT_TRANSFER;
   int FEATURE_ECHO_NOISE;
   int FEATURE_CALL_WAITING;
   int FEATURE_CLI_PRESENTATION;
   int FEATURE_VOICE_RECOGNITION;
   int FEATURE_VOLUME_CONTROL;

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
