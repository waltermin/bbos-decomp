package net.rim.device.cldc.impl.tunnel;

import java.util.Vector;
import net.rim.device.api.hrt.HostRoutingInfo;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.cldc.io.tunnel.TunnelApnList;

public final class TunnelApnListImpl implements TunnelApnList {
   private Vector _apnList = (Vector)(new Object());
   private static String IIF_APN;
   private static int SUPPORTED_WAFS;
   private static int CDMA_AND_GPRS_MASK;
   private static final String EMPTY_APN;
   private static final String BLACKBERRY_NET;

   @Override
   public final void removeFirst() {
      synchronized (this._apnList) {
         if (this._apnList.size() > 0) {
            this._apnList.removeElementAt(0);
         }
      }
   }

   @Override
   public final void addFirst(String apn) {
      if (apn != null) {
         synchronized (this._apnList) {
            this._apnList.insertElementAt(apn, 0);
         }
      }
   }

   @Override
   public final void addLast(String apn) {
      if (apn != null) {
         synchronized (this._apnList) {
            this._apnList.addElement(apn);
         }
      }
   }

   @Override
   public final String getFirst() {
      String result = null;
      synchronized (this._apnList) {
         if (this._apnList.size() > 0) {
            result = (String)this._apnList.elementAt(0);
         }

         return result;
      }
   }

   @Override
   public final int getSize() {
      synchronized (this._apnList) {
         return this._apnList.size();
      }
   }

   @Override
   public final void resetList() {
      synchronized (this._apnList) {
         this._apnList.removeAllElements();
      }
   }

   @Override
   public final void initializeList(Object hri) {
      int activeWafs = RadioInfo.getActiveWAFs();
      if (this.getSize() <= 0) {
         this.resetList();
         if ((SUPPORTED_WAFS & CDMA_AND_GPRS_MASK) == CDMA_AND_GPRS_MASK) {
            if ((activeWafs & 1) != 0) {
               if (IIF_APN != null) {
                  this.addLast(IIF_APN);
               }

               if (hri instanceof Object) {
                  this.addLast(((HostRoutingInfo)hri).getApn());
               } else {
                  this.addLast("blackberry.net");
               }
            }

            if ((activeWafs & 10) != 0 && (hri instanceof Object || hri instanceof Object)) {
               this.addLast("");
               return;
            }
         } else if ((SUPPORTED_WAFS & 1) != 0) {
            if (hri instanceof Object) {
               this.addLast(((HostRoutingInfo)hri).getApn());
               return;
            }
         } else {
            if ((SUPPORTED_WAFS & 10) == 0) {
               throw new Object();
            }

            if (hri instanceof Object || hri instanceof Object) {
               this.addLast("");
               return;
            }
         }
      }
   }

   static {
      byte[] data = Branding.getData(13824);
      if (data != null) {
         IIF_APN = (String)(new Object(data));
      }

      SUPPORTED_WAFS = RadioInfo.getSupportedWAFs();
      CDMA_AND_GPRS_MASK = 3;
   }
}
