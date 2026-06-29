package net.rim.device.cldc.io.udp;

import java.io.IOException;
import javax.microedition.io.Datagram;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.IOFormatException;
import net.rim.device.api.io.IOProperties;
import net.rim.device.api.io.UdpAddress;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioException;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.UDPPacketHeader;
import net.rim.device.api.system.UDPPacketListener;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.datarecovery.DataRecovery;
import net.rim.device.cldc.io.datarecovery.DataRecoveryListener;
import net.rim.device.cldc.io.nativebase.NativeTransport;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.system.VoiceDataUsage;

public final class Transport extends NativeTransport implements UDPPacketListener, DataRecoveryListener {
   private byte[] _txAddress = new byte[]{0, 0, 0, 0};
   private byte[] _txEncode;
   private boolean _txRetryOnNoContext;
   private boolean _connectionAvailable;
   private boolean _isBlackBerryTrafficInvalid;

   public final Datagram newMidletDatagram(byte[] buffer, int offset, int length, String address) {
      return new MIDletDatagram(buffer, offset, length, address);
   }

   @Override
   public final void packetReceived(UDPPacketHeader header, byte[] data) {
      EventLogger.logEvent(super.GUID, 1381528436, 4);
      byte[] ipAddress = header.getSourceAddress();
      int destPort = header.getDestinationPort();
      int srcPort = header.getSourcePort();

      String apn;
      try {
         apn = RadioInfo.getAccessPointName(header.getAccessPointNumber());
      } catch (RadioException e) {
         EventLogger.logEvent(super.GUID, 1380278640, 2);
         return;
      }

      int type = 1;
      DatagramBase dgram = new DatagramBase(data, 0, data.length, new UdpInternalAddress(ipAddress, destPort, srcPort, apn, type, data));
      EventLogger.logEvent(super.GUID, 1381527669, 5);
      if (this.passUpDatagram(dgram)) {
         if (!super._itPolicyEnabled) {
            VoiceDataUsage.addDataBytes(data.length);
            return;
         }
      } else {
         EventLogger.logEvent(super.GUID, 1381527152, 3);
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
   public final void nativePreSend() throws IOException {
      UdpAddress addressBase = (UdpAddress)super._txAddressBase;
      UDPPacketHeader header = (UDPPacketHeader)super._txHeader;
      String apn = addressBase.getApn();
      int apnId;
      if (super._dataServicesEnabled || super._dataServicesMode == 3 && StringUtilities.strEqualIgnoreCase(apn, WLAN.WLAN_PSEUDO_APN, 1701707776)) {
         try {
            apnId = RadioInternal.registerAccessPointNumber(
               apn, 0, apn == null ? 0 : apn.length(), null, null, addressBase.getApnUsername(), addressBase.getApnPassword()
            );
         } catch (RadioException e) {
            EventLogger.logEvent(super.GUID, 1413833072, 2);
            this.xmitDgslEvent(super._txListener, super._txDgramId, 12689, null);
            throw new IOException();
         }

         try {
            RadioInternal.registerPort(17, 1, header.getSourcePort(), apnId);
         } catch (RadioException e) {
            EventLogger.logEvent(super.GUID, 1413836914, 2);
            this.xmitDgslEvent(super._txListener, super._txDgramId, 12691, null);
            throw new IOException();
         }
      } else {
         apnId = -1;
      }

      header.setAccessPointNumber(apnId);
      super._txApnId = apnId;
   }

   @Override
   public final void nativeSendSetupHeader(Datagram datagram, IOProperties properties) throws IOException {
      UdpAddress addressBase = (UdpAddress)super._txAddressBase;
      UDPPacketHeader header = (UDPPacketHeader)super._txHeader;
      header.reset();
      DatagramAddressBase.writeInt(this._txAddress, 0, addressBase.getIpAddressInt());
      header.setDestinationAddress(this._txAddress);
      int srcPort = addressBase.getSrcPort();
      int destPort = addressBase.getDestPort();
      if (srcPort != -1) {
         header.setSourcePort(srcPort);
      } else {
         header.setSourcePort(destPort);
      }

      header.setDestinationPort(destPort);
      this._txRetryOnNoContext = properties == null ? false : properties.isFlagSet(1024);
      if (!this._txRetryOnNoContext && !this._connectionAvailable && this._isBlackBerryTrafficInvalid) {
         throw new IOException();
      }
   }

   @Override
   public final void nativeSendSetupData(Datagram datagram) throws IOFormatException {
      int type = ((UdpAddress)super._txAddressBase).getType();
      int gpakHostAddress = 0;
      switch (type) {
         case 2:
            if (super._txAddressBase instanceof UdpInternalAddress) {
               gpakHostAddress = ((UdpInternalAddress)super._txAddressBase).getGpakHostAddress();
            }
         case 4:
            try {
               super._txLength = GpakUtil.encode(
                  (byte)(type == 4 ? 16 : 8), this._txEncode, datagram.getData(), datagram.getOffset(), datagram.getLength(), gpakHostAddress
               );
               super._txData = this._txEncode;
               super._txOffset = 0;
               return;
            } catch (IndexOutOfBoundsException e) {
               EventLogger.logEvent(super.GUID, 1413834351, 2);
               this.xmitDgslEvent(super._txListener, super._txDgramId, 12674, null);
               throw new IOFormatException();
            }
         default:
            super._txData = datagram.getData();
            super._txOffset = datagram.getOffset();
            super._txLength = datagram.getLength();
      }
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

   @Override
   public final void nativePostSend() {
      if (super._txData == this._txEncode) {
         Arrays.fill(this._txEncode, (byte)0);
      }

      super._txHeader.reset();
      super.nativePostSend();
   }

   @Override
   public final DatagramAddressBase newDatagramAddressBase(String address, boolean swap) {
      UdpInternalAddress ret = new UdpInternalAddress(address);
      if (swap) {
         ret.swap();
      }

      return ret;
   }

   @Override
   public final DatagramAddressBase newDatagramAddressBase(DatagramAddressBase addressBase, boolean swap) {
      UdpInternalAddress ret = new UdpInternalAddress(addressBase);
      if (swap) {
         ret.swap();
      }

      return ret;
   }

   @Override
   public final void nativeInit() {
      super._networkServiceMask = 4;
      super._maxPacketSize = UDPPacketHeader.getMaxPacketSize();
      super._txHeader = new UDPPacketHeader();
      this._txEncode = new byte[super._maxPacketSize];
      DataRecovery.getInstance().addListener(this);
   }

   public Transport() {
      super.GUID = -832245984976358184L;
      super.STR = "net.rim.udp";
      super.SEND_BACKOFF_DEF = 3000;
      byte[] data = Branding.getData(12295);
      int flags = data != null && data.length > 0 ? data[0] & 0xFF : 0;
      this._isBlackBerryTrafficInvalid = InternalServices.isDeviceSecure() && !DeviceInfo.isSimulator() && (flags & 8) == 0;
      super._generateTransmittingStatusEvent = !DeviceInfo.isSimulator()
         && RadioInfo.getNetworkType() == 3
         && (InternalServices.getOSAPIVersion() & -65536) >= 1048576;
   }

   @Override
   public final void nativeSendVerify(DatagramAddressBase addressBase, Datagram datagram) throws IOFormatException {
      UdpAddress addr = null;
      if (addressBase instanceof UdpAddress) {
         addr = (UdpAddress)addressBase;
      }

      if (addr == null) {
         addr = new UdpAddress(datagram.getAddress());
      }

      if (datagram.getLength() <= super._maxPacketSize && addr.getIpAddressInt() != -1 && addr.getDestPort() != -1 && addr.getApn() != null) {
         super._txAddressBase = addr;
      } else {
         EventLogger.logEvent(super.GUID, 1413834351, 2);
         this.xmitDgslEvent(super._txListener, super._txDgramId, 12674, null);
         throw new IOFormatException();
      }
   }
}
