package net.rim.device.apps.internal.supl;

final class UlpMessageFactory {
   static final byte ULP_MSG_ALT_SUPL_INIT = 0;
   static final byte ULP_MSG_ALT_SUPL_START = 1;
   static final byte ULP_MSG_ALT_SUPL_RESPONSE = 2;
   static final byte ULP_MSG_ALT_SUPL_POS_INIT = 3;
   static final byte ULP_MSG_ALT_SUPL_POS = 4;
   static final byte ULP_MSG_ALT_SUPL_END = 5;
   static final byte ULP_MSG_ALT_SUPL_AUTH_REQ = 6;
   static final byte ULP_MSG_ALT_SUPL_AUTH_RESP = 7;
   static final byte ULP_MSG_ALT_BIT_SIZE = 3;

   static final UlpMessage DecodeChoiceIndex(Nibbler nib) {
      UlpMessage ulpMessage = null;
      nib.getBit();
      switch (nib.getBitsLarge(3)) {
         case 0:
         default:
            return new SuplInit();
         case 1:
            return new SuplStart();
         case 2:
            return new SuplResponse();
         case 3:
            return new SuplPosInit();
         case 4:
            return new SuplPos();
         case 5:
            return new SuplEnd();
         case 6:
            return new SuplAuthReq();
         case 7:
            ulpMessage = new SuplAuthResp();
         case -1:
            return ulpMessage;
      }
   }
}
