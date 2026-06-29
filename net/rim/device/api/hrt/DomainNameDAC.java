package net.rim.device.api.hrt;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.UdpAddress;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.Persistable;

public final class DomainNameDAC extends DAC implements Persistable {
   private String[] _hosts;
   private int[] _dstPorts;
   private int[] _srcPorts;

   @Override
   public final boolean isValid() {
      return this._hosts != null
         && this._hosts.length > 0
         && this._dstPorts != null
         && this._dstPorts.length == this._hosts.length
         && this._srcPorts != null
         && this._srcPorts.length == this._hosts.length;
   }

   @Override
   public final DAC clone() {
      DomainNameDAC d = new DomainNameDAC();
      if (this._hosts != null) {
         d._hosts = new String[this._hosts.length];

         for (int i = this._hosts.length - 1; i >= 0; i--) {
            d._hosts[i] = this._hosts[i];
         }
      }

      d._dstPorts = Arrays.copy(this._dstPorts);
      d._srcPorts = Arrays.copy(this._srcPorts);
      this.cloneInto(d);
      return d;
   }

   @Override
   public final int getNumCodes() {
      return this._hosts != null ? this._hosts.length : 0;
   }

   public final String[] getHosts() {
      return this._hosts;
   }

   public final int[] getDstPorts() {
      return this._dstPorts;
   }

   public final int[] getSrcPorts() {
      return this._srcPorts;
   }

   public final void setAddresses(String dal) {
      if (dal != null) {
         this._hosts = getDalHosts(dal);
         this._dstPorts = getDalDestPorts(dal);
         this._srcPorts = this.getDalSrcPorts(dal);
      }
   }

   public final String[] getAddresses() {
      if (this._hosts != null) {
         String[] addrs = new String[this._hosts.length];

         for (int i = this._hosts.length - 1; i >= 0; i--) {
            StringBuffer strBuf = new StringBuffer();
            strBuf.append(this._hosts[i]);
            if (this._dstPorts[i] >= 0) {
               strBuf.append(':');
               strBuf.append(Integer.toString(this._dstPorts[i], 10));
               if (this._srcPorts[i] >= 0) {
                  strBuf.append(':');
                  strBuf.append(Integer.toString(this._srcPorts[i], 10));
               }
            }

            addrs[i] = strBuf.toString();
         }

         return addrs;
      } else {
         return null;
      }
   }

   @Override
   public final void appendAddressString(StringBuffer strBuf, int index) {
      if (this._hosts != null && index >= 0 && index < this._hosts.length) {
         String name = this._hosts[index];
         long dstPort = this._dstPorts[index];
         long srcPort = this._srcPorts[index];
         strBuf.append(name);
         strBuf.append(':');
         NumberUtilities.appendNumber(strBuf, dstPort, 16);
         strBuf.append(':');
         if (srcPort != 0) {
            NumberUtilities.appendNumber(strBuf, srcPort, 16);
         }
      }
   }

   @Override
   public final int rcvdFromAddress(String addr, int start, int length) {
      String[] dalHosts = getDalHosts(addr.substring(start, start + length));
      if (dalHosts != null && this._hosts != null) {
         for (int i = 0; i <= this._hosts.length; i++) {
            if (this._hosts[i].equalsIgnoreCase(dalHosts[0])) {
               return i;
            }
         }
      }

      return -1;
   }

