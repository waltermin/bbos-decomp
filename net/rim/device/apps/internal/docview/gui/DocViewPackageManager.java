package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.transmission.rim.CMIMEConverterRegistry;

final class DocViewPackageManager {
   public static final void libMain(String[] args) {
      registerOnceOnSystemStart();
   }

   private DocViewPackageManager() {
   }

   public static final void registerOnceOnSystemStart() {
      DocViewOptions.getOptions().enableSynchronization();
      DocViewSyncCollection.getInstance();
      ApplicationRegistry.getApplicationRegistry().put(-811824568997914181L, new AttachmentViewerModelFactory());
      CMIMEConverterRegistry.addConverter(new DocViewModelConverter(), 2);
      ((CollectionEventSource)FolderMerge.getMergeCollection(-5581791943352753293L)).addCollectionListener(new DocViewMessageListener());
      VerbFactoryRepository.addFactory(6003662854924499794L, new DocViewPackageManager$EmailViewerScreenVerbFactory());
   }
}
