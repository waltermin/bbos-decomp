package net.rim.device.internal.bluetooth;

public interface BluetoothMEListener2 extends BluetoothMEListener {
   int SDP_ERROR_NONE = 0;
   int SDP_ERROR_BAD_VERSION = 1;
   int SDP_ERROR_BAD_HANDLE = 2;
   int SDP_ERROR_BAD_SYNTAX = 3;
   int SDP_ERROR_BAD_PDU_SIZE = 4;
   int SDP_ERROR_BAD_CONTINUATION = 5;
   int SDP_ERROR_OUT_OF_RESOURCES = 6;
   int LINK_MODE_ACTIVE = 0;
   int LINK_MODE_HOLD = 1;
   int LINK_MODE_SNIFF = 2;
   int LINK_MODE_PARK = 3;
   int SECURITY_RECORD_ID_HEADSET = -1;
   int SECURITY_RECORD_ID_HANDSFREE = -2;
   int SECURITY_RECORD_ID_AVDTP = -3;
   int SECURITY_RECORD_ID_AVCTP = -4;
   int SECURITY_RECORD_ID_UNKNOWN = -5;

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
