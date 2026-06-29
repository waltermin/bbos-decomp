package com.fourthpass.wapstack;

import com.fourthpass.wapstack.wdp.WDPPacket;

public interface IPacketTransiver {
   int send(WDPPacket var1);

   int receive(WDPPacket var1);

   void setReceivingTimeout(int var1);

   boolean isSecure();

   boolean isClosed();
}
