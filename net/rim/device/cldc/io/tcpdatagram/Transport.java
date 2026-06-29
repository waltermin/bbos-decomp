package net.rim.device.cldc.io.tcpdatagram;

import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.IOProperties;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioPacketHeader;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.datarecovery.DataRecovery;
import net.rim.device.cldc.io.datarecovery.DataRecoveryListener;
import net.rim.device.cldc.io.nativebase.NativeTransport;
import net.rim.device.internal.io.tcp.Deque;
import net.rim.device.internal.io.tcp.SimpleTcpDataBlock;
import net.rim.device.internal.io.tcp.TcpAddress;
import net.rim.device.internal.io.tcp.TcpConstants;
import net.rim.device.internal.io.tcp.TcpDatagramBase;
import net.rim.device.internal.io.tcp.TcpDatagramProperties;
import net.rim.device.internal.io.tcp.TcpObjectPool;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.system.TCPPacketHeader;
import net.rim.device.internal.system.TCPPacketListener;
import net.rim.device.internal.system.VoiceDataUsage;

public final class Transport extends NativeTransport implements TcpObjectPool, TCPPacketListener, TcpConstants, DataRecoveryListener {
   private boolean _isRunningInSimulator = DeviceInfo.isSimulator();
   private byte[] _txAddressBytes = new byte[]{0, 0, 0, 0};
   private Object _sendLock = new Object();
   private boolean _txRetryOnNoContext;
   private boolean _connectionAvailable;
   private boolean _isBlackBerryTrafficInvalid;
   private TcpAddress _cachedTcpAddress;
   protected String _cachedTcpApn;
   private Deque _dgPool;
   private Deque _tcpDgPropsPool;
   public static final int MAX_DGS;
   public static final int MIN_DGS;
   public static final int MAX_TDG_PROPS;
   public static final int MIN_TDG_PROPS;

   public final Datagram newDatagram(byte[] buffer, int offset, int length, DatagramAddressBase address) {
      return new TcpDatagramBase(buffer, offset, length, address);
   }

