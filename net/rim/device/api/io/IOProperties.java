package net.rim.device.api.io;

public interface IOProperties {
   int FLAG_INVALID = -1;
   int FLAG_UNSET = 0;
   int FLAG_SET = 1;
   int MOBITEX_MAILBOX_FLAG = 2;
   int MOBITEX_REQUEST_ACK_FLAG = 4;
   int CDMA_SET_FAST_DORMANCY_FLAG = 8;
   int UDP_RETRY_ON_NO_CONTEXT = 1024;
   int MDP_DATAGRAM_ACK_FLAG = 128;
   int MDP_PACKET_ACK_FLAG = 256;
   int MDP_DONT_SET_FAST_DORMANCY_FLAG = 2048;
   int GME_REQUEST_CONFIRMATION_FLAG = 16;
   int GME_ADD_SRC_FIELD_FLAG = 32;
   int GME_FAIL_ON_MISSING_ROUTING_INFO_FLAG = 64;
   int GME_DELAYED_ACK_FLAG = 512;

   Object setProperty(String var1, Object var2);

   Object getProperty(String var1);

   void setFlag(int var1, boolean var2);

   int getFlag(int var1);

   boolean isFlagSet(int var1);
}
