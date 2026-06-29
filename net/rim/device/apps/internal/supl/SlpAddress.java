package net.rim.device.apps.internal.supl;

interface SlpAddress {
   byte SUPL_SLP_ADDRESS_ALT_IP_ADDRESS = 0;
   byte SUPL_SLP_ADDRESS_ALT_FQDN = 1;
   byte SUPL_SLP_ADDRESS_ALT_BIT_SIZE = 1;

   void decode(Nibbler var1);

   void encode(Stuffer var1);
}
