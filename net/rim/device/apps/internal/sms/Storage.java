package net.rim.device.apps.internal.sms;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.IntRangedActionTarget;
import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.messaging.util.PersistedSortedCollectionEncryptor;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.api.utility.lowMemory.PurgeManager;
import net.rim.device.apps.internal.sms.resources.SMSResources;
import net.rim.device.internal.deviceoptions.SMSOptions;
import net.rim.device.internal.system.RadioInternal;

public final class Storage implements PersistentContentListener {
   public static final long SMS_HIERARCHY_ID;
   public static final long SMS_INBOX;
   public static final long SMS_SIM_INBOX;
   public static final long SMS_OUTBOX;
   public static final long SMS_ORPHANED_SAVED_ITEMS;
   public static final long SMS_FAMILY;
   private static LongKeyProviderAdaptor _longKeyProviderAdaptor = (LongKeyProviderAdaptor)(new Object());
   private static int[] _addressKeys = new int[1];
   private static final long MESSAGE_THREADS_LUID;
   private static final long MESSAGE_THREAD_HASH;
   private static LongHashtable _messageThreads;

   private Storage() {
   }

   public static final LongKeyProviderAdaptor getLongKeyProviderAdaptor() {
      return _longKeyProviderAdaptor;
   }

   static final void registerOnceOnSystemStart() {
      SimpleFolder hierarchy = SimpleFolder.getInstance(-6498019436237624557L, 2937394060276591988L);
      SimpleFolder inboxFolder = null;
      SimpleFolder simInboxFolder = null;
      SimpleFolder outboxFolder = null;
      SimpleFolder orphanedSavedItemsFolder = null;
      if (hierarchy == null) {
         hierarchy = SimpleFolder.createInstance(-6498019436237624557L, 2937394060276591988L, SMSResources.getResourceBundle(), 80, 1);
         inboxFolder = createFolder(hierarchy, 1393133342214151287L, 91, 1);
         outboxFolder = createFolder(hierarchy, -8580923390364260649L, 93, 1);
         orphanedSavedItemsFolder = createFolder(hierarchy, -4468584479793228955L, 100, 0);
         if (SIMCard.isSupported()) {
            simInboxFolder = createFolder(hierarchy, -441701525336570016L, 92, 1);
         }
      } else {
         inboxFolder = (SimpleFolder)hierarchy.getFolder(1393133342214151287L);
         outboxFolder = (SimpleFolder)hierarchy.getFolder(-8580923390364260649L);
         simInboxFolder = (SimpleFolder)hierarchy.getFolder(-441701525336570016L);
         orphanedSavedItemsFolder = (SimpleFolder)hierarchy.getFolder(-4468584479793228955L);
      }

      if (hierarchy != null && inboxFolder != null && outboxFolder != null && orphanedSavedItemsFolder != null) {
         PurgeManager purgeManager = PurgeManager.getInstance();
         FolderHierarchies.registerFolderHierarchy(hierarchy);
         boolean defaultUi = SMSOptions.getMessageListUiId() == 0;
         SMSUtilityFolder utilityFolder = SMSUtilityFolder.getUtilityFolder();
         if (defaultUi) {
            registerMergedFolder(inboxFolder);
            registerMergedFolder(outboxFolder);
         } else {
            registerMergedFolder(utilityFolder);
         }

         FolderMerge.registerMergedFolder(7509894771240321003L, inboxFolder);
         inboxFolder.initializeOnSystemStart();
         ReadableList inboxItems = (ReadableList)inboxFolder.getContainedItems();
         purgeManager.addCollection(inboxItems);
         RadioInternal.smsCountUpdated(inboxItems.size());
         FolderMerge.registerMergedFolder(7509894771240321003L, outboxFolder);
         outboxFolder.initializeOnSystemStart();
         purgeManager.addCollection((Collection)outboxFolder.getContainedItems());
         FolderMerge.registerMergedFolder(6368823655991217730L, orphanedSavedItemsFolder);
         FolderMerge.registerMergedFolder(7509894771240321003L, orphanedSavedItemsFolder);
         orphanedSavedItemsFolder.initializeOnSystemStart();
         if (simInboxFolder != null) {
            if (defaultUi) {
               registerMergedFolder(simInboxFolder);
            }

            FolderMerge.registerMergedFolder(7509894771240321003L, simInboxFolder);
            simInboxFolder.initializeOnSystemStart();
         }

         PersistentContent.addListener(new Storage());
         utilityFolder.addSource(inboxFolder);
         utilityFolder.addSource(outboxFolder);
         if (simInboxFolder != null) {
            utilityFolder.addSource(simInboxFolder);
         }

         utilityFolder.initializeFolder();
      } else {
         SMSService.log(541478470);
      }
   }

