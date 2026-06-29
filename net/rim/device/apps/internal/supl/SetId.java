package net.rim.device.apps.internal.supl;

interface SetId {
   byte SUPL_SET_ID_ALT_MSISDN = 0;
   byte SUPL_SET_ID_ALT_MDN = 1;
   byte SUPL_SET_ID_ALT_MIN = 2;
   byte SUPL_SET_ID_ALT_IMSI = 3;
   byte SUPL_SET_ID_ALT_NAI = 4;
   byte SUPL_SET_ID_ALT_IP_ADDR = 5;
   byte SUPL_SET_ID_CHOICE_BIT_SIZE = 3;

   void decode(Nibbler var1);

   void encode(Stuffer var1);
}
