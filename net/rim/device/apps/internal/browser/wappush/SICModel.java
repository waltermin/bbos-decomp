package net.rim.device.apps.internal.browser.wappush;

import net.rim.device.api.browser.push.PushOptions;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.internal.browser.bookmark.BookmarksScreen;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.BrowserImpl;
import net.rim.device.apps.internal.browser.push.PushDisplayDialog;
import net.rim.device.apps.internal.browser.store.BrowserFolders;

public final class SICModel extends WAPPushModel implements Persistable, EncryptableProvider {
   private Object _messageEncoding;
   private Object _id;
   private long _expiry;
   private long _created;

   public SICModel(String configUID, int configType, String transportCID) {
      super(System.currentTimeMillis(), null, configUID, configType, transportCID);
      super._action = 7;
      this._expiry = Long.MAX_VALUE;
   }

   @Override
   public final boolean showBrowser() {
      if (super._action != 8 && super._action != 7 && super._action != 6) {
         return false;
      }

      switch (PushOptions.getOptions().getAcceptMode(1, this.mapPushSourceToProtocolType(super._connectionType))) {
         case 0:
            if (super._action == 8) {
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
      int result = this.rejectMessage(1);
      if (result != 0) {
         return result;
      } else if (this._expiry < System.currentTimeMillis()) {
         return 5;
      } else {
         return super._action != 9 && this.checkForDuplicate(false) != null ? 1 : 0;
      }
   }

   @Override
   public final void run() {
      if (this._expiry >= System.currentTimeMillis()) {
         switch (super._action) {
            case 4:
               break;
            case 5:
            case 6:
            case 7:
            case 8:
            default:
               if (super._action != 5) {
                  NotificationsManager.cancelImmediateEvent(4665536253483290822L, 0, null, null);
                  NotificationsManager.triggerImmediateEvent(4665536253483290822L, 0, null, null);
               }

               int siMessagesMode = PushOptions.getOptions().getAcceptMode(1, this.mapPushSourceToProtocolType(super._connectionType));
               if (siMessagesMode == 1 || siMessagesMode == 0 && super._action == 8) {
                  Screen activeScreen = UiApplication.getUiApplication().getActiveScreen();
                  if (activeScreen instanceof BookmarksScreen) {
                     activeScreen.close();
                  }

                  PushDisplayDialog dialog = new PushDisplayDialog(this.getMessage(), this.getURL());
                  dialog.setModal(true);
                  dialog.show();
                  int closeReason = dialog.getCloseReason();
                  if ((closeReason == 0 || closeReason == 1) && this.addToFolder()) {
                     this.changeStatus(0);
                  }

                  if (closeReason == 1 || closeReason == -1) {
                     BrowserImpl browser = BrowserDaemonRegistry.getInstance();
                     if (!this.getBrowserForeground() && browser.enquedPushSize() == 0) {
                        browser.closeBrowser(false);
                        return;
                     }
                  }
               } else if (this.addToFolder()) {
                  this.changeStatus(1);
                  return;
               }
               break;
            case 9:
               Folder browserFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_WAPPUSH_FOLDER_ID);
               SICModel duplicate = this.checkForDuplicate(true);
               if (browserFolder != null && duplicate != null) {
                  WritableSet browserItems = (WritableSet)browserFolder.getContainedItems();
                  duplicate.changeStatus(0);
                  browserItems.remove(duplicate);
               }
         }
      }
   }

   public final String getMessage() {
      return PersistentContent.decodeString(this._messageEncoding);
   }

   public final long getExpiry() {
      return this._expiry;
   }

   public final void setMessage(String message) {
      this._messageEncoding = PersistentContent.encode(message, true, true);
      this.setDisplayMessage(message);
   }

   public final void setID(String id) {
      this._id = PersistentContent.encode(id, true, true);
   }

   public final String getID() {
      return PersistentContent.decodeString(this._id);
   }

   public final void setExpiry(long cal) {
      this._expiry = cal;
   }

   public final void setCreated(long cal) {
      this._created = cal;
   }

   @Override
   protected final int getType() {
      return 1;
   }

   @Override
   protected final boolean writeWAPPushModel(SyncBuffer syncBuffer) {
      super.writeWAPPushModel(syncBuffer);
      syncBuffer.addLong(8, this._expiry);
      syncBuffer.addLong(9, this._created);
      syncBuffer.addField(10, this.getID());
      syncBuffer.addField(7, this.getMessage());
      return true;
   }

   @Override
   protected final boolean readWAPPushModel(SyncBuffer syncBuffer) {
      if (!super.readWAPPushModel(syncBuffer)) {
         return false;
      }

      try {
         while (!syncBuffer.isEmpty()) {
            int position = syncBuffer.getPosition();
            switch (syncBuffer.getFieldType(true)) {
               case 6:
                  break;
               case 7:
                  this.setMessage(syncBuffer.getString());
                  break;
               case 8:
               default:
                  this._expiry = syncBuffer.getLong();
                  break;
               case 9:
                  this._created = syncBuffer.getLong();
                  break;
               case 10:
                  this.setID(syncBuffer.getString());
            }

            syncBuffer.setPosition(position);
            syncBuffer.skipField();
         }

         return true;
      } finally {
         ;
      }
   }

   @Override
   public final boolean equals(Object anObject) {
      if (anObject == this) {
         return true;
      } else if (anObject instanceof SICModel && this.getID() != null) {
         SICModel aModel = (SICModel)anObject;
         return StringUtilities.strEqualIgnoreCase(aModel.getID(), this.getID(), 1701707776);
      } else {
         return false;
      }
   }

   private final SICModel checkForDuplicate(boolean before) {
      Folder browserFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_WAPPUSH_FOLDER_ID);
      if (browserFolder != null) {
         ReadableList browserList = (ReadableList)browserFolder.getContainedItems();
         int count = browserList.size();

         for (int i = 0; i < count; i++) {
            Object anObject = browserList.getAt(i);
            if (this.equals(anObject)) {
               SICModel anotherSI = (SICModel)anObject;
               if (this._created == 0 && anotherSI._created == 0) {
                  return anotherSI;
               }

               if (before && anotherSI._created <= this._created) {
                  return anotherSI;
               }

               if (!before && anotherSI._created >= this._created) {
                  return anotherSI;
               }
            }
         }
      }

      return null;
   }

   private final boolean addToFolder() {
      Folder browserFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_WAPPUSH_FOLDER_ID);
      if (browserFolder != null) {
         WritableSet browserItems = (WritableSet)browserFolder.getContainedItems();
         SICModel duplicate = this.checkForDuplicate(true);
         if (duplicate != null) {
            duplicate.changeStatus(0);
            browserItems.remove(duplicate);
         }

         browserItems.add(this);
         WAPPushExpiryManager.getInstance().addExpiry(this._expiry);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return super.checkCrypt(compress, encrypt) && PersistentContent.checkEncoding(this._messageEncoding, compress, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      super.reCrypt(compress, encrypt);
      this._messageEncoding = PersistentContent.reEncode(this._messageEncoding, compress, encrypt);
      return null;
   }
}
