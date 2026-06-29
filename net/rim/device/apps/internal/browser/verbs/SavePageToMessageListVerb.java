package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.api.collection.WritableSet;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserHotkeys;
import net.rim.device.apps.internal.browser.core.BrowserSession;
import net.rim.device.apps.internal.browser.page.BrowserPageModel;
import net.rim.device.apps.internal.browser.page.Page;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.store.BrowserFolders;
import net.rim.device.apps.internal.browser.ui.DialogEnterString;
import net.rim.device.internal.system.InternalServices;

public final class SavePageToMessageListVerb extends BrowserVerb {
   private static final int DESCRIPTION;

   public SavePageToMessageListVerb() {
      super(16987184);
      if (!InternalServices.isReducedFormFactor()) {
         BrowserHotkeys.registerBrowserHotKey(341, this);
      }
   }

   @Override
   public final String toString() {
      return BrowserResources.getString(111);
   }

   @Override
   public final Object invoke(Object context) {
      Page page = BrowserDaemonRegistry.getInstance().getCurrentPage();
      if (page != null && page.getModelResult() != null) {
         boolean confirm = true;
         if (context instanceof Object) {
            confirm = !((ContextObject)context).getFlag(64);
         }

         int result = 0;
         String title = page.getTitle();
         if (confirm) {
            DialogEnterString dialog = (DialogEnterString)(new Object(
               BrowserResources.getString(525), title, CommonResources.getString(117), CommonResources.getString(2002)
            ));
            result = dialog.doModal();
            title = dialog.getResult();
         }

         if (result != -1) {
            Folder browserFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_MESSAGES_FOLDER_ID);
            if (browserFolder == null) {
               return null;
            }

            PageModel pageModel = (PageModel)page.getPageModel();
            if (pageModel == null) {
               return null;
            }

            BrowserPageModel browserPageModel = new BrowserPageModel(0, title, pageModel.getModelResult(), browserFolder.getLUID());
            BrowserSession session = BrowserSession.getCurrentSession();
            if (session != null) {
               browserPageModel.getModelResult().setConfigUID(session.getConfig().getUid());
            }

            if (!browserPageModel.checkCrypt(true, true)) {
               browserPageModel.reCrypt(true, true);
            }

            WritableSet browserItems = (WritableSet)browserFolder.getContainedItems();
            browserItems.add(browserPageModel);
            browserPageModel.changeStatus(4);
         }

         return null;
      } else {
         return null;
      }
   }

   @Override
   public final boolean isEnabled() {
      Page page = BrowserDaemonRegistry.getInstance().getCurrentPage();
      return page != null && page.getModelResult() != null;
   }

   @Override
   public final void cleanup() {
      if (!InternalServices.isReducedFormFactor()) {
         BrowserHotkeys.deregisterBrowserHotKey(341);
      }
   }
}
