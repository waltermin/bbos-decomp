package net.rim.device.apps.internal.browser.stack;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.crypto.RandomSource;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.page.BrowserPageModel;
import net.rim.device.apps.internal.browser.page.Page;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.page.QueuePage;
import net.rim.device.apps.internal.browser.store.BrowserFolders;

public final class OfflineQueue {
   private SimpleFolder _queuesFolder = (SimpleFolder)FolderHierarchies.getFolder(
      BrowserFolders.RIM_BROWSER_MESSAGES_HIERARCHY_ID, BrowserFolders.BROWSER_OFFLINE_QUEUES_FOLDER_ID
   );
   private Hashtable _queues;
   public static final String QUEUE_SCHEME = "queue:";
   private static final long OFFLINE_QUEUE_KEY = 6623930195017231157L;

   public OfflineQueue() {
      PersistentObject store = RIMPersistentStore.getPersistentObject(6623930195017231157L);
      Object contents = store.getContents();
      if (contents instanceof Hashtable) {
         this._queues = (Hashtable)contents;
      } else {
         this._queues = new Hashtable();
         this.commit();
      }
   }

   public final void start() {
      Enumeration names = this._queues.keys();

      while (names.hasMoreElements()) {
         synchronized (FolderHierarchies.getLockObject()) {
            ReadableList requests = (ReadableList)this.getQueue((String)names.nextElement());
            if (requests != null) {
               int size = requests.size();

               for (int i = 0; i < size; i++) {
                  PageModel request = (PageModel)requests.getAt(i);
                  if (!request.isCompleted()) {
                     BrowserDaemonRegistry.getInstance().addQueueRequest(new FetchRequest(request));
                  }
               }
            }
         }
      }
   }

   public final synchronized void addItem(String name, PageModel pageModel) {
      long folderId = 0;
      SimpleFolder queueFolder = null;
      Long queueId = (Long)this._queues.get(name);
      synchronized (FolderHierarchies.getLockObject()) {
         if (queueId == null) {
            folderId = BrowserFolders.makeUniqueLUID(this._queuesFolder);
            queueFolder = new SimpleFolder(BrowserFolders.BROWSER_FAMILY, folderId, name, BrowserFolders.BROWSER_FOLDER_COLLECTION_CLASS, this._queuesFolder);
            this._queuesFolder.putFolder(queueFolder);
            this._queues.put(name, new Long(folderId));
            this.commit();
         } else {
            folderId = queueId;
            queueFolder = (SimpleFolder)this._queuesFolder.getFolder(folderId);
         }

         ((BrowserPageModel)pageModel).setFolderId(folderId);
         WritableSet requests = (WritableSet)queueFolder.getContainedItems();
         requests.add(pageModel);
      }
   }

   public final synchronized void removeItem(String name, PageModel pageModel) {
      Long queueId = (Long)this._queues.get(name);
      if (queueId != null) {
         synchronized (FolderHierarchies.getLockObject()) {
            SimpleFolder queueFolder = (SimpleFolder)this._queuesFolder.getFolder(queueId);
            WritableSet requestsW = (WritableSet)queueFolder.getContainedItems();
            ReadableList requestsR = (ReadableList)requestsW;
            requestsW.remove(pageModel);
            if (requestsR.size() == 0) {
               this._queuesFolder.removeSubFolder(queueFolder);
               this._queues.remove(name);
               this.commit();
            }
         }
      }
   }

   public final Collection getQueue(String name) {
      Collection requests = null;
      Long queueId = (Long)this._queues.get(name);
      if (queueId != null) {
         synchronized (FolderHierarchies.getLockObject()) {
            SimpleFolder queueFolder = (SimpleFolder)this._queuesFolder.getFolder(queueId);
            if (queueFolder != null) {
               requests = queueFolder.getContainedItems();
            }

            return requests;
         }
      } else {
         return requests;
      }
   }

   public final synchronized String[] getNames() {
      String[] names = null;
      if (this._queues.isEmpty()) {
         return new String[0];
      }

      names = new String[this._queues.size()];
      Enumeration enumeration = this._queues.keys();
      int index = 0;

      while (enumeration.hasMoreElements()) {
         names[index++] = (String)enumeration.nextElement();
      }

      return names;
   }

   public final int numQueues() {
      return this._queues.size();
   }

   public final Page getPage(FetchRequest fetchRequest) {
      return new QueuePage(fetchRequest);
   }

   private final void commit() {
      PersistentObject store = RIMPersistentStore.getPersistentObject(6623930195017231157L);
      store.setContents(this._queues, 51);
      store.commit();
   }

   public static final String generateRequestId() {
      StringBuffer deviceId = new StringBuffer();
      deviceId.append('B');
      deviceId.append(DeviceInfo.getDeviceId());
      deviceId.append('B');
      StringBuffer requestId = new StringBuffer();
      requestId.append(Math.abs(deviceId.toString().hashCode()));
      requestId.append(Math.abs(RandomSource.getInt()));
      return requestId.toString();
   }

   public static final String generateRequestDate() {
      SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
      return dateFormat.format(Calendar.getInstance());
   }
}
