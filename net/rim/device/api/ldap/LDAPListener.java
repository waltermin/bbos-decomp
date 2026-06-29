package net.rim.device.api.ldap;

public interface LDAPListener {
   int STATE_STARTED;
   int STATE_ENCODING;
   int STATE_PENDING;
   int STATE_SENDING;
   int STATE_SENT;
   int STATE_DECODING;
   int STATE_COMPLETE;
   int STATE_ERROR;
   int STATE_TIMEOUT;
   int STATE_ABORTED;
   int STATE_NO_SERVICE_BOOK;
   int STATE_THROTTLED;

   void statusUpdate(LDAPQuery var1, int var2);

   void entryReady(LDAPQuery var1, int var2);
}