   private static final void registerMergedFolder(Folder folder) {
      FolderMerge.registerMergedFolder(-7118119043524803584L, folder);
      FolderMerge.registerMergedFolder(-4696470826620059293L, folder);
   }

   private static final void deregisterMergedFolder(Folder folder) {
      FolderMerge.deregisterMergedFolder(-7118119043524803584L, folder);
      FolderMerge.deregisterMergedFolder(-4696470826620059293L, folder);
   }

   private static final void addToMessageList(Folder folder, boolean add) {
      if (add) {
         FolderMerge.registerMergedFolder(-5581791943352753293L, folder);
         FolderMerge.registerMergedFolder(2993144521330132876L, folder);
      } else {
         FolderMerge.deregisterMergedFolder(-5581791943352753293L, folder);
         FolderMerge.deregisterMergedFolder(2993144521330132876L, folder);
      }
   }

   static final void addToMessageList(boolean add) {
      boolean convUI = SMSOptions.getMessageListUiId() != 0;
      addToMessageList(convUI, add);
   }

   private static final void addToMessageList(boolean convUI, boolean add) {
      if (convUI) {
         SMSUtilityFolder utilityFolder = SMSUtilityFolder.getUtilityFolder();
         addToMessageList(utilityFolder, add);
      } else {
         SimpleFolder hierarchy = SimpleFolder.getInstance(-6498019436237624557L, 2937394060276591988L);
         SimpleFolder inboxFolder = (SimpleFolder)hierarchy.getFolder(1393133342214151287L);
         SimpleFolder outboxFolder = (SimpleFolder)hierarchy.getFolder(-8580923390364260649L);
         SimpleFolder simInboxFolder = (SimpleFolder)hierarchy.getFolder(-441701525336570016L);
         addToMessageList(inboxFolder, add);
         addToMessageList(outboxFolder, add);
         if (simInboxFolder != null) {
            addToMessageList(simInboxFolder, add);
         }
      }
   }

   private static final SimpleFolder createFolder(SimpleFolder hierarchy, long folderId, int folderName, int flags) {
      SimpleFolder folder = (SimpleFolder)(new Object(
         -6498019436237624557L,
         folderId,
         SMSResources.getResourceBundle(),
         folderName,
         "net.rim.device.apps.api.messaging.util.PersistedSortedCollection",
         hierarchy,
         flags
      ));
      hierarchy.putFolder(folder);
      return folder;
   }

   public static final void changeMesssageListSMSUi() {
      boolean convUI = SMSOptions.getMessageListUiId() != 0;
      SimpleFolder hierarchy = SimpleFolder.getInstance(-6498019436237624557L, 2937394060276591988L);
      SimpleFolder inboxFolder = (SimpleFolder)hierarchy.getFolder(1393133342214151287L);
      SimpleFolder outboxFolder = (SimpleFolder)hierarchy.getFolder(-8580923390364260649L);
      SimpleFolder simInboxFolder = (SimpleFolder)hierarchy.getFolder(-441701525336570016L);
      SMSUtilityFolder utilityFolder = SMSUtilityFolder.getUtilityFolder();
      if (convUI) {
         deregisterMergedFolder(inboxFolder);
         deregisterMergedFolder(outboxFolder);
         if (simInboxFolder != null) {
            deregisterMergedFolder(simInboxFolder);
         }

         utilityFolder.clearFolder();
         utilityFolder.initializeFolder();
         registerMergedFolder(utilityFolder);
      } else {
         deregisterMergedFolder(utilityFolder);
         registerMergedFolder(inboxFolder);
         registerMergedFolder(outboxFolder);
         if (simInboxFolder != null) {
            registerMergedFolder(simInboxFolder);
         }
      }

      addToMessageList(!convUI, false);
      addToMessageList(convUI, true);
   }

