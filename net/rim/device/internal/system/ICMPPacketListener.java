package net.rim.device.internal.system;

import net.rim.device.api.system.RadioPacketListener;

public interface ICMPPacketListener extends RadioPacketListener {
   void packetReceived(ICMPPacketHeader var1, byte[] var2);
}
