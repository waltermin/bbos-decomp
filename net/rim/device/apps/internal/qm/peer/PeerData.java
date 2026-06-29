package net.rim.device.apps.internal.qm.peer;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.util.BigVector;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.LongHashtable;

final class PeerData {
   private static final long PERSISTENCE_ID;
   private static final long PERSISTED_CONVERSATIONS;
   private static final long BRANCH_TOGGLE_STATE;
   private static final long CONTACT_LIST_BRANCH_TOGGLE_STATE;
   private static final long USER_STATUS_KEY;
   private static final long USER_NAME_KEY;
   private static final long PASSWORD_KEY;
   private static final long CONTACT_ID_KEY;
   private static final long FLAGS_KEY;
   private static final long REQUESTS_KEY;
   static final long DEFAULT_CONTACT_LIST_COLLECTION;
   static final long AUDIT_DATA;
   private static final int VIBRATE_ON_BUZZ;
   private static final int ASK_INVITE_QUESTION;
   private static final int ALLOW_FORWARD_INVITE;
   private static final int SHOW_CONTACT_CARD;
   private static final int MESSAGE_LIST_INTEGRATION;
   private static final int FLAGS_DEFAULT;
   private static LongHashtable _hashtable;
   private static PersistentObject _persistentObject = RIMPersistentStore.getPersistentObject(7428109662273460978L);
   private static PeerData$PeerSyncItem _syncItem;

   private static final void commit(boolean syncDataChanged) {
      _persistentObject.commit();
      if (syncDataChanged) {
         _syncItem.fireSyncItemUpdated();
      }
   }

   static final void lock() {
      Enumeration enumeration = _hashtable.elements();

      while (enumeration.hasMoreElements()) {
         Object obj = enumeration.nextElement();
         if (obj instanceof Object) {
            _hashtable.put(_hashtable.getKey(obj), PersistentContent.reEncode(obj, true, true));
         }
      }

      Object obj = _hashtable.get(-8344431410952200744L);
      if (obj instanceof Object) {
         Vector conversations = (Vector)obj;

         for (int i = conversations.size() - 1; i >= 0; i--) {
            lockConversation((IntHashtable)conversations.elementAt(i));
         }
      }

      commit(false);
   }

   private static final void lockConversation(IntHashtable conversation) {
      Object uniqueId = conversation.get(1);
      uniqueId = PersistentContent.reEncode(uniqueId, true, true);
      conversation.put(1, uniqueId);
      Vector messages = (Vector)conversation.get(6);

      for (int i = messages.size() - 1; i >= 0; i--) {
         MessengerMessageImpl.lock((IntHashtable)messages.elementAt(i));
      }

      Enumeration elements = ((IntHashtable)conversation.get(3)).elements();

      while (elements.hasMoreElements()) {
         IntHashtable data = (IntHashtable)elements.nextElement();
         Object id = PersistentContent.reEncode(data.get(3), true, true);
         Object name = PersistentContent.reEncode(data.get(2), true, true);
         data.put(3, id);
         data.put(2, name);
      }
   }

   static final void updateSyncItem(CollectionListener listener) {
      PeerData$PeerSyncItem.access$000(_syncItem, listener);
   }

   static final Object getPersistedConversations() {
      return _hashtable.get(-8344431410952200744L);
   }

   static final void setPersistedConversations(Vector conversations) {
      _hashtable.put(-8344431410952200744L, conversations);
      commit(false);
   }

   static final boolean isExpanded(PeerContactList group) {
      return isExpanded(-534643115206180287L, group.getIdHash());
   }

   static final void setExpanded(PeerContactList group, boolean expanded) {
      setExpanded(-534643115206180287L, group.getIdHash(), expanded);
   }

   static final boolean isExpanded(int branchId) {
      return isExpanded(9016387446862379904L, branchId);
   }

   static final void setExpanded(int branchId, boolean expanded) {
      setExpanded(9016387446862379904L, branchId, expanded);
   }

   static final boolean isExpanded(long collectionId, int branchId) {
      boolean result = true;
      Object obj = _hashtable.get(collectionId);
      if (obj instanceof Object) {
         result = ((IntIntHashtable)obj).get(branchId) != -1;
      }

      return result;
   }

   static final void setExpanded(long collectionId, int branchId, boolean expanded) {
      Object obj = _hashtable.get(collectionId);
      IntIntHashtable hashtable = null;
      if (!(obj instanceof Object)) {
         hashtable = (IntIntHashtable)(new Object());
         _hashtable.put(collectionId, hashtable);
      } else {
         hashtable = (IntIntHashtable)obj;
      }

      if (expanded) {
         hashtable.put(branchId, 1);
      } else {
         hashtable.remove(branchId);
      }

      commit(false);
   }

