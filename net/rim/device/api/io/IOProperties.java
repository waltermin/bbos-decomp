package net.rim.device.api.io;

public interface IOProperties {
   int FLAG_INVALID;
   int FLAG_UNSET;
   int FLAG_SET;
   int MOBITEX_MAILBOX_FLAG;
   int MOBITEX_REQUEST_ACK_FLAG;
   int CDMA_SET_FAST_DORMANCY_FLAG;
   int UDP_RETRY_ON_NO_CONTEXT;
   int MDP_DATAGRAM_ACK_FLAG;
   int MDP_PACKET_ACK_FLAG;
   int MDP_DONT_SET_FAST_DORMANCY_FLAG;
   int GME_REQUEST_CONFIRMATION_FLAG;
   int GME_ADD_SRC_FIELD_FLAG;
   int GME_FAIL_ON_MISSING_ROUTING_INFO_FLAG;
   int GME_DELAYED_ACK_FLAG;

   Object setProperty(String var1, Object var2);

   Object getProperty(String var1);

   void setFlag(int var1, boolean var2);

   int getFlag(int var1);

   boolean isFlagSet(int var1);
}
