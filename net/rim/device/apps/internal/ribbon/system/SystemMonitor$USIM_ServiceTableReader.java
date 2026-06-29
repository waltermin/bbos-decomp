package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.SIMCard;
import net.rim.device.apps.api.ribbon.RibbonApi;
import net.rim.device.internal.system.SIMServiceTable;

final class SystemMonitor$USIM_ServiceTableReader extends SIMServiceTable {
   private byte[] _USTdata;
   private byte[] _ESTdata;
   private final SystemMonitor this$0;

   private SystemMonitor$USIM_ServiceTableReader(SystemMonitor _1) {
      this.this$0 = _1;
      this._USTdata = new byte[16];
      this._ESTdata = new byte[16];
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final boolean startRead() {
      if (this.this$0._USTlen < 1) {
         boolean var3 = false /* VF: Semaphore variable */;

         try {
            var3 = true;
            this.this$0._ESTlen = SIMCard.requestEFRead(101, 0, 0, this._ESTdata);
            this.this$0._USTlen = SIMCard.requestEFRead(100, 0, 0, this._USTdata);
            var3 = false;
         } finally {
            if (var3) {
               if (RibbonApi._logONSState) {
                  System.out.println("*** UST,EST not populated");
               }

               this.this$0.simCachePopulated();
               return false;
            }
         }

         if (this.this$0._USTlen == -1) {
            return false;
         }

         this.createServiceTableArray();
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         ar.replace(SIMServiceTable.SIM_SERVICE_TABLE_SINGLETON_GUID, this);
      }

      this.this$0.loadHomeNetworkNamesToCache();
      return true;
   }

   private final void createServiceTableArray() {
      byte[] sst = super._mappedSST;
      int i = sst.length;

      while (--i >= 0) {
         sst[i] = 0;
      }

      i = Math.min(this.this$0._USTlen * 8, SystemMonitor.UST_TO_COMMON_ST_INDEX.length);

      while (--i >= 0) {
         if ((this._USTdata[i / 8] & 1 << i % 8) != 0) {
            int commonIndex = SystemMonitor.UST_TO_COMMON_ST_INDEX[i];
            sst[commonIndex / 8] = (byte)(sst[commonIndex / 8] | 1 << commonIndex % 8);
         }
      }

      if ((sst[4] & 2) != 0) {
         i = Math.min(this.this$0._ESTlen * 8, SystemMonitor.EST_TO_COMMON_ST_INDEX.length);

         while (--i >= 0) {
            if ((this._ESTdata[i / 8] & 1 << i % 8) == 0) {
               int commonIndex = SystemMonitor.UST_TO_COMMON_ST_INDEX[i];
               sst[commonIndex / 8] = (byte)(sst[commonIndex / 8] & 1 << commonIndex % 8);
            }
         }
      }
   }

   SystemMonitor$USIM_ServiceTableReader(SystemMonitor x0, SystemMonitor$1 x1) {
      this(x0);
   }
}
