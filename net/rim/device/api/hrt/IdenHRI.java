package net.rim.device.api.hrt;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.util.Persistable;

public final class IdenHRI extends HostRoutingInfo implements Persistable {
   public IdenHRI() {
      super._dac = new IPv4UdpDAC(16, 20);
      this.setArt(32);
      this.setPte(1);
      this.setApn("");
   }

   private IdenHRI(IdenHRI hri) {
      super(hri);
   }

   @Override
   public final HostRoutingInfo clone() {
      return new IdenHRI(this);
   }

   @Override
   public final long getNpcBase() {
      return 80;
   }

   @Override
   public final int getWirelessNetType() {
      return 5;
   }

   @Override
   public final void setArt(int art) {
      super._art = art & 32;
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
      return hri instanceof IdenHRI ? super._dac.hasEquivalentDestination(hri.getDac()) : false;
   }

   @Override
   public final void setPte(int pte) {
      super._pte = 1;
   }
}