   private static final int getInt(long key, int def) {
      Integer flags = (Integer)_hashtable.get(key);
      if (flags == null) {
         flags = (Integer)(new Object(def));
      }

      return flags;
   }

   private static final void saveInt(long key, int value) {
      _hashtable.put(key, new Object(value));
   }

   private static final boolean checkFlag(long key, int defaultValue, int mask) {
      return (getInt(key, defaultValue) & mask) == mask;
   }

   private static final void saveFlag(long key, int defaultValue, int mask, boolean b) {
      int flags = getInt(key, defaultValue);
      if (b) {
         flags |= mask;
      } else {
         flags &= ~mask;
      }

      saveInt(key, flags);
   }

   static final BigVector getContactListCollectionData(long pclcId) {
      Object obj = _hashtable.get(pclcId);
      return (BigVector)(!(obj instanceof Object) ? null : obj);
   }

   static final void addContactListCollectionData(long pclcId, BigVector pclc) {
      _hashtable.put(pclcId, pclc);
      commit(true);
   }

   static final IntHashtable getAuditData() {
      return (IntHashtable)_hashtable.get(1166674198523868859L);
   }

   static final void setAuditData(IntHashtable data) {
      _hashtable.put(1166674198523868859L, data);
      commit(true);
   }

   static final IntHashtable getUserStatus() {
      return (IntHashtable)_hashtable.get(1547662524332108162L);
   }

   static final void setUserStatus(IntHashtable userStatus) {
      _hashtable.put(1547662524332108162L, userStatus);
      commit(false);
   }

   static final String getUserName() {
      try {
         return PersistentContent.decodeString(_hashtable.get(-7708773143185203648L));
      } finally {
         ;
      }
   }

   static final void saveUserName(String name) {
      _hashtable.put(-7708773143185203648L, name);
      commit(true);
   }

   static final String getPasswordKey() {
      try {
         String key = PersistentContent.decodeString(_hashtable.get(1033784522318989540L));
         return key != null ? key : "1234";
      } finally {
         ;
      }
   }

   static final void savePasswordKey(String password) {
      _hashtable.put(1033784522318989540L, password);
      commit(true);
   }

   static final String getPin() {
      try {
         return PersistentContent.decodeString(_hashtable.get(-1021583542955865241L));
      } finally {
         ;
      }
   }

   static final void savePin(String pin) {
      _hashtable.put(-1021583542955865241L, pin);
      commit(true);
   }

   static final void saveVibrateOnBuzz(boolean b) {
      saveFlag(-222541206799519196L, 53, 1, b);
   }

   static final boolean isVibrateOnBuzz() {
      return checkFlag(-222541206799519196L, 53, 1);
   }

   static final void saveAskInviteQuestion(boolean b) {
      saveFlag(-222541206799519196L, 53, 2, b);
   }

   static final boolean isAskInviteQuestion() {
      return checkFlag(-222541206799519196L, 53, 2);
   }

   static final void saveAllowForwardInvite(boolean b) {
      saveFlag(-222541206799519196L, 53, 4, b);
   }

   static final boolean isAllowForwardInvite() {
      return checkFlag(-222541206799519196L, 53, 4);
   }

   static final boolean isMessagelistIntegration() {
      return checkFlag(-222541206799519196L, 53, 32);
   }

   static final void saveMessagelistIntegration(boolean b) {
      saveFlag(-222541206799519196L, 53, 32, b);
   }

   static final void saveRequests(Vector requests) {
      _hashtable.put(293351436076650281L, requests);
      commit(true);
   }

   static final Vector getRequests() {
      return (Vector)_hashtable.get(293351436076650281L);
   }

   static final boolean showOptions() {
      PeerApplication app = PeerApplication.getInstance();
      if (app != null) {
         PeerData$OptionsScreen os = new PeerData$OptionsScreen(null);
         app.pushModalScreen(os);
         return os.isDirty();
      } else {
         return false;
      }
   }

   static final boolean isAutoCap() {
      return true;
   }

   static final LongHashtable access$200() {
      return _hashtable;
   }

   static final void access$300(boolean x0) {
      commit(x0);
   }

   static {
      synchronized (_persistentObject) {
         _hashtable = (LongHashtable)_persistentObject.getContents();
         if (_hashtable == null) {
            _hashtable = (LongHashtable)(new Object());
            _persistentObject.setContents(_hashtable, 51);
            _persistentObject.commit();
         }
      }

      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _syncItem = (PeerData$PeerSyncItem)ar.get(-2083195621769849627L);
      if (_syncItem == null) {
         _syncItem = new PeerData$PeerSyncItem();
         ar.put(-2083195621769849627L, _syncItem);
         SyncManager.getInstance().enableSynchronization(_syncItem, true);
      }
   }
}
