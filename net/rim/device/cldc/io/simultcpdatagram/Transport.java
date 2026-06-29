package net.rim.device.cldc.io.simultcpdatagram;

import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.IOProperties;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioPacketHeader;
import net.rim.device.cldc.io.nativebase.NativeTransport;
import net.rim.device.cldc.io.simultcp.SimulTcpAddress;
import net.rim.device.internal.io.tcp.Deque;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.system.TCPPacketHeader;
import net.rim.device.internal.system.TCPPacketListener;

public final class Transport extends NativeTransport implements TCPPacketListener {
   private byte[] _txAddressBytes = new byte[]{0, 0, 0, 0};
   private Object _sendLock = new Object();
   Deque dgPool;
   Deque tcpDgPropsPool;
   protected SimulTcpAddress cachedTcpAddress;
   protected String cachedTcpApn;
   public static final int MAX_DGS;
   public static final int MIN_DGS;
   public static final int MAX_TDG_PROPS;
   public static final int MIN_TDG_PROPS;

   public final void giveBackTcpDatagramProperties(SimulTcpDatagramProperties tdgp) {
      if (this.tcpDgPropsPool.getSize() < 6) {
         this.tcpDgPropsPool.enqueueTail(tdgp);
      }
   }

   public final SimulTcpDatagramBase getNewSimulTcpDatagram() {
      if (this.dgPool.getSize() > 2) {
         try {
            return (SimulTcpDatagramBase)this.dgPool.dequeueHead();
         } finally {
            return new SimulTcpDatagramBase();
         }
      } else {
         return new SimulTcpDatagramBase();
      }
   }

   public final void giveBackDatagram(SimulTcpDatagramBase dg) {
      if (this.dgPool.getSize() < 6) {
         if (dg.tcpProps != null) {
            this.giveBackTcpDatagramProperties(dg.tcpProps);
         }

         this.dgPool.enqueueTail(dg);
      }
   }

