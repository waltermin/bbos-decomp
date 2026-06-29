package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.internal.profiles.Profiles;

public final class PagingSupport {
   private static boolean _pagingInitialized;
   private static final long PAGING_ENABLED_GUID;

   private PagingSupport() {
   }

   public static final boolean isPagingEnabled() {
      if (_pagingInitialized) {
         return true;
      } else {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         if (applicationRegistry.get(3974083155728078924L) != null) {
            _pagingInitialized = true;
            return true;
         } else {
            return false;
         }
      }
   }

   static final void enablePagingSupport() {
      if (!isPagingEnabled()) {
         _pagingInitialized = true;
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         Profiles profiles = Profiles.getInstance();
         applicationRegistry.put(3974083155728078924L, applicationRegistry);
         if (profiles != null) {
            profiles.enablePagingSupport();
         }

         NotificationsManager.registerSource(6432934947797527350L, new PagingSupport$1(), 0);
      }
   }
}