   @Override
   public final int rcvdFromAddress(DatagramAddressBase addr) {
      if (!(addr instanceof UdpAddress)) {
         throw new IllegalArgumentException();
      }

      UdpAddress udpAddress = (UdpAddress)addr;
      String ipAddrStr = ipAddr2String(udpAddress.getIpAddressInt());

      for (int i = 0; i < this._hosts.length; i++) {
         if (this._hosts[i].equalsIgnoreCase(ipAddrStr)) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public final boolean hasEquivalentDestination(DAC dac) {
      if (dac instanceof DomainNameDAC) {
         DomainNameDAC dnd = (DomainNameDAC)dac;
         int index = dnd.getNextCodeIndex();
         String host = dnd._hosts[index];
         int destPort = dnd._dstPorts[index];
         int srcPort = dnd._srcPorts[index];

         for (int i = this._hosts.length - 1; i >= 0; i--) {
            if (this._hosts[i].equalsIgnoreCase(host) && this._dstPorts[i] == destPort && this._srcPorts[i] == srcPort) {
               return true;
            }
         }
      }

      return false;
   }

   public static final String[] getDalHosts(String dal) {
      String[] dalHosts = new String[0];
      if (dal != null) {
         int idx = 0;

         while (true) {
            int colonIdx = dal.indexOf(58, idx);
            if (colonIdx > idx) {
               Arrays.add(dalHosts, dal.substring(idx, colonIdx));
            } else {
               Arrays.add(dalHosts, dal.substring(idx, dal.length()));
            }

            idx = dal.indexOf(44, idx);
            if (idx < 0) {
               return dalHosts;
            }

            idx++;
         }
      } else {
         return dalHosts;
      }
   }

   public static final int[] getDalDestPorts(String dal) {
      int[] dalDestPorts = new int[0];
      if (dal == null) {
         return dalDestPorts;
      }

      int idx = 0;

      while (true) {
         int destPort = -1;
         int colonIdx = dal.indexOf(58, idx);
         int newIdx = dal.indexOf(44, idx);
         if (colonIdx >= 0) {
            int secondColonIdx = colonIdx + 1 < dal.length() ? dal.indexOf(58, colonIdx + 1) : -1;
            int endIndex;
            if (secondColonIdx < 0 && newIdx < 0) {
               endIndex = dal.length();
            } else if (secondColonIdx >= 0 && newIdx < 0) {
               endIndex = secondColonIdx;
            } else if (secondColonIdx < 0 && newIdx >= 0) {
               endIndex = newIdx;
            } else if (secondColonIdx > newIdx) {
               endIndex = newIdx;
            } else {
               endIndex = secondColonIdx;
            }

            label73:
            try {
               destPort = Integer.valueOf(dal.substring(colonIdx + 1, endIndex));
            } finally {
               break label73;
            }
         }

         Arrays.add(dalDestPorts, destPort);
         if (newIdx < 0) {
            return dalDestPorts;
         }

         idx = newIdx + 1;
      }
   }

   public final int[] getDalSrcPorts(String dal) {
      int[] dalSrcPorts = new int[0];
      if (dal == null) {
         return dalSrcPorts;
      }

      int idx = 0;

      while (true) {
         int srcPort = -1;
         int colonIdx = dal.indexOf(58, idx);
         int newIdx = dal.indexOf(44, idx);
         if (colonIdx >= 0) {
            int secondColonIdx = colonIdx + 1 < dal.length() ? dal.indexOf(58, colonIdx + 1) : -1;

            label66:
            try {
               if (secondColonIdx >= 0 && newIdx < 0) {
                  srcPort = Integer.valueOf(dal.substring(secondColonIdx + 1, dal.length()));
               } else if (secondColonIdx >= 0 && newIdx >= 0 && secondColonIdx < newIdx) {
                  srcPort = Integer.valueOf(dal.substring(secondColonIdx + 1, newIdx));
               }
            } finally {
               break label66;
            }
         }

         Arrays.add(dalSrcPorts, srcPort);
         if (newIdx < 0) {
            return dalSrcPorts;
         }

         idx = newIdx + 1;
      }
   }

   public static final String ipAddr2String(int ipAddr) {
      StringBuffer strBuf = new StringBuffer(15);

      int shift;
      for (shift = 24; shift > 0; shift -= 8) {
         int s = ipAddr >>> shift & 0xFF;
         strBuf.append(Integer.toString(s, 10));
         strBuf.append('.');
      }

      strBuf.append(Long.toString(ipAddr >>> shift & 0xFF, 10));
      return strBuf.toString();
   }
}
