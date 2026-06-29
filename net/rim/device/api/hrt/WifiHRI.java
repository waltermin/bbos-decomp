package net.rim.device.api.hrt;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;

public final class WifiHRI extends HostRoutingInfo implements Persistable {
   public WifiHRI() {
      this.setDal(new DomainNameDAC());
      super._dac = this.getDal();
      this.setName("Wi-Fi");
      this.setNpc((long)96);
      this.setArt(8);
      this.setPte(2);
   }

   private WifiHRI(WifiHRI hri) {
      super(hri);
   }

   @Override
   public final HostRoutingInfo clone() {
      return new WifiHRI(this);
   }

   @Override
   public final int getWirelessNetType() {
      return 6;
   }

   @Override
   public final void setArt(int art) {
      super._art = art & 8;
   }

   @Override
   public final long getNpcBase() {
      return 96;
   }

   @Override
   public final DAC getDac() {
      return this.getDal();
   }

   @Override
   public final void setDac(DAC d) {
      if (d instanceof DomainNameDAC) {
         super.setDal((DomainNameDAC)d);
         super.setDac(d);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final DatagramAddressBase getAddressBase(int index) {
      return null;
   }

   @Override
   public final boolean parseField(int type, int length, DataBuffer b) {
      return super.parseField(type, length, b);
   }

   @Override
   public final boolean hasEquivalentDestination(HostRoutingInfo hri) {
      return hri instanceof WifiHRI ? super._dal.hasEquivalentDestination(hri.getDal()) : false;
   }

   @Override
   public final void setPte(int pte) {
      if (pte != 2 && pte != 3 && pte != 4 && pte != 5 && pte != 6) {
         super._pte = 2;
      } else {
         super._pte = pte;
      }
   }
}
