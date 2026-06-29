package net.rim.device.cldc.io.mdp;

import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramStatusListener;

final class MdpWaitingQueueThread$MdpTxDatagram {
   int reference;
   int retries;
   byte completeRetries;
   long endTime;
   long maxTime;
   int datagramAckTimeout;
   DatagramStatusListener listener;
   DatagramAddressBase addressBase;
   Datagram datagram;
   boolean overflowDatagram;

   private MdpWaitingQueueThread$MdpTxDatagram() {
   }

   MdpWaitingQueueThread$MdpTxDatagram(MdpWaitingQueueThread$1 x0) {
      this();
   }
}
