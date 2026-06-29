package net.rim.device.internal.bluetooth;

public interface BluetoothMEListener2 extends BluetoothMEListener {
   int SDP_ERROR_NONE;
   int SDP_ERROR_BAD_VERSION;
   int SDP_ERROR_BAD_HANDLE;
   int SDP_ERROR_BAD_SYNTAX;
   int SDP_ERROR_BAD_PDU_SIZE;
   int SDP_ERROR_BAD_CONTINUATION;
   int SDP_ERROR_OUT_OF_RESOURCES;
   int LINK_MODE_ACTIVE;
   int LINK_MODE_HOLD;
   int LINK_MODE_SNIFF;
   int LINK_MODE_PARK;
   int SECURITY_RECORD_ID_HEADSET;
   int SECURITY_RECORD_ID_HANDSFREE;
   int SECURITY_RECORD_ID_AVDTP;
   int SECURITY_RECORD_ID_AVCTP;
   int SECURITY_RECORD_ID_UNKNOWN;

   void inquiryComplete();

   void inquiryCancelled();

   void inquiryResult(byte[] var1, int var2, int var3, int var4);

   void deviceConnected(byte[] var1, int var2, int var3);

   void deviceDisconnected(byte[] var1, int var2);

   void deviceNameRetrieved(byte[] var1, byte[] var2);

   void pairingComplete(byte[] var1, int var2);

   void pinCodeRequired(byte[] var1, int var2);

   void authorizationRequired(byte[] var1, int var2);

   void serviceDiscoveryComplete(byte[] var1, int var2, byte[] var3);

   void linkModeChanged(byte[] var1, int var2, int var3);

   void connectionAccepted(byte[] var1);

   void linkKeyChangeComplete(byte[] var1, int var2);

   void authenticationComplete(byte[] var1, int var2);

   void encryptionComplete(byte[] var1, int var2);

   void hciFatalError(byte[] var1, int var2);
}
