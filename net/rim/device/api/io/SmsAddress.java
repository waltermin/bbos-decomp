package net.rim.device.api.io;

import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public final class SmsAddress extends DatagramAddressBase {
   private SMSPacketHeader _header;
   private boolean _statusReportSpecified;
   private boolean _userHeaderSpecified;
   private int[] _ports;
   public static final byte ORIGINATOR_PORT_INDEX = 1;
   public static final byte DESTINATION_PORT_INDEX = 0;
   public static final int PORT_NBS_VCARD = 226;
   public static final int PORT_NBS_VCALENDAR = 228;
   public static final int PORT_NBS_RING_TONE = 5505;
   public static final int PORT_NBS_SIMPLE_EMAIL = 5512;
   public static final int PORT_WAP_PUSH = 2948;
   public static final int PORT_GCMP = 65536;
   public static final int PORT_KODIAK_NETWORKS_WV = 65552;

   public SmsAddress() {
      this._header = new SMSPacketHeader();
   }

   public SmsAddress(SMSPacketHeader header, int[] ports) {
      this.init(header, ports);
   }

   public SmsAddress(DatagramAddressBase addressBase) {
      this._header = new SMSPacketHeader();
      if (!(addressBase instanceof SmsAddress)) {
         this.setAddress(addressBase.getAddress());
      } else {
         SMSPacketHeader header = ((SmsAddress)addressBase)._header;
         this._header.setPeerAddress(header.getPeerAddress());
         this._header.setSCAddress(header.getSCAddress());
         this._header.setCallbackAddress(header.getCallbackAddress());
         this._header.setProtocolId(header.getProtocolId());
         this._header.setMessageCoding(header.getMessageCoding());
         this._header.setMessageClass(header.getMessageClass());
         this._header.setPrivacy(header.getPrivacy());
         this._header.setPriority(header.getPriority());
         this._header.setLanguage(header.getLanguage());
         this._header.setStatusReportRequest(header.getStatusReportRequest());
         this._header.setUserDataHeaderPresent(header.isUserDataHeaderPresent());
         this._header.setValidityPeriod(header.getValidityPeriod());
         this._header.setDeliveryPeriod(header.getDeliveryPeriod());
         this._header.setMessageWaitingType(header.getMessageWaitingType());
         this._header.setNumMessages(header.getNumMessages());
         this._statusReportSpecified = ((SmsAddress)addressBase)._statusReportSpecified;
         this._userHeaderSpecified = ((SmsAddress)addressBase)._userHeaderSpecified;
         if (((SmsAddress)addressBase)._ports != null) {
            this._ports = Arrays.copy(((SmsAddress)addressBase)._ports);
            return;
         }
      }
   }

   public SmsAddress(String address) {
      this._header = new SMSPacketHeader();
      this.setAddress(address);
   }

   private final void init(SMSPacketHeader header, int[] ports) {
      this._header = header != null ? header : new SMSPacketHeader();
      this._statusReportSpecified = true;
      this._userHeaderSpecified = true;
      if (ports != null) {
         this._ports = Arrays.copy(ports);
      }
   }

   public final SMSPacketHeader getHeader() {
      return this._header;
   }

   public final int[] getPorts() {
      return this._ports;
   }

   @Override
   public final void setAddress(String address) {
      int length = address.length();
      this._header.reset();
      if (!address.startsWith("//")) {
         throw new IllegalArgumentException(address);
      }

      super._address = address;
      if (address.length() != 2) {
         int delim = address.indexOf(58);
         if (delim == -1) {
            delim = address.indexOf(59);
            if (delim == -1) {
               this._header.setPeerAddress(address.substring(2));
            } else {
               this._header.setPeerAddress(address.substring(2, delim));
               int offset = delim + 1;
            }
         } else {
            this._header.setPeerAddress(address.substring(2, delim));
            int offset = delim + 1;
            if (offset < length) {
               int portsEnd = address.indexOf(59, offset);
               if (portsEnd == -1) {
                  portsEnd = length;
               }

               int port = 0;

               while (offset < portsEnd) {
                  delim = address.indexOf(124, offset);
                  if (delim == -1 || delim > portsEnd) {
                     delim = portsEnd;
                  }

                  if (port == 0) {
                     this._ports = new int[1];
                  } else {
                     Array.resize(this._ports, port + 1);
                  }

                  this._ports[port++] = DatagramAddressBase.parseInt(address, offset, delim, 10);
                  offset = delim + 1;
               }
            }
         }
      }
   }

   @Override
   public final String getAddress() {
      if (super._address == null) {
         super._address = makeAddress(false, this._header, this._ports);
      }

      return super._address;
   }

   @Override
   public final boolean equals(Object addressBase) {
      if (addressBase == this) {
         return true;
      }

      if (!(addressBase instanceof SmsAddress)) {
         return false;
      }

      SmsAddress address = (SmsAddress)addressBase;
      String myPeerAddress = this._header.getPeerAddress();
      String theirPeerAddress = address._header.getPeerAddress();
      return myPeerAddress != null
            && myPeerAddress.length() != 0
            && theirPeerAddress != null
            && theirPeerAddress.length() != 0
            && !myPeerAddress.equals(theirPeerAddress)
         ? false
         : this.matchPort(address);
   }

   @Override
   public final int hashCode() {
      int hash = 7;
      String peer = this._header.getPeerAddress();
      if (peer != null) {
         hash = 31 * hash + peer.hashCode();
      }

      if (this._ports != null) {
         for (int i = this._ports.length - 1; i >= 0; i--) {
            hash = 31 * hash + this._ports[i];
         }
      }

      return hash;
   }

   private final boolean matchPort(SmsAddress address) {
      if (this._ports == null) {
         return true;
      }

      if (address._ports != null) {
         for (int i = this._ports.length - 1; i >= 0; i--) {
            if (this._ports[i] == address._ports[0]) {
               return true;
            }
         }
      }

      return false;
   }

   public static final String makeAddress(boolean open, SMSPacketHeader header, int[] ports) {
      StringBuffer buf = new StringBuffer();
      if (header == null) {
         header = new SMSPacketHeader();
      }

      appendAddress(buf, open, header.getPeerAddress(), ports);
      return buf.toString();
   }

   private static final void appendAddress(StringBuffer buf, boolean open, String peerAddress, int[] ports) {
      if (open) {
         buf.append("sms:");
      }

      buf.append("//");
      if (peerAddress != null) {
         buf.append(peerAddress);
      }

      if (ports != null) {
         for (int i = 0; i < ports.length; i++) {
            if (i == 0) {
               buf.append(':');
            } else {
               buf.append('|');
            }

            buf.append(ports[i]);
         }
      }
   }
}
