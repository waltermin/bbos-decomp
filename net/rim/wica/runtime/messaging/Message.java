package net.rim.wica.runtime.messaging;

import net.rim.wica.transport.Serializable;

public interface Message extends Serializable {
   int DESTINATION_SERVER;
   int DESTINATION_WICLET_LOCAL;
   int DESTINATION_SERVICE;
   int DESTINATION_SYSTEM;

   void setDestinationType(int var1);

   long getAGID();

   void setAGID(long var1);

   long getWicletID();

   void setWicletID(long var1);

   String getServiceID();

   void setServiceID(String var1);

   int getMessageCode();

   void setMessageCode(int var1);

   String getMessageName();

   void setMessageName(String var1);

   void setSecurityMode(int var1);

   ReadableDataStream openReadableDataStream();

   WritableDataStream openWritableDataStream();
}
