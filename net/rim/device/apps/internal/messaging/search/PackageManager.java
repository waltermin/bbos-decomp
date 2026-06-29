package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.synchronization.SyncEventListener;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.hotkeys.HotKeys;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.search.GlobalSearchRegistry;
import net.rim.device.apps.internal.messaging.search.criteria.BodySearchModelFactory;
import net.rim.device.apps.internal.messaging.search.criteria.FolderModelFactory;
import net.rim.device.apps.internal.messaging.search.criteria.NameSearchModelFactory;
import net.rim.device.apps.internal.messaging.search.criteria.ServiceSearchModelFactory;
import net.rim.device.apps.internal.messaging.search.criteria.ShowSearchModelFactory;
import net.rim.device.apps.internal.messaging.search.criteria.SubjectSearchModelFactory;
import net.rim.device.apps.internal.messaging.search.criteria.TypeSearchModelFactory;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;

final class PackageManager {
   static final long VIEW_FOLDER_VERB_KEY;

   public static final void libMain(String[] args) {
      Application.setAcceptEventsForProcess(false);
      MessageSearchImpl searchImpl = new MessageSearchImpl();
      RIMModelFactoryRepository.addFactory(7820085525428081380L, NameSearchModelFactory.getInstance());
      RIMModelFactoryRepository.addFactory(7820085525428081380L, SubjectSearchModelFactory.getInstance());
      RIMModelFactoryRepository.addFactory(7820085525428081380L, BodySearchModelFactory.getInstance());
      RIMModelFactoryRepository.addFactory(7820085525428081380L, FolderModelFactory.getInstance());
      RIMModelFactoryRepository.addFactory(7820085525428081380L, ShowSearchModelFactory.getInstance());
      RIMModelFactoryRepository.addFactory(7820085525428081380L, TypeSearchModelFactory.getInstance());
      RIMModelFactoryRepository.addFactory(7820085525428081380L, ServiceSearchModelFactory.getInstance());
      ResourceBundleFamily rb = SearchResources.getBundle();
      SyncManager.getInstance().enableSynchronization(searchImpl.getCollection());
      SyncManager.getInstance().addSyncEventListener((SyncEventListener)searchImpl.getCollection());
      Verb v = new SearchEditVerb();
      VerbRepository.getVerbRepository(-1585975315762883881L).register(v, -6556169429562584812L);
      HotKeys.registerHotKey(1, rb, 31, v, true);
      HotKeys.registerHotKey(3, rb, 31, v, true);
      GlobalSearchRegistry.register(-6766254879837386275L, MessageSearchable.getInstance());
      ViewFolder fv = new ViewFolder();
      VerbFactoryRepository.addFactory(2729258854446987021L, fv);
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         ar.put(3746287353255662597L, new SelectFolderVerbImpl());
         ar.put(7054367301267426154L, fv);
      }
   }
}
