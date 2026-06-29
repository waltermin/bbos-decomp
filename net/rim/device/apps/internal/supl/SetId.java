package net.rim.device.apps.internal.supl;

interface SetId {
   byte SUPL_SET_ID_ALT_MSISDN;
   byte SUPL_SET_ID_ALT_MDN;
   byte SUPL_SET_ID_ALT_MIN;
   byte SUPL_SET_ID_ALT_IMSI;
   byte SUPL_SET_ID_ALT_NAI;
   byte SUPL_SET_ID_ALT_IP_ADDR;
   byte SUPL_SET_ID_CHOICE_BIT_SIZE;

   void decode(Nibbler var1);

   void encode(Stuffer var1);
}
