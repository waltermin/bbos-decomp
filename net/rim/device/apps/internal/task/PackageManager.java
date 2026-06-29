package net.rim.device.apps.internal.task;

import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;
import net.rim.device.apps.api.search.GlobalSearchRegistry;
import net.rim.device.internal.proxy.Proxy;

final class PackageManager {
   private static final long TASK_SEARCHABLE_ID = -5331631597610348814L;

   static final void registerOnceOnSystemStart() {
      UnreadCountManager.setUnreadCountVisible(9, false);
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Proxy p = Proxy.getInstance();
      p.addGlobalEventListener(TaskUtilities.getInstance());
      TaskModelFactory tmf = new TaskModelFactory();
      ar.put(-4172790793103625162L, tmf);
      RIMModelFactoryRepository.addFactory(-8250775496544885030L, tmf);
      TaskOptions taskOptions = TaskOptions.getOptions();
      taskOptions.enableSynchronization();
      ar.put(-5646701879688313636L, new PackageManager$1());
      VerbRepository.getVerbRepository(-7621772147653206349L).register(OpenTaskVerb.getInstance(null), 4738722199580714034L);
      VerbRepository.getVerbRepository(-5900177594279140906L).register(new NewTaskVerb(""), 4738722199580714034L);
      NotificationsManager.registerSource(204325571560529255L, new PackageManager$2(), 1);
      RIMModelFactoryRepository.addFactory(7798410905730545828L, new TaskDataModelFactory());
      GlobalSearchRegistry.register(-5331631597610348814L, new TaskSearchable());
      SyncManager.getInstance().enableSynchronization(TaskCollectionImpl.getInstance(), taskOptions.isWirelessSyncAllowed(), 5);
   }
}
