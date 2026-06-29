package net.rim.device.apps.internal.supl;

interface SlpAddress {
   byte SUPL_SLP_ADDRESS_ALT_IP_ADDRESS;
   byte SUPL_SLP_ADDRESS_ALT_FQDN;
   byte SUPL_SLP_ADDRESS_ALT_BIT_SIZE;

   void decode(Nibbler var1);

   void encode(Stuffer var1);
}
