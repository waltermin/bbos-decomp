package net.rim.device.api.crypto;

import net.rim.device.api.synchronization.SyncManager;

public final class RandomDatabase {
   public RandomDatabase() {
      SyncManager.getInstance().enableSynchronization(new RandomDatabaseSync("Random Pool"));
   }
}
