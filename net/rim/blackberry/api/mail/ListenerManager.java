package net.rim.blackberry.api.mail;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.blackberry.api.mail.event.DefaultSessionListener;
import net.rim.blackberry.api.mail.event.EventListener;
import net.rim.blackberry.api.mail.event.FolderEvent;
import net.rim.blackberry.api.mail.event.FolderListener;
import net.rim.blackberry.api.mail.event.MessageEvent;
import net.rim.blackberry.api.mail.event.MessageListener;
import net.rim.blackberry.api.mail.event.ServiceListener;
import net.rim.blackberry.api.mail.event.StoreEvent;
import net.rim.blackberry.api.mail.event.StoreListener;
import net.rim.blackberry.api.mail.event.ViewListener;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.ModelViewListener;
import net.rim.device.apps.api.framework.registration.ModelViewListenerRegistry;
import net.rim.device.apps.api.framework.registration.OpenViewer;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.messaging.MergedCollection;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceListener;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.api.transmission.rim.RIMMessagingService;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModelImpl;
import net.rim.device.apps.internal.blackberryemail.email.EmailPayloadModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailSendListener;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMessageUtilities;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolderListener;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.blackberryemail.folder.PreverifierListener;
import net.rim.device.apps.internal.blackberryemail.properties.TransitoryMessagePropertiesModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.vm.ClassInfo;

