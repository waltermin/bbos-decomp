package net.rim.wica.runtime.persistence;

import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.util.LongEnumeration;

public interface MessageStore {
   void storeIncomingMessages(long var1, BigVector var3, boolean var4);

   void deleteIncomingMessages(long var1, int var3);

   BigVector loadIncomingMessages(long var1);

   void storeOutgoingMessages(long var1, BigVector var3, boolean var4);

   void deleteOutgoingMessages(long var1, int var3);

   BigVector loadOutgoingMessages(long var1);

   void storeInRequestQueue(BigVector var1);

   void deleteInRequestQueue();

   BigVector loadInRequestQueue();

   LongEnumeration getInMessageKeys();

   LongEnumeration getOutMessageKeys();
}
