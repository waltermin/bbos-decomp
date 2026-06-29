package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.SIMCard;
import net.rim.device.apps.api.ribbon.RibbonApi;
import net.rim.device.internal.system.SIMServiceTable;

final class SystemMonitor$SIM_ServiceTableReader extends SIMServiceTable {
   private byte[] _SSTdata;
   private final SystemMonitor this$0;

   private SystemMonitor$SIM_ServiceTableReader(SystemMonitor _1) {
      this.this$0 = _1;
      this._SSTdata = new byte[30];
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final boolean startRead() {
      if (this.this$0._SSTlen < 1) {
         boolean var3 = false /* VF: Semaphore variable */;

         try {
            var3 = true;
            this.this$0._SSTlen = SIMCard.requestEFRead(7, 0, 0, this._SSTdata);
            var3 = false;
         } finally {
            if (var3) {
               if (RibbonApi._logONSState) {
                  System.out.println("*** SST not populated");
               }

               this.this$0.simCachePopulated();
               return false;
            }
         }

         if (this.this$0._SSTlen == -1) {
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

      i = Math.min(this.this$0._SSTlen * 4, SystemMonitor.SST_TO_COMMON_ST_INDEX.length);

      while (--i >= 0) {
         int bitPair = 3 << (i % 4 << 1);
         if ((this._SSTdata[i / 4] & bitPair) == bitPair) {
            int commonIndex = SystemMonitor.SST_TO_COMMON_ST_INDEX[i];
            sst[commonIndex / 8] = (byte)(sst[commonIndex / 8] | 1 << commonIndex % 8);
         }
      }
   }

   SystemMonitor$SIM_ServiceTableReader(SystemMonitor x0, SystemMonitor$1 x1) {
      this(x0);
   }
}
