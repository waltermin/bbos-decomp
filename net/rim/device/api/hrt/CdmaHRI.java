package net.rim.device.api.hrt;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.UdpAddress;
import net.rim.device.api.system.Branding;
import net.rim.device.api.util.Persistable;

public final class CdmaHRI extends HostRoutingInfo implements Persistable {
   private static final String CDMA_DEFAULT_APN = "";

   public CdmaHRI() {
      super._dac = new IPv4UdpDAC(16, 20);
      this.setArt(16);
      this.setPte(1);
      this.setApn("");
   }

   private CdmaHRI(CdmaHRI hri) {
      super(hri);
   }

   @Override
   public final HostRoutingInfo clone() {
      return new CdmaHRI(this);
   }

   @Override
   public final int getWirelessNetType() {
      return 4;
   }

   @Override
   public final void setArt(int art) {
      super._art = art & 16;
   }

   @Override
   public final String getApn() {
      if (HRUtils.isWorldPhone() && HRUtils.isPretendCDMA() && "".equals(super._apn)) {
         byte[] data = Branding.getData(13824);
         if (data != null) {
            return new String(data);
         }
      }

      return super._apn;
   }

   @Override
   public final long getNpcBase() {
      return 64;
   }

   @Override
   public final void setDac(DAC d) {
      if (d instanceof IPv4UdpDAC) {
         super.setDac(d);
      } else {
         throw new IllegalArgumentException();
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
      return new UdpAddress(IPv4UdpDAC.addr2IpAddress(addr), IPv4UdpDAC.addr2DstPort(addr), IPv4UdpDAC.addr2SrcPort(addr), this.getApn(), 2);
   }

   @Override
   public final boolean hasEquivalentDestination(HostRoutingInfo hri) {
      return hri instanceof CdmaHRI ? super._dac.hasEquivalentDestination(hri.getDac()) : false;
   }

   @Override
   public final void setPte(int pte) {
      super._pte = 1;
   }
}
