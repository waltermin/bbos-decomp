package net.rim.device.cldc.io.ippp;

import java.util.Enumeration;
import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramStatusListener;
import net.rim.device.api.util.IntHashtable;

final class DatagramProtocol extends ConnectionBase {
   private DatagramStatusListener _datagramStatuslistener;
   private IntHashtable _sendingDatagrams = (IntHashtable)(new Object());
   private static final byte TCP_PROTOCOL_BYTE = 6;

   public DatagramProtocol(String name, boolean useTimeouts) {
      super(name, useTimeouts);
      super._protocol = 6;
   }

   public DatagramProtocol(Queue queue, String name, boolean useTimeouts) {
      super(queue, name, useTimeouts);
      super._protocol = 6;
   }

   @Override
   public final void setDatagramStatusListener(DatagramStatusListener newDatagramStatuslistener) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final boolean isAddressed(String addr) {
      if (addr != null) {
         int index = addr.lastIndexOf(58);
         if (index == -1) {
            int connectionId = Integer.parseInt(addr);
            if (this.getConnectionID() == connectionId) {
               return true;
            }

            return false;
         }
      }

      return false;
   }

   public final void initializeDatagram(IPPPDatagramBase initDatagram) {
      SocketDatagram tcpDatagram = (SocketDatagram)initDatagram;
      super.initializeDatagram(tcpDatagram);
      String domainName = super._url.getHost();
      if (domainName == null) {
         domainName = "";
      }

      tcpDatagram.setDomainName(domainName);
      tcpDatagram.setPort((short)super._url.getPort());
   }

   @Override
   public final int allocateDatagramId(Datagram datagram) {
      int id = super.allocateDatagramId(datagram);
      this._sendingDatagrams.put(id, datagram);
      return id;
   }

   @Override
   public final void updateDatagramStatus(int dgId, int code, Object context) {
      if (code == 0) {
         this._sendingDatagrams.remove(dgId);
      } else if (code == 4243) {
         Datagram dg = (Datagram)this._sendingDatagrams.get(dgId);
         if (dg != null) {
            label63:
            try {
               this.superSend(dg);
               return;
            } finally {
               break label63;
            }
         }
      }

      if ((code | 128) == code) {
         this._sendingDatagrams.remove(dgId);
         this.errorOccured(0, "Failed to transmit");
      }

      if (this._datagramStatuslistener != null) {
         try {
            this._datagramStatuslistener.updateDatagramStatus(dgId, code, context);
         } finally {
            return;
         }
      }
   }

   @Override
   public final void dataReceived(SocketDatagram datagram) {
      super.dataReceived(datagram);
      if (this._datagramStatuslistener != null) {
         try {
            this._datagramStatuslistener.updateDatagramStatus(-1, 4608, null);
         } finally {
            return;
         }
      }
   }

   @Override
   public final void connectRequestReceived(SocketDatagram datagram) {
   }

   @Override
   public final void disconnectOrderReceived(SocketDatagram datagram) {
      if (this._datagramStatuslistener != null) {
         try {
            this._datagramStatuslistener.updateDatagramStatus(-1, 4609, null);
         } finally {
            return;
         }
      }
   }

   @Override
   public final void errorReceived(SocketDatagram datagram) {
      if (this._datagramStatuslistener != null) {
         try {
            this._datagramStatuslistener.updateDatagramStatus(-1, 4611, null);
         } finally {
            return;
         }
      }
   }

   public final boolean cancelOutboundPackets() {
      boolean shouldSendDisconnect = true;

      try {
         Enumeration datagrams = this._sendingDatagrams.elements();

         while (datagrams.hasMoreElements()) {
            Datagram dg = (Datagram)datagrams.nextElement();
            if (dg instanceof SocketDatagram) {
               SocketDatagram socketDg = (SocketDatagram)dg;
               int flags = socketDg.getIPPPFlags();
               if ((flags & 1) != 0) {
                  shouldSendDisconnect = false;
               }
            }

            this.cancel(dg);
         }
      } finally {
         return shouldSendDisconnect;
      }

      return shouldSendDisconnect;
   }

   @Override
   public final void close() {
      if (this._datagramStatuslistener != null) {
         label45:
         try {
            this._datagramStatuslistener.updateDatagramStatus(-1, 4609, null);
         } finally {
            break label45;
         }
      }

      super.close();
      if (this._datagramStatuslistener != null) {
         label41:
         try {
            this._datagramStatuslistener.updateDatagramStatus(-1, 4610, null);
         } finally {
            break label41;
         }
      }

      this._datagramStatuslistener = null;
   }

   @Override
   public final int getProperties(String name) {
      return 1;
   }
}
