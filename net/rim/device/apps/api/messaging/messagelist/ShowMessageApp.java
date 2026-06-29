package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.MessageEntryPoint;

public final class ShowMessageApp {
   private ApplicationDescriptor _messagingAppDescriptor;
   private boolean _requestForeground;
   public static final long OPEN_NEW_ITEM;
   public static final long REQUEST_FOREGROUND_AND_INVOKE_VERB;
   private static final long SHOW_MESSAGEAPP_ID;
   private static final long SHOW_MESSAGEAPP_NO_FOREGROUND_ID;
   public static final long SUSPEND_NOTIFICATION;
   public static final long SWITCH_VIEWS;
   public static final long REMOVE_SERVICE_VIEW;
   public static final long UPDATE_SERVICE_VIEW;
   public static final int MAIN_MESSAGE_CENTRE_VIEW;
   public static final int SAVED_VIEW;
   public static final int SAVED_VIEW_RIBBON;
   public static final int SEARCH_EDIT_SCREEN;
   public static final int SEARCH_EDIT_SCREEN_RIBBON;
   public static final int SEARCH_VIEW;
   public static final int NAMED_SEARCH;
   public static final int SERVICE_VIEW_RIBBON;
   public static final int SERVICE_VIEW;
   public static final int IF_MESSAGE_APP_IS_FOREGROUND_NO_SCREEN_SWITCH_REQUIRED;

   private ShowMessageApp(ApplicationDescriptor applicationDescriptor, boolean requestForeground) {
      this._messagingAppDescriptor = applicationDescriptor;
      this._requestForeground = requestForeground;
   }

   public static final void register() {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      ApplicationDescriptor applicationDescriptor = ApplicationDescriptor.currentApplicationDescriptor();
      applicationRegistry.put(-7047158500934521385L, new ShowMessageApp(applicationDescriptor, true));
      applicationRegistry.put(7203216689892274460L, new ShowMessageApp(applicationDescriptor, false));
      applicationRegistry.put(-634672356903020959L, new ShowMessagesVerb(16777280));
   }

   public static final boolean isRegistered() {
      return ApplicationRegistry.getApplicationRegistry().get(-7047158500934521385L) != null;
   }

   private final int startMessagingApp() {
      try {
         return ApplicationManager.getApplicationManager().runApplication(this._messagingAppDescriptor, this._requestForeground);
      } finally {
         ;
      }
   }

   private static final int showMessageApp(long uid) {
      ShowMessageApp runner = (ShowMessageApp)ApplicationRegistry.getApplicationRegistry().get(uid);
      return runner.startMessagingApp();
   }

   public static final int showMessageApp() {
      ApplicationManager applicationManager = ApplicationManager.getApplicationManager();
      ShowMessageApp showMessageAppInstance = (ShowMessageApp)ApplicationRegistry.getApplicationRegistry().get(-7047158500934521385L);
      int messagingPid = applicationManager.getProcessId(showMessageAppInstance._messagingAppDescriptor);
      int foregroundPid = applicationManager.getForegroundProcessId();
      return messagingPid != foregroundPid ? showMessageApp(-246332839, null) : messagingPid;
   }

   public static final int showMessageApp(int screenType, Object objectParameter) {
      return showMessageApp(null, screenType, objectParameter);
   }

   public static final int showMessageApp(MessageEntryPoint entry, int screenType, Object objectParameter) {
      postEvent(-7912584003624203437L, screenType, 0, objectParameter, entry);
      return showMessageApp(-7047158500934521385L);
   }

   public static final int showMessageAppNoForegound() {
      return showMessageApp(7203216689892274460L);
   }

   public static final void displayMessage(RIMModel model, Object context) {
      postEvent(-4363772166259176210L, 0, 0, model, context);
   }

   public static final void invokeVerb(Verb verb, Object context) {
      postEvent(-1152649409323516530L, 0, 0, verb, context);
   }

   public static final void postEvent(long guid, int data0, int data1, Object object0, Object object1) {
      int pid = showMessageAppNoForegound();
      RIMGlobalMessagePoster.postGlobalEvent(pid, guid, data0, data1, object0, object1);
   }

   public static final boolean isMessagingAppForeground() {
      ShowMessageApp showMessageAppInstance = (ShowMessageApp)ApplicationRegistry.getApplicationRegistry().get(-7047158500934521385L);
      if (showMessageAppInstance == null) {
         return false;
      }

      ApplicationManager applicationManager = ApplicationManager.getApplicationManager();
      int messagingPid = applicationManager.getProcessId(showMessageAppInstance._messagingAppDescriptor);
      int foregroundPid = applicationManager.getForegroundProcessId();
      return messagingPid == foregroundPid;
   }
}
