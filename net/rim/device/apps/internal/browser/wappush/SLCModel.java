package net.rim.device.apps.internal.browser.wappush;

import net.rim.device.api.browser.push.PushOptions;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.internal.browser.bookmark.BookmarksScreen;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.push.BrowserPushResources;
import net.rim.device.apps.internal.browser.push.PushDisplayDialog;
import net.rim.device.apps.internal.browser.store.BrowserFolders;
import net.rim.device.apps.internal.browser.wappush.verbs.FollowLinkVerb;

final class SLCModel extends WAPPushModel implements Persistable {
   public SLCModel(String configUID, int configType, String transportCID) {
      super(System.currentTimeMillis(), null, configUID, configType, transportCID);
      this.setAction(5);
   }

   @Override
   public final boolean showBrowser() {
      if (super._action != 6 && super._action != 5) {
         return false;
      }

      switch (PushOptions.getOptions().getAcceptMode(0, this.mapPushSourceToProtocolType(super._connectionType))) {
         case 0:
            if (super._action != 5) {
               return true;
            }

            return false;
         case 2:
            return false;
         default:
            return true;
      }
   }

   @Override
   public final int rejectMessage() {
      return this.rejectMessage(0);
   }

   @Override
   public final void run() {
      switch (super._action) {
         case 4:
            break;
         case 5:
         case 6:
         default:
            NotificationsManager.cancelImmediateEvent(4665536253483290822L, 0, null, null);
            NotificationsManager.triggerImmediateEvent(4665536253483290822L, 0, null, null);
            UiApplication app = UiApplication.getUiApplication();
            if (app != null) {
               app.invokeLater(new SLCModel$1(this), 5000, false);
               int slMessagesMode = PushOptions.getOptions().getAcceptMode(0, this.mapPushSourceToProtocolType(super._connectionType));
               if (slMessagesMode != 1) {
                  if (slMessagesMode == 0 && super._action == 5) {
                     Folder browserFolder = FolderHierarchies.getFolder(
                        BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_WAPPUSH_FOLDER_ID
                     );
                     if (browserFolder != null) {
                        WritableSet browserItems = (WritableSet)browserFolder.getContainedItems();
                        browserItems.add(this);
                        this.changeStatus(1);
                        return;
                     }
                  } else {
                     Folder browserFolder = FolderHierarchies.getFolder(
                        BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_WAPPUSH_FOLDER_ID
                     );
                     if (browserFolder != null) {
                        WritableSet browserItems = (WritableSet)browserFolder.getContainedItems();
                        browserItems.add(this);
                        this.changeStatus(0);
                     }

                     Status.show(BrowserPushResources.getString(8));
                     FollowLinkVerb verb = new FollowLinkVerb(this);
                     verb.invoke(null);
                     Screen activeScreen = app.getActiveScreen();
                     if (activeScreen instanceof BookmarksScreen) {
                        activeScreen.close();
                        return;
                     }
                  }
               } else {
                  String message = BrowserPushResources.getString(15);
                  PushDisplayDialog dialog = new PushDisplayDialog(message, this.getURL());
                  dialog.setModal(true);
                  dialog.show();
                  int closeReason = dialog.getCloseReason();
                  if (closeReason == 0 || closeReason == 1) {
                     Folder browserFolder = FolderHierarchies.getFolder(
                        BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_WAPPUSH_FOLDER_ID
                     );
                     if (browserFolder != null) {
                        WritableSet browserItems = (WritableSet)browserFolder.getContainedItems();
                        browserItems.add(this);
                        this.changeStatus(0);
                     }
                  }

                  if (closeReason == 1 || closeReason == -1) {
                     BrowserImpl browser = BrowserDaemonRegistry.getInstance();
                     if (!this.getBrowserForeground() && browser.enquedPushSize() == 0) {
                        browser.closeBrowser(false);
                        return;
                     }
                  }
               }
            }
            break;
         case 7:
            SLCModel$CacheThread cThread = new SLCModel$CacheThread(
               this.getURL(), super._preferredConfigUID, super._preferredConfigType, super._preferredTransportCID
            );
            cThread.start();
      }
   }

   @Override
   public final void setURL(String url) {
      super.setURL(url);
      this.setDisplayMessage(url);
   }

   @Override
   protected final int getType() {
      return 2;
   }
}
