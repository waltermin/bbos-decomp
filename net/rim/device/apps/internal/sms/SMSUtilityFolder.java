package net.rim.device.apps.internal.sms;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.model.KeyUtilities;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.util.SortedCollection;

public class SMSUtilityFolder extends SortedCollection implements Folder, CollectionListener {
   LongHashtable _messageEntries;
   private static SMSUtilityFolder _utilityFolderAllItems;
   private static final long SMS_UTILITY_FOLDER_ALL = -8262673811206753019L;

   private SMSUtilityFolder() {
      super.initialize(-6498019436237624557L, -8262673811206753019L, Storage.getLongKeyProviderAdaptor(), null);
      this._messageEntries = new LongHashtable();
   }

   public static SMSUtilityFolder getUtilityFolder() {
      if (_utilityFolderAllItems == null) {
         ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
         _utilityFolderAllItems = (SMSUtilityFolder)registry.get(-8262673811206753019L);
         if (_utilityFolderAllItems == null) {
            _utilityFolderAllItems = new SMSUtilityFolder();
            registry.put(-8262673811206753019L, _utilityFolderAllItems);
         }
      }

      return _utilityFolderAllItems;
   }

   void addSource(Folder source) {
      Collection collection = source.getContainedItems();
      if (collection instanceof SortedCollection) {
         SortedCollection sourceCollection = (SortedCollection)collection;
         int num = sourceCollection.size();

         for (int index = 0; index < num; index++) {
            Object element = sourceCollection.getAt(index);
            if (element instanceof SMSModel) {
               SMSModel model = (SMSModel)element;
               Storage.getMessageThread(model._payload.getFirstAddress());
            }
         }
      }
   }

   void initializeFolder() {
      synchronized (FolderHierarchies.getLockObject()) {
         LongHashtable threadsTable = Storage.getMessageThreads();
         if (threadsTable != null) {
            Enumeration messageThreads = threadsTable.elements();

            while (messageThreads.hasMoreElements()) {
               MessageThread current = (MessageThread)messageThreads.nextElement();
               int nextItem = KeyUtilities.mapKeyToIndex(current, Storage.getLongKeyProviderAdaptor(), System.currentTimeMillis());
               if (nextItem >= 0) {
                  SMSModel latest = null;
                  boolean recentMessageThread = true;

                  for (int i = nextItem - 1; i >= 0; i--) {
                     Object element = current.getAt(i);
                     if (element instanceof SMSModel) {
                        SMSModel model = (SMSModel)element;
                        if (!model.flagsSet(8)) {
                           if (latest == null || (!latest.inbound() || !latest.flagsSet(1)) && model.inbound() && !model.flagsSet(1)) {
                              latest = (SMSModel)element;
                           }

                           if ((model.getFlags() & 32) != 0) {
                              this.add(latest);
                              if (recentMessageThread) {
                                 this._messageEntries.put(Storage.getAddressHash(latest._payload.getFirstAddress()), latest);
                                 recentMessageThread = false;
                              }

                              latest = null;
                           }
                        }
                     }
                  }

                  if (latest != null) {
                     this.add(latest);
                     if (recentMessageThread) {
                        this._messageEntries.put(Storage.getAddressHash(latest._payload.getFirstAddress()), latest);
                        recentMessageThread = false;
                     }

                     Object var13 = null;
                  }
               }
            }
         }
      }
   }

   public void clearFolder() {
      this._messageEntries.clear();
      this.removeAll();
   }

   @Override
   public long getLUID() {
      return -8262673811206753019L;
   }

   @Override
   public String getFriendlyName() {
      return null;
   }

   @Override
   public Folder getFolder(long folderUid) {
      return folderUid == -8262673811206753019L ? this : null;
   }

   @Override
   public Folder getParentFolder() {
      return null;
   }

   @Override
   public Enumeration getSubFolders() {
      return null;
   }

   @Override
   public boolean containsSubFolders() {
      return false;
   }

   @Override
   public Collection getContainedItems() {
      return this;
   }

   @Override
   public Folder getBaseFolder() {
      return null;
   }

   @Override
   public boolean canContainItems() {
      return true;
   }

   @Override
   public boolean isVisible(Object context) {
      return false;
   }

   @Override
   public void reset(Collection collection) {
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      if (element instanceof SMSModel) {
         SMSModel newModel = (SMSModel)element;
         this.setModelToRepresentThread(newModel);
      }
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      if (newElement instanceof SMSModel) {
         SMSModel updatedModel = (SMSModel)newElement;
         this.setModelToRepresentThread(updatedModel);
      }
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      if (element instanceof SMSModel) {
         SMSModel removedModel = (SMSModel)element;
         int index = this.getIndex(removedModel);
         if (index != -1) {
            MessageThread messageThread = Storage.getMessageThread(removedModel._payload.getFirstAddress());
            this.remove(removedModel);
            this._messageEntries.remove(Storage.getAddressHash(removedModel._payload.getFirstAddress()));
            if (!removedModel.flagsSet(32)) {
               int nextItem = KeyUtilities.mapKeyToIndex(messageThread, Storage.getLongKeyProviderAdaptor(), removedModel._payload._creationDate);
               if (nextItem > 0) {
                  this.setModelToRepresentThread((SMSModel)messageThread.getAt(nextItem - 1));
               }
            }
         }
      }
   }

   private synchronized void setModelToRepresentThread(SMSModel model) {
      long hash = Storage.getAddressHash(model._payload.getFirstAddress());
      MessageThread messageThread = Storage.getMessageThread(model._payload.getFirstAddress());
      int nextItem = KeyUtilities.mapKeyToIndex(messageThread, Storage.getLongKeyProviderAdaptor(), model._payload._creationDate);
      if (nextItem < 0) {
         nextItem = 0;
      }

      int i;
      for (i = nextItem; i < messageThread.size(); i++) {
         SMSModel current = (SMSModel)messageThread.getAt(i);
         if (current != model && current.flagsSet(32)) {
            i--;
            break;
         }
      }

      if (i >= messageThread.size()) {
         i = messageThread.size() - 1;
      }

      SMSModel latest = null;
      boolean recentMessageThread = false;

      while (i >= 0) {
         Object element = messageThread.getAt(i);
         if (element instanceof SMSModel) {
            SMSModel current = (SMSModel)element;
            if (this.getIndex(current) != -1) {
               this.remove(current);
               if (this._messageEntries.get(hash) != null) {
                  recentMessageThread = true;
               }
            }

            if (!current.flagsSet(8)) {
               if (latest == null || (!latest.inbound() || latest.flagsSet(1)) && ((SMSModel)element).inbound() && !((SMSModel)element).flagsSet(1)) {
                  latest = current;
               }

               if (current.flagsSet(32)) {
                  break;
               }
            }
         }

         i--;
      }

      if (latest != null) {
         this.add(latest);
         if (recentMessageThread) {
            this._messageEntries.put(Storage.getAddressHash(latest._payload.getFirstAddress()), latest);
         }

         SMSModel var12 = null;
      }
   }
}
