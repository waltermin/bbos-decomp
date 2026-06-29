package net.rim.device.cldc.io.dns;

import java.util.Vector;

public interface DNSListener {
   int DNS_DNSEXCEPTION = -1;
   int DNS_UNABLE_TO_MAKE_PACKET = -2;
   int DNS_RADIOEXCEPTION_WHEN_SENDING = -3;
   int DNS_MISSING_SERVER_ADDRESS = -4;
   int DNS_UNABLE_TO_SEND_PACKET = -100;
   int DNS_WAITING = 0;
   int DNS_ADDRESSRECEIVED = 1;
   int DNS_RESPONSETIMEOUT = 2;
   int DNS_CANTCREATETIMER = 3;
   int DNS_PACKETNOTSENT = 4;
   int DNS_FORMATERROR = 5;
   int DNS_SERVERFAILURE = 6;
   int DNS_NAMEERROR = 7;
   int DNS_NOTIMPLEMENTED = 8;
   int DNS_REFUSED = 9;
   int DNS_NORELEVANTDATA = 10;
   int DNS_HOSTNAMERECEIVED = 11;
   int DNS_RESPONSERECEIVED = 12;

   void DNSEvent(int var1, int var2, Vector var3);
}
