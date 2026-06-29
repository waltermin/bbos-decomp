package net.rim.device.apps.internal.messaging;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.util.AnonymousMessages;
import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;

final class AnonymousMessagesImpl extends AnonymousMessages {
   private AnonymousMessagesImpl$AnonymousMessageHierarchy _hierarchy;
   private static final long HIERARCHY_KEY;
   private static final long MAIN_FOLDER_KEY;
   private static final long SAVED_THEN_ORPHANED_FOLDER_KEY;
   private static final long ANONYMOUS_FAMILY;
   private static AnonymousMessagesImpl _instance;

   static final void register() {
      _instance = new AnonymousMessagesImpl();
      _instance._hierarchy = new AnonymousMessagesImpl$AnonymousMessageHierarchy();
      FolderHierarchies.registerFolderHierarchy(_instance._hierarchy);
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(-7420407412231626381L, _instance);
   }

   private static final AnonymousMessagesImpl getInstance() {
      if (_instance == null) {
         _instance = (AnonymousMessagesImpl)ApplicationRegistry.getApplicationRegistry().waitFor(-7420407412231626381L);
      }

      return _instance;
   }

   private static final Collection getMainCollection() {
      Folder folder = getInstance()._hierarchy.getFolder(-77939598941636974L);
      return folder.getContainedItems();
   }

   private static final Collection getOrphanCollection() {
      Folder folder = getInstance()._hierarchy.getFolder(-4135505141387831963L);
      return folder.getContainedItems();
   }

   @Override
   protected final void createAnonymousMessageImpl(String from, String subject, String body) {
      ((WritableSet)getMainCollection()).add(new AnonymousMessagesImpl$AnonymousMessageModel(from, subject, body, null));
   }

   @Override
   protected final boolean isFirstBootImpl() {
      return getInstance()._hierarchy.isFirstBoot();
   }

   private static final void adjustUnreadCount(boolean increment) {
      adjustNewAndUnreadCount(increment, false, true);
   }

   private static final void adjustNewAndUnreadCount(boolean increment, boolean updateNewCount, boolean updateReadCount) {
      if (increment) {
         UnreadCountManager.incrementUnreadCount(1, updateNewCount, updateReadCount);
         UnreadCountManager.incrementUnreadCount(2, updateNewCount, updateReadCount);
      } else {
         UnreadCountManager.decrementUnreadCount(1, updateNewCount, updateReadCount);
         UnreadCountManager.decrementUnreadCount(2, updateNewCount, updateReadCount);
      }
   }
}
