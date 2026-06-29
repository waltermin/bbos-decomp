package net.rim.device.cldc.io.dns;

import java.util.Vector;

public interface DNSListener {
   int DNS_DNSEXCEPTION;
   int DNS_UNABLE_TO_MAKE_PACKET;
   int DNS_RADIOEXCEPTION_WHEN_SENDING;
   int DNS_MISSING_SERVER_ADDRESS;
   int DNS_UNABLE_TO_SEND_PACKET;
   int DNS_WAITING;
   int DNS_ADDRESSRECEIVED;
   int DNS_RESPONSETIMEOUT;
   int DNS_CANTCREATETIMER;
   int DNS_PACKETNOTSENT;
   int DNS_FORMATERROR;
   int DNS_SERVERFAILURE;
   int DNS_NAMEERROR;
   int DNS_NOTIMPLEMENTED;
   int DNS_REFUSED;
   int DNS_NORELEVANTDATA;
   int DNS_HOSTNAMERECEIVED;
   int DNS_RESPONSERECEIVED;

   void DNSEvent(int var1, int var2, Vector var3);
}