   static final void removeAll() {
      synchronized (FolderHierarchies.getLockObject()) {
         emptyFolder(1393133342214151287L);
         emptyFolder(-8580923390364260649L);
         emptyFolder(-4468584479793228955L);
         LongHashtable messageThreads = getMessageThreads();
         if (messageThreads != null) {
            _messageThreads.clear();
         }
      }
   }

   static final void removeSIMMessages() {
      synchronized (FolderHierarchies.getLockObject()) {
         emptyFolder(-441701525336570016L);
         LongHashtable messageThreads = getMessageThreads();
         if (messageThreads != null) {
            _messageThreads.clear();
         }
      }
   }

   static final Folder getSMSFolder(long id) {
      return FolderHierarchies.getFolder(2937394060276591988L, id);
   }

   private static final void emptyFolder(long folder) {
      Folder messageFolder = getSMSFolder(folder);
      if (messageFolder != null) {
         IntRangedActionTarget target = (IntRangedActionTarget)messageFolder.getContainedItems();
         target.apply(Integer.MIN_VALUE, Integer.MAX_VALUE, -198247372487919817L, null);
         ((WritableSet)target).removeAll();
      }
   }

   public static final long getFolderForMessage(SMSModel message) {
      if (message.flagsSet(8)) {
         return -4468584479793228955L;
      } else {
         return message.inbound() ? 1393133342214151287L : -8580923390364260649L;
      }
   }

   public static final void fileMessage(SMSModel message, long folderId) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   public static final boolean isFiled(SMSModel message) {
      return message._folderId != 0;
   }

   public static final boolean moveMessage(SMSModel message, long fromFolderId, long toFolderId) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   public static final void removeMessage(SMSModel message) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   static final long getAddressHash(Object threadAddress) {
      int hash;
      if (threadAddress instanceof Object && threadAddress instanceof Object) {
         KeyProvider keyProvider = (KeyProvider)threadAddress;
         synchronized (_addressKeys) {
            keyProvider.getKeys(null, _addressKeys, 0, -4145532165335996154L);
            hash = _addressKeys[0];
         }
      } else {
         hash = threadAddress.hashCode();
      }

      return -7647744941858359268L + hash;
   }

   static final synchronized LongHashtable getMessageThreads() {
      if (_messageThreads == null) {
         _messageThreads = ApplicationRegistry.getApplicationRegistry().getLongHashtable(8379405910859600340L);
      }

      return _messageThreads;
   }

   public static final MessageThread getMessageThread(Object address) {
      synchronized (FolderHierarchies.getLockObject()) {
         LongHashtable messageThreads = getMessageThreads();
         long hash = getAddressHash(address);
         MessageThread messageThread = (MessageThread)messageThreads.get(hash);
         if (messageThread == null) {
            messageThread = new MessageThread(hash);
            messageThreads.put(hash, messageThread);
            addFolderToMessageThread(messageThread, 1393133342214151287L);
            addFolderToMessageThread(messageThread, -441701525336570016L);
            addFolderToMessageThread(messageThread, -8580923390364260649L);
            addFolderToMessageThread(messageThread, -4468584479793228955L);
            messageThread.addCollectionListener(SMSUtilityFolder.getUtilityFolder());
         }

         return messageThread;
      }
   }

   private static final void addFolderToMessageThread(MessageThread messageThread, long folder) {
      Folder messageFolder = getSMSFolder(folder);
      if (messageFolder != null) {
         CollectionEventSource eventSource = (CollectionEventSource)messageFolder.getContainedItems();
         eventSource.addCollectionListener(messageThread);
         messageThread.addSource(eventSource);
      }
   }

   public static final long assignFolderIdForMessage(SMSModel model, Object context) {
      return model._folderId == 0 ? -8580923390364260649L : model._folderId;
   }

   @Override
   public final void persistentContentStateChanged(int state) {
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      PersistedSortedCollectionEncryptor.crypt(-6498019436237624557L, generation);
   }
}
