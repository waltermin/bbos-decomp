package net.rim.device.apps.internal.browser.wappush;

import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.StringMatch;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ColumnPaintProvider;
import net.rim.device.apps.api.framework.model.ColumnPainter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FolderProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.UniqueIDProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;
import net.rim.device.apps.api.search.Match;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.internal.browser.push.BasePushModel;
import net.rim.device.apps.internal.browser.store.BrowserFolders;
import net.rim.device.apps.internal.browser.ui.BrowserIcons;
import net.rim.device.apps.internal.browser.wappush.verbs.ChangeStatusVerb;
import net.rim.device.apps.internal.browser.wappush.verbs.FollowLinkVerb;
import net.rim.device.apps.internal.browser.wappush.verbs.ViewMessageVerb;
import net.rim.vm.Array;

public class WAPPushModel
   extends BasePushModel
   implements Runnable,
   ActionProvider,
   PersistableRIMModel,
   VerbProvider,
   ColumnPaintProvider,
   UniqueIDProvider,
   KeyProvider,
   SyncObject,
   ConversionProvider,
   MatchProvider,
   EncryptableProvider,
   FolderProvider {
   private long _timeStamp;
   protected int _status = -1;
   protected Object _urlEncoding;
   protected int _action;
   protected Object _displayMessageEncoding;
   protected String _preferredConfigUID;
   protected String _preferredTransportCID;
   protected int _preferredConfigType;
   public static final int STATUS_OPEN = 0;
   public static final int STATUS_UNOPEN = 1;
   static final int ICON_PAINT_WIDTH = 15;
   static final int TIME_PAINT_WIDTH = 35;
   static final int DESCRIPTION_PAINT_WIDTH = 105;
   static final int ICON_COLUMN = 0;
   static final int TIME_COLUMN = 1;
   static final int DESCRIPTION_COLUMN = 2;
   private static ContextObject _notificationsContext = new ContextObject();

   public int getPreferredConfigType() {
      return this._preferredConfigType;
   }

   public String getPreferredConfigUID() {
      return this._preferredConfigUID;
   }

   public String getPreferredTransportCID() {
      return this._preferredTransportCID;
   }

   public void changeStatus(int newStatus) {
      if (this._status != newStatus) {
         if (newStatus == 0) {
            if (this._status != -1) {
               UnreadCountManager.decrementUnreadCount(6);
            }

            NotificationsManager.cancelImmediateEvent(4665536253483290822L, 0, null, null);
         } else if (newStatus == 1) {
            UnreadCountManager.incrementUnreadCount(6);
         }

         this._status = newStatus;
         Folder browserFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_WAPPUSH_FOLDER_ID);
         if (browserFolder != null) {
            ReadableList browserItems = (ReadableList)browserFolder.getContainedItems();
            if (browserItems.getIndex(this) != -1) {
               ((CollectionListener)browserItems).elementUpdated(null, this, this);
            }
         }
      }
   }

   protected int getType() {
      throw null;
   }

   protected boolean readWAPPushModel(SyncBuffer syncBuffer) {
      int start = syncBuffer.getPosition();

      try {
         while (!syncBuffer.isEmpty()) {
            int position = syncBuffer.getPosition();
            switch (syncBuffer.getFieldType(true)) {
               case 1:
               case 7:
               case 8:
               case 9:
               case 10:
                  break;
               case 2:
               default:
                  this._timeStamp = syncBuffer.getLong();
                  break;
               case 3:
                  this._status = syncBuffer.getInt();
                  if (this._status == 1) {
                     UnreadCountManager.incrementUnreadCount(6);
                  }
                  break;
               case 4:
                  this.setURL(syncBuffer.getString());
                  break;
               case 5:
                  this._action = syncBuffer.getInt();
                  break;
               case 6:
                  this.setDisplayMessage(syncBuffer.getString());
                  break;
               case 11:
                  this._preferredConfigUID = syncBuffer.getString();
                  break;
               case 12:
                  this._preferredTransportCID = syncBuffer.getString();
                  break;
               case 13:
                  this._preferredConfigType = syncBuffer.getInt();
            }

            syncBuffer.setPosition(position);
            syncBuffer.skipField();
         }
      } finally {
         ;
      }

      syncBuffer.setPosition(start);
      return true;
   }

   public int getStatus() {
      return this._status;
   }

   protected boolean writeWAPPushModel(SyncBuffer syncBuffer) {
      syncBuffer.addInt(1, this.getType(), 1);
      syncBuffer.addLong(2, this._timeStamp);
      syncBuffer.addInt(3, this._status, 1);
      syncBuffer.addField(4, this.getURL());
      syncBuffer.addInt(5, this._action, 1);
      syncBuffer.addField(6, this.getDisplayMessage());
      syncBuffer.addField(11, this._preferredConfigUID);
      syncBuffer.addField(12, this._preferredTransportCID);
      syncBuffer.addInt(13, this._preferredConfigType, 1);
      return true;
   }

   public void setDisplayMessage(String msg) {
      this._displayMessageEncoding = PersistentContent.encode(msg, true, true);
   }

   public void setAction(int action) {
      this._action = action;
   }

   public void setURL(String url) {
      this._urlEncoding = PersistentContent.encode(url, true, true);
   }

   public long getTimeStamp() {
      return this._timeStamp;
   }

   public String getURL() {
      return PersistentContent.decodeString(this._urlEncoding);
   }

   @Override
   public long getLUID(Object context) {
      return 0;
   }

   @Override
   public boolean perform(long actionId, Object context) {
      if (actionId == -3967872215949752466L) {
         Folder browserFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_WAPPUSH_FOLDER_ID);
         if (browserFolder != null) {
            this.changeStatus(0);
            WritableSet browserItems = (WritableSet)browserFolder.getContainedItems();
            browserItems.remove(this);
            return true;
         }
      } else {
         if (actionId == -6225946334564270161L) {
            this.changeStatus(0);
            return true;
         }

         if (actionId == 5803508244060051872L) {
            this.changeStatus(0);
            return true;
         }

         if (actionId == -8629311385729242560L) {
            this.changeStatus(1);
            return true;
         }

         if (actionId == -5544992959212130441L) {
            if (this.getStatus() == 1) {
               return true;
            }

            return false;
         }

         if (actionId == 477896226347912237L) {
            if (this.getStatus() != 1) {
               return true;
            }

            return false;
         }

         if (actionId == 6780594967363292755L) {
            Folder browserFolder = FolderHierarchies.getFolder(BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_WAPPUSH_FOLDER_ID);
            if (browserFolder != null) {
               this.changeStatus(0);
               WritableSet browserItems = (WritableSet)browserFolder.getContainedItems();
               browserItems.remove(this);
               return true;
            }

            return false;
         }

         if (actionId == 4951292880494466830L) {
            if (this._status == 1) {
               UnreadCountManager.incrementUnreadCount(6);
            }

            return true;
         }

         if (actionId == -198247372487919817L) {
            this.changeStatus(0);
         }
      }

      return false;
   }

   @Override
   public void paint(ColumnPainter painter, Object context) {
      painter.drawIcon(1, BrowserIcons.getIcons(), this._status == 0 ? 3 : 2);
      painter.drawTime(2, this.getTimeStamp());
      String displayMessage = this.getDisplayMessage();
      if (displayMessage != null) {
         painter.drawText(3, displayMessage, true);
      }
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      Verb defaultVerb = null;
      Array.resize(verbs, 3);
      verbs[0] = new ViewMessageVerb(this);
      verbs[1] = new FollowLinkVerb(this);
      if (this._status == 1) {
         verbs[2] = new ChangeStatusVerb(this, 0, 11);
      } else {
         verbs[2] = new ChangeStatusVerb(this, 1, 10);
      }

      return verbs[0];
   }

   @Override
   public int getUID() {
      return (int)this._timeStamp;
   }

   @Override
   public boolean convert(Object context, Object target) {
      if (!(target instanceof SyncBuffer)) {
         return false;
      }

      SyncBuffer syncBuffer = (SyncBuffer)target;
      return this.writeWAPPushModel(syncBuffer);
   }

   @Override
   public int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      if (keyRequested == 92199951187614847L) {
         keyArray[index] = this.getTimeStamp();
         return 1;
      } else {
         return 0;
      }
   }

   @Override
   public int match(Object criteria) {
      if (!(criteria instanceof SearchCriterion)) {
         SearchCriterion[] crit = (SearchCriterion[])criteria;
         int var6 = crit.length;

         while (--var6 >= 0) {
            SearchCriterion c = crit[var6];
            if (Match.performMatch(this, c) != 1) {
               return 0;
            }
         }

         return 1;
      } else {
         SearchCriterion crit = (SearchCriterion)criteria;
         boolean match = false;
         switch (crit.getType()) {
            case 1:
            case 21:
               StringMatch matcher = (StringMatch)crit.getValue();
               if (this._displayMessageEncoding != null && matcher.indexOf(this.getDisplayMessage()) >= 0) {
                  return 1;
               }
               break;
            case 9:
               match = true;
               break;
            case 24:
               match = (Integer)crit.getValue() == this.getUID();
               break;
            case 28:
               match = this._status == 1;
               break;
            default:
               match = false;
         }

         return match ? 1 : 0;
      }
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._urlEncoding, compress, encrypt)
         && PersistentContent.checkEncoding(this._displayMessageEncoding, compress, encrypt);
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      this._urlEncoding = PersistentContent.reEncode(this._urlEncoding, compress, encrypt);
      this._displayMessageEncoding = PersistentContent.reEncode(this._displayMessageEncoding, compress, encrypt);
      return null;
   }

   @Override
   public void setFolderId(long id) {
   }

   @Override
   public long getFolderId() {
      return BrowserFolders.BROWSER_WAPPUSH_FOLDER_ID;
   }

   @Override
   public int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   private String getDisplayMessage() {
      return PersistentContent.decodeString(this._displayMessageEncoding);
   }

   public WAPPushModel(long timeStamp, String displayMessage, String configUID, int configType, String transportCID) {
      this._timeStamp = timeStamp;
      this._displayMessageEncoding = PersistentContent.encode(displayMessage, true, true);
      this._preferredConfigUID = configUID;
      this._preferredTransportCID = transportCID;
      this._preferredConfigType = configType;
   }

   static {
      _notificationsContext.putIntegerData(0);
   }
}