   public final Datagram newDatagram() {
      return new TcpDatagramBase();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void packetReceived(TCPPacketHeader header, byte[] data) {
      if (!this._isRunningInSimulator || !header._isSimulTcpPacket) {
         try {
            byte[] ipAddress = header.getSourceAddress();
            int srcPort = header.getDestinationPort();
            int destPort = header.getSourcePort();
            boolean var15 = false /* VF: Semaphore variable */;

            String apn;
            try {
               var15 = true;
               apn = RadioInfo.getAccessPointName(header._accessPointNumber);
               var15 = false;
            } finally {
               if (var15) {
                  EventLogger.logEvent(3177555665113652794L, 1413763635, 2);
                  return;
               }
            }

            TcpAddress tcpAddress = null;
            if (this._cachedTcpAddress != null
               && this._cachedTcpAddress.getIpAddress() == TCPPacketHeader.IPv4ByteArrayToInt(ipAddress)
               && this._cachedTcpAddress.getDestPort() == destPort
               && this._cachedTcpAddress.getLocalPort() == srcPort
               && this._cachedTcpAddress.getApnName().equalsIgnoreCase(apn)) {
               tcpAddress = this._cachedTcpAddress;
            } else {
               tcpAddress = new TcpAddress(ipAddress, destPort, srcPort, apn);
               this._cachedTcpAddress = tcpAddress;
               this._cachedTcpApn = apn;
            }

            TcpDatagramProperties tcpProps = this.getNewTcpDatagramProperties();
            tcpProps.setData(
               header._sequenceNumber, header._acknowledgementNumber, header._dataOffset, header._flags, header._window, header._urgentPointer, false
            );
            parseTcpOptions(data, tcpProps);
            TcpDatagramBase tcpdatagram = this.getNewTcpDatagram();
            int optionsLength = (tcpProps._dataOffset - 5) * 4;
            tcpdatagram.setData(data, optionsLength, data.length - optionsLength, tcpAddress);
            tcpdatagram._tcpProps = tcpProps;
            if (!this.passUpDatagram(tcpdatagram)) {
               EventLogger.logEvent(3177555665113652794L, 1413763629, 3);
               return;
            }

            if (!super._itPolicyEnabled) {
               VoiceDataUsage.addDataBytes(data.length);
            }
         } finally {
            return;
         }
      }
   }

   @Override
   public final TcpDatagramBase getNewTcpDatagram() {
      if (this._dgPool.getSize() > 2) {
         label32:
         try {
            return (TcpDatagramBase)this._dgPool.dequeueHead();
         } finally {
            break label32;
         }
      }

      try {
         return (TcpDatagramBase)this.newDatagram();
      } finally {
         return new TcpDatagramBase();
      }
   }

   @Override
   public final TcpDatagramBase getNewTcpDatagram(TcpAddress address) {
      if (this._dgPool.getSize() > 2) {
         label36:
         try {
            TcpDatagramBase dg = (TcpDatagramBase)this._dgPool.dequeueHead();
            if (dg != null) {
               dg.setData(null, 0, 0, address);
               return dg;
            }
         } finally {
            break label36;
         }
      }

      try {
         return (TcpDatagramBase)this.newDatagram(null, 0, 0, address);
      } finally {
         return new TcpDatagramBase(null, 0, 0, address);
      }
   }

   @Override
   public final void giveBackDatagram(TcpDatagramBase dg) {
      if (this._dgPool.getSize() < 6) {
         if (dg._tcpProps != null) {
            this.giveBackTcpDatagramProperties(dg._tcpProps);
         }

         dg.simpleReset();
         this._dgPool.enqueueTail(dg);
      }
   }

   @Override
   public final TcpDatagramProperties getNewTcpDatagramProperties() {
      if (this._tcpDgPropsPool.getSize() > 2) {
         try {
            return (TcpDatagramProperties)this._tcpDgPropsPool.dequeueHead();
         } finally {
            return new TcpDatagramProperties();
         }
      } else {
         return new TcpDatagramProperties();
      }
   }

   @Override
   public final void giveBackTcpDatagramProperties(TcpDatagramProperties tdgp) {
      if (this._tcpDgPropsPool.getSize() < 6) {
         this._tcpDgPropsPool.enqueueTail(tdgp);
      }
   }

   @Override
   public final void dataRecoveryEventOccurred(int event, int linkType) {
      switch (event) {
         case 1:
            this._connectionAvailable = true;
         default:
            return;
         case 3:
            this._connectionAvailable = false;
      }
   }

   @Override
   public final void nativeInit() {
      EventLogger.register(super.GUID, super.STR, 2);
      EventLogger.logEvent(super.GUID, "nativeInit function called.  TcpDatagram being initialised".getBytes());
      super._networkServiceMask = 4;
      super._txHeader = (RadioPacketHeader)(new Object());
      super._maxPacketSize = TCPPacketHeader.getMaxPacketSize();
      DataRecovery.getInstance().addListener(this);
   }

   public Transport() {
      super.GUID = 3177555665113652794L;
      super.STR = "net.rim.tcpdatagram";
      super.SEND_BACKOFF_DEF = 2000;
      this._dgPool = (Deque)(new Object());
      int num = 4;

      for (int i = 0; i < num; i++) {
         this._dgPool.enqueueHead(new TcpDatagramBase());
      }

      this._tcpDgPropsPool = (Deque)(new Object());
      int var4 = 4;

      for (int i = 0; i < var4; i++) {
         this._tcpDgPropsPool.enqueueHead(new TcpDatagramProperties());
      }

      this._connectionAvailable = DataRecovery.getInstance().isConnectionAvailable();
      byte[] data = Branding.getData(12295);
      int flags = data != null && data.length > 0 ? data[0] & 0xFF : 0;
      this._isBlackBerryTrafficInvalid = InternalServices.isDeviceSecure() && !DeviceInfo.isSimulator() && (flags & 8) == 0 && RadioInfo.getNetworkType() != 6;
   }

   @Override
   public final Datagram newDatagram(byte[] buffer, int offset, int length, String address) {
      return new TcpDatagramBase(buffer, offset, length, address);
   }

   @Override
   protected final void superSend(Datagram datagram) {
      super.superSend(datagram);
      this.giveBackDatagram((TcpDatagramBase)datagram);
   }

   @Override
   public final void nativeSendVerify(DatagramAddressBase addressBase, Datagram datagram) {
      if (addressBase instanceof TcpAddress) {
         super._txAddressBase = (TcpAddress)addressBase;
      } else {
         if (datagram instanceof TcpDatagramBase) {
            TcpAddress tcpAddress = (TcpAddress)((TcpDatagramBase)datagram).getAddressBase();
            if (tcpAddress != null) {
               super._txAddressBase = tcpAddress;
               return;
            }
         }

         throw new Object("Bad address");
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void nativePreSend() {
      TcpAddress tcpAddress = (TcpAddress)super._txAddressBase;
      TCPPacketHeader header = (TCPPacketHeader)super._txHeader;
      String apn = tcpAddress.getApnName();
      int apnId;
      if (super._dataServicesEnabled || super._dataServicesMode == 3 && StringUtilities.strEqualIgnoreCase(apn, WLAN.WLAN_PSEUDO_APN, 1701707776)) {
         boolean var7 = false /* VF: Semaphore variable */;

         try {
            var7 = true;
            apnId = RadioInternal.registerAccessPointNumber(apn);
            var7 = false;
         } finally {
            if (var7) {
               EventLogger.logEvent(3177555665113652794L, 1413763891, 2);
               throw new Object();
            }
         }
      } else {
         apnId = -1;
      }

      header._accessPointNumber = apnId;
      super._txApnId = apnId;
   }

   @Override
   public final void nativeSendSetupHeader(Datagram datagram, IOProperties properties) {
      TCPPacketHeader header = (TCPPacketHeader)super._txHeader;
      TcpDatagramProperties tcpProps = ((TcpDatagramBase)datagram)._tcpProps;
      if (tcpProps == null) {
         throw new Object("Invalid datagram properties");
      }

      int length = datagram.getLength();
      TcpAddress tcpAddress = (TcpAddress)super._txAddressBase;
      if (length > super._maxPacketSize
         || tcpAddress.getIpAddress() == -1
         || tcpAddress.getDestPort() == -1
         || tcpAddress.getLocalPort() == -1
         || tcpAddress.getApnName().length() == 0 && (RadioInfo.getNetworkType() == 3 || RadioInfo.getNetworkType() == 7)) {
         if (length > super._maxPacketSize) {
            throw new Object("Bad length");
         } else if (tcpAddress.getApnName().length() != 0 || RadioInfo.getNetworkType() != 3 && RadioInfo.getNetworkType() != 7) {
            throw new Object("Bad address");
         } else {
            throw new Object("Bad APN");
         }
      } else {
         synchronized (this._sendLock) {
            DatagramAddressBase.writeInt(this._txAddressBytes, 0, tcpAddress.getIpAddress());
            header.setDestinationAddress(this._txAddressBytes);
            header.setSourcePort(tcpAddress.getLocalPort());
            header.setDestinationPort(tcpAddress.getDestPort());
            header._sequenceNumber = tcpProps._sequenceNumber;
            header._acknowledgementNumber = tcpProps._acknowledgementNumber;
            header._flags = tcpProps._flags;
            header._window = tcpProps._window;
            header._urgentPointer = tcpProps._urgentPointer;
            header._dataOffset = tcpProps._dataOffset;
            this._txRetryOnNoContext = properties == null ? false : properties.isFlagSet(1024);
            if (!this._txRetryOnNoContext && !this._connectionAvailable && this._isBlackBerryTrafficInvalid) {
               throw new Object();
            }
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
            return -3;
         case -2:
         case 0:
         case 1:
         case 2:
         case 129:
            return status;
         case 250:
            if (this._txRetryOnNoContext) {
               return -3;
            }
         default:
            return 12673;
         case 252:
            return -4;
         case 255:
            return 12674;
      }
   }

   private static final void parseTcpOptions(byte[] data, TcpDatagramProperties props) {
      boolean sack = false;
      int optionsLength = (props._dataOffset - 5) * 4;
      int index = 0;

      while (index < optionsLength) {
         int kind = data[index++];
         if (kind == 0) {
            break;
         }

         if (kind != 1) {
            if (index >= optionsLength) {
               break;
            }

            int length = data[index++];
            if (index + length - 2 > optionsLength) {
               break;
            }

            switch (kind) {
               case 1:
                  break;
               case 2:
               default:
                  if (length == 4) {
                     int mss = (data[index] & 255) << 8 | data[index + 1] & 255;
                     props._maxSegmentSize = mss;
                  }
                  break;
               case 3:
                  props._windowScale = data[index] & 255;
                  break;
               case 4:
                  if ((2 & props._flags) != 0) {
                     props._sackPermitted = true;
                     sack = true;
                  }
                  break;
               case 5:
                  int numSackBlocks = (length - 2) / 8;
                  int tempIndex = index;
                  SimpleTcpDataBlock[] sackBlockArray = new SimpleTcpDataBlock[numSackBlocks];

                  for (int i = 0; i < numSackBlocks; i++) {
                     int leftEdge = (data[tempIndex] & 255) << 24
                        | (data[tempIndex + 1] & 255) << 16
                        | (data[tempIndex + 2] & 255) << 8
                        | data[tempIndex + 3] & 255;
                     int rightEdge = (data[tempIndex + 4] & 255) << 24
                        | (data[tempIndex + 5] & 255) << 16
                        | (data[tempIndex + 6] & 255) << 8
                        | data[tempIndex + 7] & 255;
                     tempIndex += 8;
                     sackBlockArray[i] = new SimpleTcpDataBlock(leftEdge, rightEdge);
                  }

                  props._sackBlocks = sackBlockArray;
            }

            index += length - 2;
         }
      }

      if (!sack) {
         props._sackPermitted = false;
      }
   }

   @Override
   public final DatagramAddressBase newDatagramAddressBase(String address, boolean swap) {
      try {
         TcpAddress newAddress = new TcpAddress(address);
         if (swap) {
            newAddress.swap();
         }

         return newAddress;
      } finally {
         throw new Object("Bad address");
      }
   }
}
