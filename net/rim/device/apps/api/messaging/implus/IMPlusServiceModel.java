package net.rim.device.apps.api.messaging.implus;

import net.rim.device.apps.api.framework.model.RIMModel;

public interface IMPlusServiceModel {
   String PHONE_PREFIX = "PHN:";
   String FAX_PREFIX = "FAX:";
   String ONE_WAY_PAGE_PREFIX = "TAP:";
   String INTERACTIVE_HANDHELD_PREFIX = "IAP:";
   long IMPLUS_CONTEXT_FLAG = -3859986508589425865L;
   int IMPLUS_COMPOSE = 1;
   byte CONFIRM_DELIVERY_OPTION = 0;
   byte CONFIRM_READ_OPTION = 1;
   byte ALLOW_READ_CONFIRM_OPTION = 2;
   int OPTION_NO = 0;
   int OPTION_YES = 1;
   int OPTION_PROMPT = 2;
   byte PHONE = 0;
   byte FAX = 1;
   byte ONE_WAY_PAGE = 2;
   byte INTERACTIVE_HANDHELD = 3;

   boolean getOptionEnabled(int var1, byte var2);

   int getOptionDefault(int var1, byte var2);

   boolean getMethodEnabled(int var1, byte var2);

   Object getIMPlusServiceRecord();

   int[] getReceiptCapableServiceRecIds();

   String getServiceName(int var1);

   byte getAckRequestHeaderByte(RIMModel var1);

   void sendReceipt(RIMModel var1, boolean var2);

   void receiveReceipt(Object var1);

   String stripIMPlusPrefix(String var1, RIMModel var2);

   String appendIMPlusPrefix(RIMModel var1, String var2);

   IMPlusComposeModel[] getComposeModels();

   boolean isIMPlusCompose(Object var1);
}