final class ListenerManager
   implements CollectionListener,
   ModelViewListener,
   EmailFolderListener,
   PreverifierListener,
   EmailSendListener,
   TransmissionServiceListener {
   private LongHashtable _storeIds = new LongHashtable();
   private LongHashtable _folderIds = new LongHashtable();
   private LongHashtable _messageIds = new LongHashtable();
   private Vector _storeListeners = new Vector();
   private Vector _viewListeners = new Vector();
   private MergedCollection _mergedCollection;
   private IntHashtable _serviceListeners = new IntHashtable();
   private LongHashtable _sendListeners = new LongHashtable();
   private Vector _defaultSessionListeners = new Vector();
   private static final long ID = 4422687540963743150L;

   public final boolean removeEmailSendListener(long serviceUidHash, SendListener l) {
      Vector v = (Vector)this._sendListeners.get(serviceUidHash);
      return v == null ? false : v.removeElement(l);
   }

   public final boolean removeMessageListener(long cmimeId, MessageListener l) {
      return this.removeListener(cmimeId, this._messageIds, l);
   }

   public final void removeViewListener(ViewListener vl) {
      this._viewListeners.removeElement(vl);
   }

   public final void addViewListener(ViewListener vl) {
      if (!this._viewListeners.contains(vl)) {
         this._viewListeners.addElement(vl);
      }
   }

   public final void addMessageListener(long cmimeId, MessageListener l) {
      this.addListener(cmimeId, this._messageIds, l);
   }

   public final boolean removeFolderListener(EmailFolder f, FolderListener l) {
      boolean retval = this.removeListener(f.getLUID(), this._folderIds, l);
      Enumeration e = f.getSubFolders();

      while (e.hasMoreElements()) {
         EmailFolder folder = (EmailFolder)e.nextElement();
         retval &= this.removeFolderListener(folder, l);
      }

      return retval;
   }

   public final void update(EmailMessageModel m) {
      long mid = m.getUID();
      long fid = m.getFolderId();
      Vector list = (Vector)this._messageIds.get(mid);
      this.doMessageEventDispatch(list, fid, m, 1);
   }

   public final void addStoreListener(StoreListener l) {
      if (!this._storeListeners.contains(l)) {
         this._storeListeners.addElement(l);
      }
   }

   public final void removeStoreListener(StoreListener l) {
      this._storeListeners.removeElement(l);
   }

   public final void addDefaultSessionListener(DefaultSessionListener l) {
      if (!this._defaultSessionListeners.contains(l)) {
         this._defaultSessionListeners.addElement(l);
      }
   }

   public final void removeDefaultSessionListener(DefaultSessionListener l) {
      this._defaultSessionListeners.removeElement(l);
   }

   public final void addStoreFolderListener(int serviceUidHash, FolderListener l) {
      this.addListener(serviceUidHash, this._storeIds, l);
   }

   public final void addEmailSendListener(int serviceUidHash, SendListener l) {
      Vector v = (Vector)this._sendListeners.get(serviceUidHash);
      if (v == null) {
         v = new Vector();
      }

      if (!v.contains(l)) {
         v.addElement(l);
         this._sendListeners.put(serviceUidHash, v);
      }
   }

   public final void removeStoreFolderListener(int serviceUidHash, FolderListener l) {
      this.removeListener(serviceUidHash, this._storeIds, l);
   }

   public final void addFolderListener(EmailFolder f, FolderListener l) {
      this.addListener(f.getLUID(), this._folderIds, l);
      Enumeration e = f.getSubFolders();

      while (e.hasMoreElements()) {
         EmailFolder folder = (EmailFolder)e.nextElement();
         this.addFolderListener(folder, l);
      }
   }

   @Override
   public final void reset(Collection collection) {
      try {
         DefaultService ds = DefaultService.getInstance();

         for (int i = this._storeListeners.size() - 1; i >= 0; i--) {
            StoreEvent se = new StoreEvent(ds, 1);
            se.dispatch((StoreListener)this._storeListeners.elementAt(i));
         }
      } catch (NoSuchServiceException e) {
         EventLogger.logEvent(DefaultService.EVENT_LOGGER_GUID, e.toString().getBytes(), 2);
      }
   }

   @Override
   public final boolean onSend(EmailMessageModel message) {
      EmailMessageModelImpl emailMsg = (EmailMessageModelImpl)message;
      ServiceRecord serviceRecord = EmailMessageUtilities.getServiceRecordForMessage(emailMsg);
      if (serviceRecord == null) {
         return true;
      }

      long serviceUID = serviceRecord.getUidHash();
      Message msg = new Message(emailMsg);
      Vector v = (Vector)this._sendListeners.get(serviceUID);
      if (v != null && v.size() != 0) {
         for (int i = v.size() - 1; i >= 0; i--) {
            SendListener l = (SendListener)v.elementAt(i);
            if (!l.sendMessage(msg)) {
               Class clazz = l.getClass();
               String moduleName = ClassInfo.getModuleInfo(clazz, 0, true);
               ApplicationDescriptor[] appDescs = CodeModuleManager.getApplicationDescriptors(CodeModuleManager.getModuleHandle(moduleName));
               String applicationName = null;
               if (appDescs != null && appDescs.length > 0) {
                  applicationName = appDescs[0].getName();
               } else {
                  applicationName = moduleName;
               }

               emailMsg.changeStatus(0, 0, 8191, 129, false, false, false, false, null);
               String refuseMessage = EmailResources.getString(207);
               emailMsg.setTransmissionErrorMessage(refuseMessage + " " + applicationName);
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (newElement instanceof EmailMessageModel && collection == this._mergedCollection) {
         EmailMessageModel m = (EmailMessageModel)newElement;
         this.update(m);
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      if (element instanceof EmailMessageModel && collection == this._mergedCollection) {
         EmailMessageModel m = (EmailMessageModel)element;
         this.dispatchFolderEvent(2, m);
      }
   }

   @Override
   public final boolean excludeMessage(EmailMessageModel m) {
      this.dispatchFolderEvent(1, m);
      if ((m.getFlags() & 262144) != 0) {
         return true;
      }

      WritableSet collection = (WritableSet)EmailHierarchy.getStorageCollection(m.getFolderId(), m.flagsSet(2));
      return collection != null && collection.contains(m);
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
   }

   @Override
   public final void modelOpened(OpenViewer openViewer, Object context) {
      this.handleModelViewListenerEvents(2, openViewer, context);
      if (ContextObject.getFlag(context, 31)) {
         this.handleModelViewListenerEvents(4, openViewer, context);
      }

      if (ContextObject.getFlag(context, 53) || ContextObject.getFlag(context, 29) || ContextObject.getFlag(context, 30)) {
         this.handleModelViewListenerEvents(6, openViewer, context);
      }

      if (ContextObject.getFlag(context, 13)) {
         this.handleModelViewListenerEvents(5, openViewer, context);
      }
   }

   @Override
   public final void modelClosed(OpenViewer openViewer, Object context) {
      this.handleModelViewListenerEvents(3, openViewer, context);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void folderAdded(EmailFolder ef) {
      int uidHash = ef.getEmailHierarchy().getServiceUidHash();
      synchronized (this) {
         Vector v = (Vector)this._serviceListeners.get(uidHash);
         if (v != null) {
            Folder f = new Folder(ef);

            for (int i = v.size() - 1; i >= 0; i--) {
               try {
                  ServiceListener sl = (ServiceListener)v.elementAt(i);
                  sl.folderCreated(new FolderEvent(f, 3, null));
               } catch (Throwable var11) {
                  System.err.println(e);
                  continue;
               }
            }
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void folderRemoved(EmailFolder ef) {
      int uidHash = ef.getEmailHierarchy().getServiceUidHash();
      int userId = ef.getEmailHierarchy().getServiceUserId();
      int key = -1;
      if (userId != -1) {
         key = userId;
      } else {
         key = uidHash;
      }

      synchronized (this) {
         Vector v = (Vector)this._serviceListeners.get(key);
         if (v != null) {
            Folder f = new Folder(ef);

            for (int i = v.size() - 1; i >= 0; i--) {
               try {
                  ServiceListener sl = (ServiceListener)v.elementAt(i);
                  sl.folderDeleted(new FolderEvent(f, 4, null));
               } catch (Throwable var13) {
                  System.err.println(e);
                  continue;
               }
            }
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void folderUpdated(EmailFolder ef) {
      int uidHash = ef.getEmailHierarchy().getServiceUidHash();
      int userId = ef.getEmailHierarchy().getServiceUserId();
      int key = -1;
      if (userId != -1) {
         key = userId;
      } else {
         key = uidHash;
      }

      synchronized (this) {
         Vector v = (Vector)this._serviceListeners.get(key);
         if (v != null) {
            Folder f = new Folder(ef);

            for (int i = v.size() - 1; i >= 0; i--) {
               try {
                  ServiceListener sl = (ServiceListener)v.elementAt(i);
                  sl.folderUpdated(new FolderEvent(f, 5, null));
               } catch (Throwable var13) {
                  System.err.println(e);
                  continue;
               }
            }
         }
      }
   }

   @Override
   public final boolean receiveObject(TransmissionService aTransmissionService, Object transmissionObject, Object contextObject) {
      return false;
   }

   @Override
   public final void statusChanged(TransmissionService aTransmissionService, int statusInt, Object contextObject) {
      for (int i = this._defaultSessionListeners.size() - 1; i >= 0; i--) {
         DefaultSessionListener listener = (DefaultSessionListener)this._defaultSessionListeners.elementAt(i);
         listener.defaultSessionChanged();
      }
   }

   public static final ListenerManager getInstance() {
      synchronized (FolderHierarchies.getLockObject()) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         ListenerManager var10000;
         synchronized (ar) {
            ListenerManager lm = (ListenerManager)ar.get(4422687540963743150L);
            if (lm == null) {
               lm = new ListenerManager();
               ar.put(4422687540963743150L, lm);
            }

            var10000 = lm;
         }

         return var10000;
      }
   }

   private final void addListener(long id, LongHashtable map, EventListener listener) {
      Vector v = (Vector)map.get(id);
      if (v == null) {
         v = new Vector();
      }

      if (!v.contains(listener)) {
         v.addElement(listener);
         map.put(id, v);
      }
   }

   private final boolean removeListener(long id, LongHashtable map, EventListener listener) {
      Vector v = (Vector)map.get(id);
      return v == null ? false : v.removeElement(listener);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void doMessageEventDispatch(Vector list, long fid, EmailMessageModel m, int type) {
      if (list != null) {
         try {
            Folder f = new Folder(this.getEmailFolder(fid, m));
            Message msg = new Message(f, m);
            MessageEvent e = new MessageEvent(type, msg);

            for (int i = list.size() - 1; i >= 0; i--) {
               EventListener l = (EventListener)list.elementAt(i);
               e.dispatch(l);
            }
         } catch (Throwable var12) {
            EventLogger.logEvent(DefaultService.EVENT_LOGGER_GUID, t.toString().getBytes(), 2);
            t.printStackTrace();
            return;
         }
      }
   }

   private final EmailFolder getEmailFolder(long folderId, EmailMessageModel m) {
      EmailFolder ef = EmailHierarchy.getEmailFolder(folderId);
      if (ef == null) {
         EmailHierarchy eh = EmailHierarchy.getEmailHierarchyForFolder(folderId);
         if (eh == null) {
            eh = EmailHierarchy.getAnonymousEmailHierarchy();
         }

         EmailPayloadModel epm = m.getPayload();
         ef = EmailHierarchy.getEmailFolder(eh.getEmailFolder(0, epm.inbound() ? 2 : 3));
      }

      return ef;
   }

   private final void handleModelViewListenerEvents(int eventId, OpenViewer openViewer, Object context) {
      if (this._viewListeners.size() > 0) {
         Object o = null;
         if (openViewer instanceof ModelScreen) {
            ModelScreen ms = (ModelScreen)openViewer;

            label57:
            try {
               o = ms.getModel(false);
            } finally {
               break label57;
            }

            if (!(o instanceof EmailMessageModel)) {
               o = ContextObject.get(context, 250);
            }

            if (!(o instanceof EmailMessageModel)) {
               o = ContextObject.get(context, 254);
            }

            if (!(o instanceof EmailMessageModel)) {
               o = ContextObject.get(context, 32241034113959076L);
               if (o instanceof TransitoryMessagePropertiesModel) {
                  TransitoryMessagePropertiesModel tmpm = (TransitoryMessagePropertiesModel)o;
                  o = tmpm.getEmailMessageModel();
               }
            }
         }

         if (o instanceof EmailMessageModel) {
            EmailMessageModel emm = (EmailMessageModel)o;
            this.doMessageEventDispatch(this._viewListeners, emm.getFolderId(), emm, eventId);
         }
      }
   }

   static final void addServiceListener(ServiceListener sl, Service s) {
      ListenerManager lm = getInstance();
      synchronized (lm) {
         ServiceConfiguration sc = s.getServiceConfiguration();
         int uidHash = sc.getServiceRecord().getUidHash();
         int userId = sc.getServiceRecord().getUserId();
         int key = -1;
         if (userId != -1) {
            key = userId;
         } else {
            key = uidHash;
         }

         Vector v = (Vector)lm._serviceListeners.get(key);
         if (v == null) {
            v = new Vector();
            lm._serviceListeners.put(key, v);
         }

         v.addElement(sl);
      }
   }

   static final void removeServiceListener(ServiceListener sl, Service s) {
      ListenerManager lm = getInstance();
      synchronized (lm) {
         ServiceConfiguration sc = s.getServiceConfiguration();
         int uidHash = sc.getServiceRecord().getUidHash();
         int userId = sc.getServiceRecord().getUserId();
         int key = -1;
         if (userId != -1) {
            key = userId;
         } else {
            key = uidHash;
         }

         Vector v = (Vector)lm._serviceListeners.get(key);
         if (v != null) {
            v.removeElement(sl);
            if (v.size() == 0) {
               lm._serviceListeners.remove(key);
            }
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void doDispatch(Vector listeners, long folderId, EmailMessageModel m, int type) {
      if (listeners != null) {
         try {
            Folder f = new Folder(this.getEmailFolder(folderId, m));
            Message msg = new Message(f, m);
            FolderEvent e = new FolderEvent(f, type, msg);

            for (int i = listeners.size() - 1; i >= 0; i--) {
               FolderListener l = (FolderListener)listeners.elementAt(i);
               e.dispatch(l);
            }
         } catch (Throwable var12) {
            EventLogger.logEvent(DefaultService.EVENT_LOGGER_GUID, t.toString().getBytes(), 2);
            t.printStackTrace();
            return;
         }
      }
   }

   private final void dispatchFolderEvent(int type, EmailMessageModel m) {
      long fid = m.getFolderId();
      EmailHierarchy emailHierarchy = EmailHierarchy.getEmailHierarchyForFolder(fid);
      net.rim.device.apps.api.messaging.Folder eFolder = null;
      if (emailHierarchy != null) {
         eFolder = emailHierarchy.getFolder(fid);
      }

      if (eFolder == null && m.inbound()) {
         fid = emailHierarchy.getInboxFolder();
      }

      Vector list = (Vector)this._folderIds.get(fid);
      this.doDispatch(list, fid, m, type);
      EmailHierarchy eh = EmailHierarchy.getEmailHierarchyForFolder(fid);
      list = (Vector)this._storeIds.get(eh.getServiceUidHash());
      this.doDispatch(list, fid, m, type);
   }

   private final void instanceRegister() {
      this._mergedCollection = (MergedCollection)FolderMerge.getMergeCollection(-5581791943352753293L);
      this._mergedCollection.addCollectionListener(this);
      MailApiPersistentProxyModelStore store = MailApiPersistentProxyModelStore.getInstance();
      store.addCollectionEventSource(this._mergedCollection);
      ModelViewListenerRegistry.addModelViewListener(-6822293833372928884L, this);
      EmailHierarchy.addEmailFolderListener(this);
      EmailHierarchy.addPreverifyListener(this);
      EmailSendUtility.addSendListeners(this);
      RIMMessagingService s = (RIMMessagingService)TransmissionServiceManager.get(8399767144006445082L);
      if (s != null) {
         s.addTransmissionServiceListener("BBMAILAPI", 100, this);
      }
   }

   private ListenerManager() {
   }

   public static final void register() {
      ListenerManager lm = getInstance();
      lm.instanceRegister();
   }
}
