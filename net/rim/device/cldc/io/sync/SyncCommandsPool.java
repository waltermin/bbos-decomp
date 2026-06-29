package net.rim.device.cldc.io.sync;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.cldc.io.sync.command.Add;
import net.rim.device.cldc.io.sync.command.Delete;
import net.rim.device.cldc.io.sync.command.Get;
import net.rim.device.cldc.io.sync.command.GetRecordsHashes;
import net.rim.device.cldc.io.sync.command.GetSyncConfiguration;
import net.rim.device.cldc.io.sync.command.InitiateSync;
import net.rim.device.cldc.io.sync.command.Log;
import net.rim.device.cldc.io.sync.command.Record;
import net.rim.device.cldc.io.sync.command.RecordsHashes;
import net.rim.device.cldc.io.sync.command.Replace;
import net.rim.device.cldc.io.sync.command.Resume;
import net.rim.device.cldc.io.sync.command.Status;
import net.rim.device.cldc.io.sync.command.Suspend;
import net.rim.device.cldc.io.sync.command.SyncConfiguration;
import net.rim.device.cldc.io.sync.command.UnKnowen;
import net.rim.device.cldc.io.sync.command.Update;
import net.rim.device.cldc.io.sync.command.UpdateSyncConfiguration;
import net.rim.device.cldc.io.sync.command.Use;
import net.rim.device.internal.synchronization.ota.util.ReusableObjectPool;

public final class SyncCommandsPool {
   private IntHashtable _pools = new IntHashtable();
   private static final long KEY_VALUE_PREFIX = CRC32.update(-1, "SCP:".getBytes());

   private static final long getKeyFor(long sid) {
      return KEY_VALUE_PREFIX << 32 | 4294967295L & CRC32.update(-1, String.valueOf(sid).getBytes());
   }

   public static final SyncCommandsPool getSingletonInstance(long sid) {
      long xRegKey = getKeyFor(sid);
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      SyncCommandsPool xSyncCommandsPool = (SyncCommandsPool)ar.getOrWaitFor(xRegKey);
      if (xSyncCommandsPool == null) {
         xSyncCommandsPool = new SyncCommandsPool();
         ar.put(xRegKey, xSyncCommandsPool);
      }

      return xSyncCommandsPool;
   }

   private final SyncCommand createSyncCommandFor(int aCmdTag) {
      SyncCommand xSyncCommand = null;
      switch (aCmdTag) {
         case 0:
         case 14:
         case 15:
            return new UnKnowen();
         case 1:
         default:
            return new Add();
         case 2:
            return new Delete();
         case 3:
            return new Update();
         case 4:
            return new Replace();
         case 5:
            return new Get();
         case 6:
            return new Record();
         case 7:
            return new Use();
         case 8:
            return new Status();
         case 9:
            return new GetSyncConfiguration();
         case 10:
            return new SyncConfiguration();
         case 11:
            return new InitiateSync();
         case 12:
            return new Suspend();
         case 13:
            return new Resume();
         case 16:
            return new GetRecordsHashes();
         case 17:
            return new RecordsHashes();
         case 18:
            return new UpdateSyncConfiguration();
         case 19:
            return new Log();
      }
   }

   private final ReusableObjectPool getCommandPoolFor(int aCmdTag) {
      synchronized (this._pools) {
         ReusableObjectPool xReusableObjectPool = (ReusableObjectPool)this._pools.get(aCmdTag);
         if (xReusableObjectPool == null) {
            xReusableObjectPool = this.createPoolFor(aCmdTag);
         }

         return xReusableObjectPool;
      }
   }

   private final ReusableObjectPool createPoolFor(int aCmdTag) {
      ReusableObjectPool xReusableObjectPool = new ReusableObjectPool(2);
      this._pools.put(aCmdTag, xReusableObjectPool);
      return xReusableObjectPool;
   }

   public final void checkIn(SyncCommand aSyncCommand) {
      int xCmdTag = aSyncCommand.getTag();
      ReusableObjectPool xReusableObjectPool = this.getCommandPoolFor(xCmdTag);
      xReusableObjectPool.checkIn(aSyncCommand);
   }

   public final SyncCommand checkOut(int aCmdTag) {
      ReusableObjectPool xReusableObjectPool = this.getCommandPoolFor(aCmdTag);
      SyncCommand xSyncCommand = (SyncCommand)xReusableObjectPool.checkOut();
      if (xSyncCommand == null) {
         xSyncCommand = this.createSyncCommandFor(aCmdTag);
      }

      return xSyncCommand;
   }

   public final void free() {
      synchronized (this._pools) {
         this._pools.clear();
      }
   }
}
