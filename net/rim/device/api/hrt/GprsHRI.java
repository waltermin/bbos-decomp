package net.rim.device.api.hrt;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.system.GPRSQOSInfo;
import net.rim.device.api.system.QOSInfo;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;

public final class GprsHRI extends HostRoutingInfo implements Persistable {
   private GPRSQOSInfo _qos;

   public GprsHRI() {
      super._dac = new IPv4UdpDAC(16, 20);
      this._qos = (GPRSQOSInfo)(new Object());
      this.setArt(7);
      this.setPte(1);
   }

   private GprsHRI(GprsHRI hri) {
      super(hri);
      this._qos = (GPRSQOSInfo)(new Object(hri._qos));
   }

   @Override
   public final HostRoutingInfo clone() {
      return new GprsHRI(this);
   }

   @Override
   public final int getWirelessNetType() {
      return 3;
   }

   @Override
   public final void setArt(int art) {
      super._art = art & 7;
   }

   @Override
   public final long getNpcBase() {
      return 48;
   }

   @Override
   public final void setDac(DAC d) {
      if (d instanceof IPv4UdpDAC) {
         super.setDac(d);
      } else {
         throw new Object();
      }
   }

   @Override
   public final void setPte(int pte) {
      super._pte = 1;
   }

   public final QOSInfo getQos() {
      return (QOSInfo)(new Object(this._qos));
   }

   public final void setQos(QOSInfo qos) {
      if (qos instanceof Object) {
         this._qos = (GPRSQOSInfo)(new Object((GPRSQOSInfo)qos));
         super._dirty = true;
      } else {
         throw new Object();
      }
   }

   @Override
   public final boolean parseField(int type, int length, DataBuffer b) {
      switch (type) {
         case 18:
            this.setApn(StringUtilities.cStr2String(b.getArray(), b.getArrayPosition(), length));
            b.skipBytes(length);
            return true;
         case 19:
            if (length != 5) {
               throw new Object();
            }

            GPRSQOSInfo qos = (GPRSQOSInfo)(new Object(
               b.readUnsignedByte(), b.readUnsignedByte(), b.readUnsignedByte(), b.readUnsignedByte(), b.readUnsignedByte()
            ));
            this._qos = qos;
            return true;
         case 34:
            this.setApnUsername(StringUtilities.cStr2String(b.getArray(), b.getArrayPosition(), length));
            b.skipBytes(length);
            return true;
         case 35:
            this.setApnPassword(StringUtilities.cStr2String(b.getArray(), b.getArrayPosition(), length));
            b.skipBytes(length);
            return true;
         default:
            return super.parseField(type, length, b);
      }
   }

   @Override
   public final DatagramAddressBase getAddressBase(int index) {
      if (super._dac == null) {
         return null;
      }

      IPv4UdpDAC ipDac = (IPv4UdpDAC)super._dac;
      long[] addrs = ipDac.getAddresses();
      if (addrs == null) {
         return null;
      }

      long addr = addrs[index];
      return (DatagramAddressBase)(new Object(IPv4UdpDAC.addr2IpAddress(addr), IPv4UdpDAC.addr2DstPort(addr), IPv4UdpDAC.addr2SrcPort(addr), super._apn, 2));
   }

   @Override
   public final boolean hasEquivalentDestination(HostRoutingInfo hri) {
      if (hri instanceof GprsHRI) {
         GprsHRI ghri = (GprsHRI)hri;
         if (StringUtilities.strEqualIgnoreCase(super._apn, ghri._apn)) {
            return super._dac.hasEquivalentDestination(ghri.getDac());
         }
      }

      return false;
   }
}
