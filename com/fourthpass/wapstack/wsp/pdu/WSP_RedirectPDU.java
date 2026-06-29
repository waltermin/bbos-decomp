package com.fourthpass.wapstack.wsp.pdu;

import com.fourthpass.wapstack.wsp.WSPAddress;
import java.io.InputStream;
import java.util.Vector;

public final class WSP_RedirectPDU extends WSP_PDU {
   public WSP_RedirectPDU(boolean connectionless, InputStream data) {
      super(connectionless, data);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final WSPAddress[] getRedirectAddress() {
      int pos = 3;
      Vector store = (Vector)(new Object());

      while (pos < super._PDU.length) {
         int addrLen = super._PDU[pos] & 63;
         byte[] data = new byte[addrLen];

         try {
            WSPAddress add;
            if ((super._PDU[pos] & 128) != 128) {
               System.arraycopy(super._PDU, pos, data, 0, addrLen);
               add = new WSPAddress(data);
            } else if ((super._PDU[pos++] & 64) == 64) {
               System.arraycopy(super._PDU, pos + 3, data, 0, addrLen);
               byte bearer = super._PDU[pos++];
               byte temp = super._PDU[pos++];
               int port;
               if (temp > 0) {
                  port = temp;
               } else {
                  port = 256 + temp;
               }

               port <<= 8;
               temp = super._PDU[pos++];
               if (temp > 0) {
                  port += temp;
               } else {
                  port = port + 256 + temp;
               }

               add = new WSPAddress(bearer, port, data);
            } else {
               System.arraycopy(super._PDU, pos + 1, data, 0, addrLen);
               add = new WSPAddress(super._PDU[pos++], data);
            }

            store.addElement(add);
            pos += addrLen;
         } catch (Throwable var11) {
            ex.printStackTrace();
            continue;
         }
      }

      WSPAddress[] addresses = new WSPAddress[store.size()];

      for (int i = 0; i < store.size(); i++) {
         addresses[i] = (WSPAddress)store.elementAt(i);
      }

      return addresses;
   }

   public final byte getFlag() {
      return super._PDU[2];
   }
}