   public final SimulTcpDatagramProperties getNewSimulTcpDatagramProperties() {
      if (this.tcpDgPropsPool.getSize() > 2 && this.tcpDgPropsPool.getSize() > 0) {
         try {
            return (SimulTcpDatagramProperties)this.tcpDgPropsPool.dequeueHead();
         } finally {
            return new SimulTcpDatagramProperties();
         }
      } else {
         return new SimulTcpDatagramProperties();
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void packetReceived(TCPPacketHeader header, byte[] data) {
      if (header._isSimulTcpPacket) {
         try {
            byte[] ipAddress = header.getSourceAddress();
            int srcPort = header.getDestinationPort();
            int destPort = header.getSourcePort();
            boolean var14 = false /* VF: Semaphore variable */;

            String apn;
            try {
               var14 = true;
               apn = RadioInfo.getAccessPointName(header._accessPointNumber);
               var14 = false;
            } finally {
               if (var14) {
                  EventLogger.logEvent(3177555665113652794L, 1413763635, 2);
                  return;
               }
            }

            SimulTcpAddress tcpAddress;
            if (this.cachedTcpAddress != null
               && this.cachedTcpAddress.getIpAddress() == TCPPacketHeader.IPv4ByteArrayToInt(ipAddress)
               && this.cachedTcpAddress.getDestPort() == destPort
               && this.cachedTcpAddress.getLocalPort() == srcPort
               && this.cachedTcpAddress.getApnName().equalsIgnoreCase(apn)) {
               tcpAddress = this.cachedTcpAddress;
            } else {
               tcpAddress = new SimulTcpAddress(ipAddress, destPort, -1, apn);
               if ((header._controlCode == 3 || header._controlCode == 6) && header._controlDescription == 1) {
                  tcpAddress.setLocalPort(-2);
               } else {
                  tcpAddress.setLocalPort(srcPort);
               }

               this.cachedTcpAddress = tcpAddress;
               this.cachedTcpApn = apn;
            }

            SimulTcpDatagramProperties tcpProps = this.getNewSimulTcpDatagramProperties();
            tcpProps.setData(
               header._sourceAddress,
               header._destinationAddress,
               header.getSourcePort(),
               header.getDestinationPort(),
               header._controlCode,
               header._controlDescription,
               header._socketID,
               header._sequenceNumber
            );
            SimulTcpDatagramBase simultcpdatagram = this.getNewSimulTcpDatagram();
            simultcpdatagram.setData(data, 0, data.length, tcpAddress);
            simultcpdatagram.tcpProps = tcpProps;
            if (!this.passUpDatagram(simultcpdatagram)) {
               EventLogger.logEvent(3177555665113652794L, 1413763629, 3);
               return;
            }
         } finally {
            return;
         }
      }
   }

   @Override
   public final void nativeSendSetupData(Datagram datagram) {
      super._txData = datagram.getData();
      super._txOffset = datagram.getOffset();
      super._txLength = datagram.getLength();
   }

   @Override
   public final int nativeGetStatus(int status) {
      switch (status) {
         case -3:
         case 249:
         case 250:
            return -3;
         case -2:
         case 0:
         case 1:
         case 2:
         case 129:
            return status;
         case 252:
            return -4;
         case 255:
            return 12674;
         default:
            return 12673;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void nativeSendSetupHeader(Datagram datagram, IOProperties properties) {
      TCPPacketHeader header = (TCPPacketHeader)super._txHeader;
      int length = datagram.getLength();
      SimulTcpAddress tcpAddress = (SimulTcpAddress)super._txAddressBase;
      SimulTcpDatagramProperties tcpProps = ((SimulTcpDatagramBase)datagram).tcpProps;
      if (length <= super._maxPacketSize
         && tcpAddress.getIpAddress() != -1
         && tcpAddress.getDestPort() != -1
         && tcpAddress.getLocalPort() != -1
         && (tcpAddress.getApnName().length() != 0 || RadioInfo.getNetworkType() != 3 && RadioInfo.getNetworkType() != 7)) {
         boolean var11 = false /* VF: Semaphore variable */;

         try {
            var11 = true;
            String e = tcpAddress.getApnName();
            super._txApnId = RadioInternal.registerAccessPointNumber(e);
            var11 = false;
         } finally {
            if (var11) {
               EventLogger.logEvent(3177555665113652794L, 1413763891, 2);
               throw new Object();
            }
         }

         synchronized (this._sendLock) {
            DatagramAddressBase.writeInt(this._txAddressBytes, 0, tcpAddress.getIpAddress());
            header.setDestinationAddress(this._txAddressBytes);
            header.setSourcePort(tcpAddress.getLocalPort());
            header.setDestinationPort(tcpAddress.getDestPort());
            header._accessPointNumber = super._txApnId;
            header._sequenceNumber = tcpProps.sequenceNumber;
            header._acknowledgementNumber = 0;
            header._flags = 0;
            header._window = 0;
            header._urgentPointer = 0;
            header._dataOffset = 0;
            header._controlCode = tcpProps.controlCode;
            header._controlDescription = tcpProps.controlDescription;
            header._socketID = tcpProps.socketID;
            header._isSimulTcpPacket = true;
         }
      } else {
         throw new Object("Bad length or address");
      }
   }

   @Override
   public final void nativeInit() {
      EventLogger.register(super.GUID, super.STR, 2);
      super._networkServiceMask = 4;
      super._txHeader = (RadioPacketHeader)(new Object());
      super._maxPacketSize = TCPPacketHeader.getMaxPacketSize();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final DatagramAddressBase newDatagramAddressBase(String address, boolean swap) {
      try {
         SimulTcpAddress newAddress = new SimulTcpAddress(address);
         if (swap) {
            newAddress.swap();
         }

         return newAddress;
      } catch (Throwable var5) {
         System.out.println(e);
         throw new Object("Bad address");
      }
   }

   @Override
   public final void nativeSendVerify(DatagramAddressBase addressBase, Datagram datagram) {
      if (addressBase instanceof SimulTcpAddress) {
         super._txAddressBase = (SimulTcpAddress)addressBase;
      } else {
         if (datagram instanceof SimulTcpDatagramBase) {
            SimulTcpAddress tcpAddress = (SimulTcpAddress)((SimulTcpDatagramBase)datagram).getAddressBase();
            if (tcpAddress != null) {
               super._txAddressBase = tcpAddress;
               return;
            }
         }

         super._txAddressBase = new SimulTcpAddress(datagram.getAddress());
      }
   }

   public Transport() {
      super.GUID = 3177555665113652794L;
      super.STR = "net.rim.tcpdatagram";
      super.SEND_BACKOFF_DEF = 2000;
      this.dgPool = (Deque)(new Object());
      int num = 4;

      for (int i = 0; i < num; i++) {
         this.dgPool.enqueueHead(new SimulTcpDatagramBase());
      }

      this.tcpDgPropsPool = (Deque)(new Object());
      int var3 = 4;

      for (int i = 0; i < var3; i++) {
         this.tcpDgPropsPool.enqueueHead(new SimulTcpDatagramProperties());
      }
   }

   @Override
   protected final void superSend(Datagram datagram) {
      super.superSend(datagram);
      this.giveBackDatagram((SimulTcpDatagramBase)datagram);
   }
}
