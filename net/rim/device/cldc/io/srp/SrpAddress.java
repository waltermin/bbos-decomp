package net.rim.device.cldc.io.srp;

import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.util.StringUtilities;

final class SrpAddress extends DatagramAddressBase {
   private int _hostAddress = -1;
   private int _hostPort = -1;
   private int _localPort = -1;
   private boolean _resolved;
   private int _linkType = -1;
   private int _connectionType = -1;

   SrpAddress(SrpAddress addressBase) {
      this((DatagramAddressBase)addressBase);
   }

   SrpAddress(DatagramAddressBase addressBase) {
      if (!(addressBase instanceof SrpAddress)) {
         this.setAddress(addressBase.getAddress());
      } else {
         SrpAddress addr = (SrpAddress)addressBase;
         this.setAddress(addr.getAddress());
         this.setLinkType(addr.getLinkType());
         this.setConnectionType(addr.getConnectionType());
      }
   }

   SrpAddress(String address) {
      this(address, -1, -1);
   }

   SrpAddress(String address, int linkType, int connectionType) {
      this.setAddress(address);
      if (this.getLinkType() == -1) {
         this.setLinkType(linkType);
      }

      if (this.getConnectionType() == -1) {
         this.setConnectionType(connectionType);
      }
   }

   @Override
   public final void setAddress(String address) {
      super.setAddress(address);
      int delim = 0;
      int length = super._address != null ? super._address.length() : 0;
      int ipAddress = -1;
      int addressFQDN = -1;
      this._resolved = false;
      this._hostPort = this._hostAddress = this._localPort = -1;
      if (length >= 2 && super._address.charAt(0) == '/' && super._address.charAt(1) == '/') {
         addressFQDN = DatagramAddressBase.indexOfNextDelim(super._address, 2);
         if (!DatagramAddressBase.isDomainName(super._address, 2, addressFQDN)) {
            if (addressFQDN > 2) {
               ipAddress = DatagramAddressBase.parseIpAddressInt(super._address, 2);
               if (ipAddress == -1) {
                  throw new IllegalArgumentException("Invalid IP_ADDRESS");
               }

               this._hostAddress = ipAddress;
               this._resolved = true;
            } else if (super._address.charAt(addressFQDN) == ':') {
               throw new IllegalArgumentException("Illegal SERVER_ADDRESS");
            }
         } else {
            this._resolved = false;
         }

         delim = addressFQDN;
      }

      if (length > delim && super._address.charAt(delim) == ':') {
         int scan = delim + 1;
         delim = DatagramAddressBase.indexOfNextDelim(super._address, scan);
         if (delim <= scan) {
            throw new IllegalArgumentException("Bad DEST_PORT");
         }

         int ret = DatagramAddressBase.parseInt(super._address, scan, delim, 10);
         if (ret < 0 || ret > 65535) {
            throw new IllegalArgumentException("Invalid DEST_PORT");
         }

         this._hostPort = ret;
      }

      if (length > delim && super._address.charAt(delim) == ';') {
         int scan = delim + 1;
         delim = DatagramAddressBase.indexOfNextDelim(super._address, scan);
         if (delim > scan) {
            label153:
            try {
               int ret = DatagramAddressBase.parseInt(super._address, scan, delim, 10);
               if (ret < 0 || ret > 65535) {
                  throw new IllegalArgumentException("Invalid SRC_PORT");
               }

               this._localPort = ret;
            } finally {
               break label153;
            }
         }
      }

      int index = -1;
      if (super._address != null) {
         index = super._address.indexOf("//");
         if (index >= 0) {
            super._address = super._address.substring(index + 2, super._address.length());
         }

         int var18 = -1;
         if ((var18 = super._address.indexOf(";interface=wifi")) >= 0) {
            this.setLinkType(0);
         } else if ((var18 = super._address.indexOf(";interface=cellular")) >= 0) {
            this.setLinkType(1);
         }

         if (var18 >= 0) {
            int nextIndex = super._address.indexOf(59, var18 + 1);
            if (nextIndex >= var18) {
               super._address = super._address.substring(0, var18) + super._address.substring(nextIndex);
            } else {
               super._address = super._address.substring(0, var18);
            }
         }

         if ((var18 = super._address.indexOf(";connectiontype=router")) >= 0) {
            this.setConnectionType(0);
         } else if ((var18 = super._address.indexOf(";connectiontype=relay")) >= 0) {
            this.setConnectionType(1);
         }

         if (var18 >= 0) {
            int nextIndex = super._address.indexOf(59, var18 + 1);
            if (nextIndex >= var18) {
               super._address = super._address.substring(0, var18) + super._address.substring(nextIndex);
               return;
            }

            super._address = super._address.substring(0, var18);
         }
      }
   }

   @Override
   public final void swap() {
      super.swap();
      int port = this._hostPort;
      this._hostPort = this._localPort;
      this._localPort = port;
   }

   final int getLocalPort() {
      return this._localPort;
   }

   final void setLocalPort(int port) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final int getDestPort() {
      return this._hostPort;
   }

   final void setDestPort(int port) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final boolean resolved() {
      return this._resolved;
   }

   @Override
   public final boolean equals(Object addressBase) {
      if (addressBase == this) {
         return true;
      }

      if (addressBase instanceof SrpAddress) {
         SrpAddress dab = (SrpAddress)addressBase;
         if (this.getLinkType() == dab.getLinkType()
            && this.getLinkType() != -1
            && this.getConnectionType() == dab.getConnectionType()
            && this.getConnectionType() != -1) {
            if (dab._resolved && this._resolved) {
               if (dab._hostPort != this._hostPort || dab._hostAddress != this._hostAddress || dab._localPort != this._localPort && this._localPort != -1) {
                  return false;
               }

               return true;
            }

            if (super._address == null || super._address.length() <= 0) {
               return true;
            }

            if (dab._address != null) {
               if (super._address.length() == dab._address.length()) {
                  return StringUtilities.strEqualIgnoreCase(dab._address, super._address, 1701707776);
               }

               String addr = dab._address;
               int size = addr.length();
               int index = addr.indexOf(0, 0);
               if (index < 0) {
                  return false;
               }

               while (index + 1 < size) {
                  if (index < 0) {
                     return false;
                  }

                  int nextIndex = addr.indexOf(0, index + 1);
                  if (nextIndex <= index) {
                     nextIndex = size;
                  }

                  if (StringUtilities.regionMatches(super._address, true, 0, addr, index + 1, nextIndex - (index + 1), 1701707776)) {
                     return true;
                  }

                  index = nextIndex;
               }
            }
         }
      }

      return false;
   }

   public final int getLinkType() {
      return this._linkType;
   }

   final void setLinkType(int linkType) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final int getConnectionType() {
      return this._connectionType;
   }

   final void setConnectionType(int connectionType) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
