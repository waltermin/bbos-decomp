package net.rim.device.api.io;

import net.rim.device.api.system.MobitexPacketHeader;
import net.rim.device.api.system.UnsupportedOperationException;

public final class MobitexAddress extends DatagramAddressBase {
   public static final int TYPE_TEXT = 1;
   public static final int TYPE_DATA = 2;
   public static final int TYPE_STATUS = 3;
   public static final int TYPE_HPDATA = 4;

   public MobitexAddress() {
      throw new UnsupportedOperationException();
   }

   public MobitexAddress(int man, int type, int hpid) {
      this();
   }

   public MobitexAddress(int man, int type, int hpid, MobitexPacketHeader header) {
      this();
   }

   public MobitexAddress(DatagramAddressBase addressBase) {
      this();
   }

   public MobitexAddress(String address) {
      this();
   }

   public final MobitexPacketHeader getPacketHeader() {
      return null;
   }

   public final void setPacketHeader(MobitexPacketHeader mph) {
   }

   public final int getMan() {
      return 0;
   }

   public final int getType() {
      return 0;
   }

   public final int getHpid() {
      return 0;
   }

   @Override
   public final void setAddress(String address) {
   }

   @Override
   public final String getAddress() {
      return null;
   }

   @Override
   public final boolean equals(Object addressBase) {
      return false;
   }

   @Override
   public final int hashCode() {
      return 0;
   }

   public static final String makeAddress(boolean open, int man, int type, int hpid) {
      throw new UnsupportedOperationException();
   }

   public static final void appendAddress(StringBuffer buf, boolean open, int man, int type, int hpid) {
      throw new UnsupportedOperationException();
   }
}
