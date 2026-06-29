package net.rim.device.apps.api.transmission;

import net.rim.device.internal.io.TrafficLogger;

public interface PacketSender extends DatagramConnectionUser {
   void transmitPacket(Packet var1, Object var2);

   void setTrafficLogger(TrafficLogger var1);
}
