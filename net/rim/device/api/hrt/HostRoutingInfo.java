package net.rim.device.api.hrt;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;

public class HostRoutingInfo implements Persistable {
   protected boolean _dirty;
   protected String _name;
   protected long _npc;
   protected DAC _dac;
   protected DomainNameDAC _dal;
   protected int _art;
   protected int _pte;
   protected String _apn;
   protected String _apnUsername;
   protected String _apnPassword;
   public static final int MOBITEX_WIRELESS_NET_TYPE;
   public static final int GPRS_WIRELESS_NET_TYPE;
   public static final int CDMA_WIRELESS_NET_TYPE;
   public static final int IDEN_WIRELESS_NET_TYPE;
   public static final int WIFI_WIRELESS_NET_TYPE;
   public static final int ART_3GPP_GPRS;
   public static final int ART_3GPP_UMTS;
   public static final int ART_3GPP_GAN;
   public static final int ART_WLAN_DEFAULT;
   public static final int ART_CDMA_DEFAULT;
   public static final int ART_IDEN_DEFAULT;
   public static final int PTE_RESERVED;
   public static final int PTE_MDP_UDP_UNENCRYPTED;
   public static final int PTE_SSL_TCP_ENCRYPTED;
   public static final int PTE_SSL_TCP_UNENCRYPTED;
   public static final int PTE_IPSEC_UDP_VPN;
   public static final int PTE_TCP_UNENCRYPTED;
   public static final int PTE_IPSEC_TCP_VPN;

   protected HostRoutingInfo() {
   }

   protected HostRoutingInfo(HostRoutingInfo hri) {
      this._dirty = hri._dirty;
      this._name = hri._name;
      this._npc = hri._npc;
      this._dac = hri._dac.clone();
      this._apn = hri._apn;
      this._apnUsername = hri._apnUsername;
      this._apnPassword = hri._apnPassword;
      if (hri._dal != null) {
         if (hri._dal == hri._dac) {
            this._dal = (DomainNameDAC)this._dac;
         } else {
            this._dal = (DomainNameDAC)hri._dal.clone();
         }
      }

      this._art = hri._art;
      this._pte = hri._pte;
   }

   public boolean isDirty() {
      return this._dac != null ? this._dirty | this._dac.isDirty() : this._dirty | false;
   }

   public void setDirty(boolean flag) {
      if (this._dac != null) {
         this._dac.setDirty(flag);
      }

      this._dirty = flag;
   }

   public boolean isValid() {
      return this._dac != null ? this._dac.isValid() : false;
   }

   public HostRoutingInfo clone() {
      throw null;
   }

   public String getName() {
      return this._name;
   }

   public void setName(String s) {
      this._name = s;
      this._dirty = true;
   }

   public String getApn() {
      return this._apn;
   }

   public final void setApn(String s) {
      this._dirty = true;
      this._apn = s;
   }

   public final String getApnUsername() {
      return this._apnUsername;
   }

   public final void setApnUsername(String s) {
      this._dirty = true;
      this._apnUsername = s;
   }

   public final String getApnPassword() {
      return this._apnPassword;
   }

   public final void setApnPassword(String s) {
      this._dirty = true;
      this._apnPassword = s;
   }

   public long getNpc() {
      return this._npc;
   }

   public void setNpc(int npc) {
      this.setNpc(npc & 4294967295L);
   }

   public void setNpc(long npc) {
      this._npc = npc;
      this._dirty = true;
   }

   public int getWirelessNetType() {
      throw null;
   }

   public int getArt() {
      return this._art;
   }

   public void setArt(int _1) {
      throw null;
   }

   protected long getNpcBase() {
      throw null;
   }

   public DAC getDac() {
      return this._dac;
   }

   public void setDac(DAC d) {
      this._dac = d;
      this._dirty = true;
   }

   public DatagramAddressBase getAddressBase() {
      return this._dac != null ? this.getAddressBase(this._dac.getNextCodeIndex()) : null;
   }

   public DatagramAddressBase getAddressBase(int _1) {
      throw null;
   }

   public boolean rcvdFromAddress(DatagramAddressBase addr) {
      int index = this._dac.rcvdFromAddress(addr);
      if (index != -1) {
         this._dac.rcvFromIndex(index);
         return true;
      } else {
         return false;
      }
   }

   public boolean handleSendError(int count) {
      return this._dac.handleSendError(count);
   }

   public void sendSuccessful() {
      this._dac.sendSuccessful();
   }

   public void setDal(DomainNameDAC dal) {
      this._dal = dal;
      this._dirty = true;
   }

   public DomainNameDAC getDal() {
      return this._dal;
   }

   public String[] getDalHosts() {
      return this._dal != null ? this._dal.getHosts() : null;
   }

   public int[] getDalDestPorts() {
      return this._dal != null ? this._dal.getDstPorts() : null;
   }

   public int[] getDalSrcPorts() {
      return this._dal != null ? this._dal.getSrcPorts() : null;
   }

   public void setPte(int _1) {
      throw null;
   }

   public int getPte() {
      return this._pte;
   }

   public boolean parseField(int type, int length, DataBuffer b) {
      switch (type) {
         case 1:
            b.readUnsignedByte();
            return true;
         case 2:
            this.setName(StringUtilities.cStr2String(b.getArray(), b.getArrayPosition(), length));
            b.skipBytes(length);
            return true;
         case 21:
            this.setPte(b.readInt());
            return true;
         case 23:
            DomainNameDAC dal = new DomainNameDAC();
            dal.setAddresses(StringUtilities.cStr2String(b.getArray(), b.getArrayPosition(), length));
            this.setDal(dal);
            return true;
         case 33:
            this.setNpc(this._npc | (long)b.readInt() << 32);
            return true;
         default:
            return this._dac.parseField(type, length, b);
      }
   }

   public boolean hasEquivalentDestination(HostRoutingInfo _1) {
      throw null;
   }

   protected void cloneInto(HostRoutingInfo hri) {
      hri._dirty = this._dirty;
      hri._name = this._name;
      hri._npc = this._npc;
      hri._dac = this._dac.clone();
   }
}
