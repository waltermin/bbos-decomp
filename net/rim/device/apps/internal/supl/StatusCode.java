package net.rim.device.apps.internal.supl;

final class StatusCode {
   byte status;
   static final byte STATUS_UNSPECIFIED;
   static final byte STATUS_SYSTEM_FAILURE;
   static final byte STATUS_UNEXPECTED_MESSAGE;
   static final byte STATUS_PROTOCOL_ERROR;
   static final byte STATUS_DATA_MISSING;
   static final byte STATUS_UNEXPECTED_DATA_VALUE;
   static final byte STATUS_POS_METHOD_FAILURE;
   static final byte STATUS_POS_METHOD_MISMATCH;
   static final byte STATUS_TARGET_SET_NOT_REACHABLE;
   static final byte STATUS_VERSION_NOT_SUPPORTED;
   static final byte STATUS_RESOURCE_SHORTAGE;
   static final byte STATUS_INVALID_SESSION_ID;
   static final byte STATUS_NON_PROXY_MODE_NOT_SUPPORTED;
   static final byte STATUS_PROXY_MODE_NOT_SUPPORTED;
   static final byte STATUS_POSITIONING_NOT_PERMITTED;
   static final byte STATUS_AUTH_NET_FAILURE;
   static final byte STATUS_AUTH_SUPL_INIT_FAILURE;
   static final byte STATUS_CONSENT_DENIED_BY_USER;
   static final byte STATUS_CONSENT_GRANTED_BY_USER;
   static final byte STATUS_BIT_SIZE;

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
      System.out.println(((StringBuffer)(new Object("StatusCode: "))).append(this.status).toString());
   }
}
