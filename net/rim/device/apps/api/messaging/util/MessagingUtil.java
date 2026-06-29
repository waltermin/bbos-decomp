package net.rim.device.apps.api.messaging.util;

import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.messagelist.MessageListOptions;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageApp;
import net.rim.device.apps.api.messaging.resources.MessageResources;

public final class MessagingUtil {
   public static final long MESSAGING_PRIVATE_CONTEXT_FLAGS;
   public static final int CLEAR_TERMINAL_RESULT_FLAG_ON_COMPOSE;

   public static final void robustElementUpdated(CollectionListener listener, Object model) {
      synchronized (FolderHierarchies.getLockObject()) {
         try {
            listener.elementUpdated(null, model, model);
         } finally {
            return;
         }
      }
   }

   public static final boolean confirmOnSend(Object context) {
      String confirmationMessage = ITPolicy.getString(21, 4);
      if (confirmationMessage == null) {
         return true;
      }

      if (confirmationMessage.length() == 0) {
         confirmationMessage = MessageResources.getString(184);
      }

      return Dialog.ask(3, confirmationMessage, -1) == 4;
   }

   public static final int showMessageAppServiceView(String folder) {
      long serviceId = getMessageServiceMergedCollectionId(folder);
      return serviceId != 0 ? ShowMessageApp.showMessageApp(1064620590, Long.toString(serviceId)) : ShowMessageApp.showMessageApp();
   }

   public static final long getMessageServiceMergedCollectionId(String folder) {
      short inboxOption = MessageListOptions.getOptions().getSMSEmailInbox();
      if (inboxOption == 0) {
         String option = ThemeManager.getActiveTheme().getOption(folder);
         if (option != null) {
            if (option.equals("SMS")) {
               return -7118119043524803584L;
            }

            if (option.equals("MMS")) {
               return -942103673428357213L;
            }

            if (option.equals("SMS_AND_MMS")) {
               return -4696470826620059293L;
            }
         }
      } else if (inboxOption == 2) {
         return -4696470826620059293L;
      }

      return 0;
   }

   public static final int getUnreadCountId(String folder) {
      short inboxOption = MessageListOptions.getOptions().getSMSEmailInbox();
      if (inboxOption == 0) {
         String option = ThemeManager.getActiveTheme().getOption(folder);
         if (option != null) {
            if (option.equals("SMS")) {
               return 4;
            }

            if (option.equals("MMS")) {
               return 5;
            }

            if (option.equals("SMS_AND_MMS")) {
               return 11;
            }
         }
      } else if (inboxOption == 2) {
         return 11;
      }

      return 1;
   }
}
