package net.rim.device.api.ldap;

public interface LDAPListener {
   int STATE_STARTED = 0;
   int STATE_ENCODING = 1;
   int STATE_PENDING = 2;
   int STATE_SENDING = 3;
   int STATE_SENT = 4;
   int STATE_DECODING = 5;
   int STATE_COMPLETE = 6;
   int STATE_ERROR = 7;
   int STATE_TIMEOUT = 8;
   int STATE_ABORTED = 9;
   int STATE_NO_SERVICE_BOOK = 10;
   int STATE_THROTTLED = 11;

   void statusUpdate(LDAPQuery var1, int var2);

   void entryReady(LDAPQuery var1, int var2);
}
