package net.rim.device.apps.internal.supl;

final class StatusCode {
   byte status;
   static final byte STATUS_UNSPECIFIED = 0;
   static final byte STATUS_SYSTEM_FAILURE = 1;
   static final byte STATUS_UNEXPECTED_MESSAGE = 2;
   static final byte STATUS_PROTOCOL_ERROR = 3;
   static final byte STATUS_DATA_MISSING = 4;
   static final byte STATUS_UNEXPECTED_DATA_VALUE = 5;
   static final byte STATUS_POS_METHOD_FAILURE = 6;
   static final byte STATUS_POS_METHOD_MISMATCH = 7;
   static final byte STATUS_TARGET_SET_NOT_REACHABLE = 8;
   static final byte STATUS_VERSION_NOT_SUPPORTED = 10;
   static final byte STATUS_RESOURCE_SHORTAGE = 11;
   static final byte STATUS_INVALID_SESSION_ID = 12;
   static final byte STATUS_NON_PROXY_MODE_NOT_SUPPORTED = 13;
   static final byte STATUS_PROXY_MODE_NOT_SUPPORTED = 14;
   static final byte STATUS_POSITIONING_NOT_PERMITTED = 15;
   static final byte STATUS_AUTH_NET_FAILURE = 16;
   static final byte STATUS_AUTH_SUPL_INIT_FAILURE = 17;
   static final byte STATUS_CONSENT_DENIED_BY_USER = 18;
   static final byte STATUS_CONSENT_GRANTED_BY_USER = 19;
   static final byte STATUS_BIT_SIZE = 5;

   StatusCode() {
   }

   StatusCode(byte status) {
      this.status = status;
   }

   final void encode(Stuffer stuff) {
      stuff.putBit(false);
      stuff.putBits(this.status, 5);
   }

   final void decode(Nibbler nib) {
      if (!nib.getBit()) {
         this.status = (byte)nib.getBitsLarge(5);
      }
   }

   final void print() {
      System.out.println("StatusCode: " + this.status);
   }
}
