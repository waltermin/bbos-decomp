package net.rim.device.apps.internal.supl;

class UlpMessage {
   static final byte ULP_MSG_ALT_SUPL_INIT = 0;
   static final byte ULP_MSG_ALT_SUPL_START = 1;
   static final byte ULP_MSG_ALT_SUPL_RESPONSE = 2;
   static final byte ULP_MSG_ALT_SUPL_POS_INIT = 3;
   static final byte ULP_MSG_ALT_SUPL_POS = 4;
   static final byte ULP_MSG_ALT_SUPL_END = 5;
   static final byte ULP_MSG_ALT_SUPL_AUTH_REQ = 6;
   static final byte ULP_MSG_ALT_SUPL_AUTH_RESP = 7;
   static final byte ULP_MSG_ALT_BIT_SIZE = 3;

   void decode(Nibbler _1) {
      throw null;
   }

   void encode(Stuffer _1) {
      throw null;
   }

   void print() {
      throw null;
   }
}
