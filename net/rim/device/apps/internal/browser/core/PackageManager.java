package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.StringPatternRepository;
import net.rim.device.apps.internal.browser.bookmark.BrowserBookmarksCollection;
import net.rim.device.apps.internal.browser.channel.ChannelModelCollection;
import net.rim.device.apps.internal.browser.channel.ChannelModelFactory;
import net.rim.device.apps.internal.browser.model.AddressBookModelFactory;
import net.rim.device.apps.internal.browser.model.AddressBookStringPattern;
import net.rim.device.apps.internal.browser.model.DTMFModelFactory;
import net.rim.device.apps.internal.browser.model.DTMFStringPattern;
import net.rim.device.apps.internal.browser.model.HTTPAddressStringPattern;
import net.rim.device.apps.internal.browser.options.BrowserOptionsSync;
import net.rim.device.apps.internal.browser.page.BrowserPageModel;
import net.rim.device.apps.internal.browser.page.BrowserPageModelFactory;
import net.rim.device.apps.internal.browser.sbloader.SBHelper;
import net.rim.device.apps.internal.browser.stack.CacheModelCollection;
import net.rim.device.apps.internal.browser.stack.CacheModelFactory;
import net.rim.device.apps.internal.browser.store.BrowserFolderCollection;
import net.rim.device.apps.internal.browser.store.BrowserRepositoryCollection;
import net.rim.device.apps.internal.browser.store.BrowserURLCollection;
import net.rim.device.apps.internal.browser.wappush.WAPPushModelFactory;
import net.rim.device.apps.internal.browser.wappush.WAPPushRepositoryCollection;

final class PackageManager {
   static final void registerOnceOnSystemStart() {
      SyncManager sm = SyncManager.getInstance();
      sm.enableSynchronization(new BrowserBookmarksCollection());
      sm.enableSynchronization(new BrowserRepositoryCollection(BrowserRepositoryCollection.BROWSER_MESSAGES_DB_NAME, 867));
      sm.enableSynchronization(new CacheModelCollection());
      sm.enableSynchronization(new ChannelModelCollection());
      sm.enableSynchronization(new BrowserFolderCollection());
      sm.enableSynchronization(new BrowserOptionsSync());
      sm.enableSynchronization(new WAPPushRepositoryCollection());
      sm.enableSynchronization(new BrowserURLCollection());
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(8419621845400492256L, new BrowserPageModelFactory());
      ar.put(3333520401445387752L, new ChannelModelFactory());
      ar.put(1043003344163629438L, new CacheModelFactory());
      ar.put(5019899335844518230L, new Object());
      ar.put(-4153783271009930225L, new WAPPushModelFactory());
      ar.put(6979592587978139791L, new DTMFModelFactory());
      ar.put(4085495887538053543L, new AddressBookModelFactory());
      StringPatternRepository.addPattern(new HTTPAddressStringPattern());
      StringPatternRepository.addPattern(new DTMFStringPattern());
      StringPatternRepository.addPattern(new AddressBookStringPattern());
      SBHelper.getInstance().injectServiceBooks();
      BrowserPageModel.registerWithNotificationsManager();
   }
}
