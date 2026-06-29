package net.rim.device.apps.internal.qm.peer;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.CollectionListenerManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.internal.qm.resource.QmResources;

final class PeerConversationsFolder extends CollectionListenerManager implements Folder, ReadableList {
   Conversations _conversations;
   boolean _registered;
   private static final long PEER_CONVERSATIONS_FOLDER;
   private static PeerConversationsFolder _instance;

   final void register() {
      FolderMerge.registerMergedFolder(-5581791943352753293L, this);
      FolderMerge.registerMergedFolder(2993144521330132876L, this);
      FolderMerge.registerMergedFolder(7509894771240321003L, this);
      this._registered = true;
   }

   final void deregister() {
      FolderMerge.deregisterMergedFolder(-5581791943352753293L, this);
      FolderMerge.deregisterMergedFolder(2993144521330132876L, this);
      FolderMerge.deregisterMergedFolder(7509894771240321003L, this);
      this._registered = false;
   }

   final boolean isRegistered() {
      return this._registered;
   }

   final void setConversations(Conversations conversations) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final void removeItem(PeerConversation conversation) {
      this.fireElementRemoved(this._conversations, conversation);
   }

   final void addItem(PeerConversation conversation) {
      this.fireElementAdded(this._conversations, conversation);
   }

   @Override
   public final Folder getFolder(long folderUid) {
      return folderUid == 5954110685349625386L ? this : null;
   }

   @Override
   public final Folder getParentFolder() {
      return null;
   }

   @Override
   public final Enumeration getSubFolders() {
      return null;
   }

   @Override
   public final boolean containsSubFolders() {
      return false;
   }

   @Override
   public final Collection getContainedItems() {
      return this;
   }

   @Override
   public final Folder getBaseFolder() {
      return null;
   }

   @Override
   public final boolean canContainItems() {
      return true;
   }

   @Override
   public final boolean isVisible(Object context) {
      return true;
   }

   @Override
   public final String getFriendlyName() {
      return QmResources.getString(87);
   }

   @Override
   public final long getLUID() {
      return 5954110685349625386L;
   }

   @Override
   public final int size() {
      return this._conversations != null ? this._conversations.size() : 0;
   }

   @Override
   public final Object getAt(int index) {
      return this._conversations != null ? this._conversations.getAt(index) : null;
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      return this._conversations != null ? this._conversations.getAt(index, count, elements, destIndex) : 0;
   }

   @Override
   public final int getIndex(Object element) {
      return this._conversations != null ? this._conversations.getIndex(element) : -1;
   }

   private PeerConversationsFolder() {
   }

   public static final PeerConversationsFolder getInstance() {
      boolean register = false;
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      _instance = (PeerConversationsFolder)registry.get(5954110685349625386L);
      if (_instance == null) {
         _instance = new PeerConversationsFolder();
         registry.put(5954110685349625386L, _instance);
         register = true;
      }

      if (register && PeerData.isMessagelistIntegration()) {
         _instance.register();
      }

      return _instance;
   }
}
