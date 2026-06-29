package net.rim.device.apps.internal.supl;

final class UlpMessageFactory {
   static final byte ULP_MSG_ALT_SUPL_INIT;
   static final byte ULP_MSG_ALT_SUPL_START;
   static final byte ULP_MSG_ALT_SUPL_RESPONSE;
   static final byte ULP_MSG_ALT_SUPL_POS_INIT;
   static final byte ULP_MSG_ALT_SUPL_POS;
   static final byte ULP_MSG_ALT_SUPL_END;
   static final byte ULP_MSG_ALT_SUPL_AUTH_REQ;
   static final byte ULP_MSG_ALT_SUPL_AUTH_RESP;
   static final byte ULP_MSG_ALT_BIT_SIZE;

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
