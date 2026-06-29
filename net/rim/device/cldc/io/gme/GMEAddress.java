package net.rim.device.cldc.io.gme;

import java.util.Vector;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.util.StringUtilities;

public final class GMEAddress extends DatagramAddressBase {
   private String _cid;
   private GMETarget _target;
   private Vector _targets;
   private GMETarget _src;

   public GMEAddress() {
   }

   public GMEAddress(String cid, GMETarget target, GMETarget src) {
      this._cid = cid;
      this._target = target;
      this._src = src;
   }

   public GMEAddress(String cid, Vector targets, GMETarget src) {
      this._cid = cid;
      this._targets = targets;
      this._src = src;
   }

   public GMEAddress(GMEAddress addressBase, boolean swap) {
      this._cid = addressBase._cid;
      if (swap) {
         this._target = addressBase._src;
      } else {
         this._target = addressBase._target;
         this._targets = addressBase._targets;
         this._src = addressBase._src;
      }
   }

   public GMEAddress(DatagramAddressBase addressBase) {
      if (addressBase instanceof GMEAddress) {
         this._cid = ((GMEAddress)addressBase)._cid;
         this._target = ((GMEAddress)addressBase)._target;
         this._targets = ((GMEAddress)addressBase)._targets;
         this._src = ((GMEAddress)addressBase)._src;
      } else {
         this.setAddress(addressBase.getAddress());
      }
   }

   public GMEAddress(String address) {
      this.setAddress(address);
   }

   public final String getCid() {
      return this._cid;
   }

   public final int getNumTargets() {
      if (this._target != null) {
         return 1;
      } else {
         return this._targets == null ? 0 : this._targets.size();
      }
   }

   public final GMETarget getTarget(int index) {
      if (this._target != null) {
         return this._target;
      } else if (this._targets == null) {
         throw new ArrayIndexOutOfBoundsException();
      } else {
         return (GMETarget)this._targets.elementAt(index);
      }
   }

   private final void addTarget(GMETarget target) {
      if (this._target != null) {
         this._targets = new Vector(2);
         this._targets.addElement(this._target);
         this._targets.addElement(target);
         this._target = null;
      } else if (this._targets == null) {
         this._target = target;
      } else {
         this._targets.addElement(target);
      }
   }

   public final GMETarget getSrc() {
      return this._src;
   }

   @Override
   public final void setAddress(String address) {
      int scan = 0;
      int delim = 0;
      int length = address.length();
      this._cid = null;
      this._target = null;
      this._targets = null;
      this._src = null;
      if (length > 0) {
         delim = DatagramAddressBase.indexOfNextDelim(address, 0);
         if (delim <= 0) {
            throw new IllegalArgumentException("Bad CID");
         }

         this._cid = address.substring(0, delim);
      }

      while (length > delim) {
         String redirect = null;
         String name;
         int type;
         if (address.charAt(delim) == '/') {
            scan = delim + 1;
            delim = DatagramAddressBase.indexOfNextDelim(address, scan);
            if (delim <= scan) {
               throw new IllegalArgumentException("Bad UID");
            }

            name = address.substring(scan, delim);
            type = 1;
         } else {
            if (address.charAt(delim) != ':') {
               break;
            }

            scan = delim + 1;
            delim = DatagramAddressBase.indexOfNextDelim(address, scan);
            if (delim <= scan) {
               throw new IllegalArgumentException("Bad PIN");
            }

            name = address.substring(scan, delim);
            type = 2;
         }

         if (length > delim && address.charAt(delim) == '(') {
            scan = delim + 1;
            delim = address.indexOf(41, scan);
            if (delim <= scan) {
               throw new IllegalArgumentException("Bad REDIRECT");
            }

            redirect = address.substring(scan, delim);
            scan = delim + 1;
            delim = DatagramAddressBase.indexOfNextDelim(address, scan);
            if (delim != scan) {
               throw new IllegalArgumentException("Bad REDIRECT");
            }
         }

         this.addTarget(new GMETarget(name, type, redirect));
      }

      if (length > delim && address.charAt(delim) == '$') {
         String redirect = null;
         scan = delim + 1;
         delim = DatagramAddressBase.indexOfNextDelim(address, scan);
         if (delim <= scan) {
            throw new IllegalArgumentException("Bad SRC");
         }

         String name = address.substring(scan, delim);
         if (length > delim && address.charAt(delim) == '(') {
            scan = delim + 1;
            delim = address.indexOf(41, scan);
            if (delim <= scan) {
               throw new IllegalArgumentException("Bad REDIRECT");
            }

            redirect = address.substring(scan, delim);
            scan = delim + 1;
            delim = DatagramAddressBase.indexOfNextDelim(address, scan);
            if (delim != scan) {
               throw new IllegalArgumentException("Bad REDIRECT");
            }
         }

         this._src = new GMETarget(name, 0, redirect);
      }

      if (delim < length) {
         throw new IllegalArgumentException("Bad address");
      }

      super._address = address;
   }

   @Override
   public final String getAddress() {
      if (super._address == null) {
         super._address = makeAddress(false, this._cid, this._target, this._targets, this._src);
      }

      return super._address;
   }

   @Override
   public final boolean equals(Object addressBase) {
      if (addressBase == this) {
         return true;
      }

      if (!(addressBase instanceof GMEAddress)) {
         return false;
      }

      GMEAddress address = (GMEAddress)addressBase;
      return (this._cid == null || address._cid == null || StringUtilities.strEqualIgnoreCase(this._cid, address._cid, 1701707776))
         && (this.getNumTargets() == 0 || address._src == null || this.compareTargets(address._src.address))
         && (this._src == null || address.getNumTargets() == 0 || address.compareTargets(this._src.address));
   }

   @Override
   public final int hashCode() {
      int hash = 7;
      if (this._cid != null) {
         hash = 31 * hash + StringUtilities.toLowerCase(this._cid, 1701707776).hashCode();
      }

      return hash;
   }

   public final boolean compareTargets(String src) {
      for (int i = this.getNumTargets() - 1; i >= 0; i--) {
         if (StringUtilities.strEqualIgnoreCase(this.getTarget(i).address, src, 1701707776)) {
            return true;
         }
      }

      return false;
   }

   public static final String makeAddress(boolean open, String cid, GMETarget target, GMETarget src) {
      return makeAddress(open, cid, target, null, src);
   }

   public static final String makeAddress(boolean open, String cid, GMETarget target, Vector targets, GMETarget src) {
      StringBuffer buf = new StringBuffer(128);
      appendAddress(buf, open, cid, target, targets, src);
      return buf.toString();
   }

   public static final void appendAddress(StringBuffer buf, boolean open, String cid, GMETarget target, Vector targets, GMETarget src) {
      if (open) {
         buf.append("gme:");
      }

      if (cid != null) {
         buf.append(cid);
      }

      if (target != null) {
         appendTarget(buf, false, target);
      } else if (targets != null) {
         int size = targets.size();

         for (int i = 0; i < size; i++) {
            target = (GMETarget)targets.elementAt(i);
            appendTarget(buf, false, (GMETarget)targets.elementAt(i));
         }
      }

      if (src != null) {
         appendTarget(buf, true, src);
      }
   }

   private static final void appendTarget(StringBuffer buf, boolean src, GMETarget target) {
      if (!src) {
         switch (target.type) {
            case 2:
               buf.append(':');
               break;
            default:
               buf.append('/');
         }
      } else {
         buf.append('$');
      }

      buf.append(target.address);
      if (target.redirect != null) {
         buf.append('(');
         buf.append(target.redirect);
         buf.append(')');
      }
   }
}
