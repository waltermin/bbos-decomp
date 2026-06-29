package net.rim.device.apps.internal.calendar.viewer;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.Tooltip;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.caldb.CalendarProxy;
import net.rim.device.apps.api.framework.hotkeys.HotKeys;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.pim.TimeBasedCollection;
import net.rim.device.apps.api.search.GlobalSearchRegistry;
import net.rim.device.internal.system.InternalServices;

final class CalendarApp extends UiApplication implements GlobalEventListener {
   boolean _externallyCloseable;
   OrganizingCalendarDialog _organizingCalendarDialog;
   private static final long CALENDAR_SEARCHABLE_ID;
   static ResourceBundle _rb = ResourceBundle.getBundle(912302513268743237L, "net.rim.device.apps.internal.resource.Calendar");
   private static long ALLOW_CLOSEABLE = 3873302428969980664L;
   private static String CALENDAR_MODULE_NAME = "net_rim_bb_calendar_app";

   public static final void main(String[] args) {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      if (applicationRegistry.get(8025740836317336000L) == null) {
         CalendarOptions options = CalendarOptions.getOptions();
         boolean integratedTasks = options.isTasksIntegratedIntoViews();
         TimeBasedCollection.getInstance().activateProvider(-5718599435502913979L, integratedTasks);
         options.enableSynchronization();
         applicationRegistry.put(8025740836317336000L, new ViewCalendarVerb());
         GlobalSearchRegistry.register(-2560306609227180038L, new CalendarSearchable());
         System.exit(0);
      }

      boolean externallyCloseable = false;
      if (args != null && args.length == 1 && args[0].equals("closeable")) {
         externallyCloseable = true;
      }

      ApplicationManager appManager = ApplicationManager.getApplicationManager();
      ApplicationDescriptor[] descriptors = appManager.getVisibleApplications();

      for (int i = 0; i < descriptors.length; i++) {
         if (descriptors[i].getModuleName().equals(CALENDAR_MODULE_NAME)) {
            int processId = appManager.getProcessId(descriptors[i]);
            RIMGlobalMessagePoster.postGlobalEvent(processId, ALLOW_CLOSEABLE, 0, 0, null, null);
            appManager.requestForeground(processId);
            return;
         }
      }

      boolean displayInitialView = true;
      boolean displayNewView = false;
      if (args != null && args.length == 1 && args[0].equals("noview")) {
         displayInitialView = false;
      } else if (args != null && args.length == 2 && args[0].equals("newview")) {
         displayInitialView = false;
         displayNewView = true;
      }

      CalendarApp calendarApp = new CalendarApp();
      CalendarAppController controller = new CalendarAppController();
      controller.initialize(calendarApp, displayInitialView);
      if (displayNewView) {
         controller.switchViews(Integer.parseInt(args[1]), false);
      }

      CalendarProxy calProxy = CalendarProxy.getInstance();
      Object[] repositoryCopy = calProxy.getRepositoryCopy(-2786162410658704605L);
      if (repositoryCopy != null) {
         Verb verb = (Verb)repositoryCopy[0];
         if (verb != null) {
            HotKeys.registerHotKey(4, !InternalServices.isReducedFormFactor() ? _rb.getString(122).charAt(0) : 'O', verb, true);
         }
      }

      calendarApp.addGlobalEventListener(calendarApp);
      calendarApp.setExternallyCloseable(externallyCloseable);
      Tooltip.init();
      CalendarOptions.getOptions().resetCalendarServiceFilter();
      calendarApp.enterEventDispatcher();
   }

   final void setExternallyCloseable(boolean externallyCloseable) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -5520237139346668942L) {
         if (this._organizingCalendarDialog == null) {
            this._organizingCalendarDialog = new OrganizingCalendarDialog(this);
         }

         if (data0 == 1) {
            this._organizingCalendarDialog.push();
         } else {
            this._organizingCalendarDialog.pop(data1);
         }
      } else if (guid == ALLOW_CLOSEABLE) {
         this.setExternallyCloseable(true);
      } else {
         if (guid == -2923539428699811595L && this._externallyCloseable && this.getActiveScreen() instanceof PreListenerScreen) {
            System.exit(0);
         }
      }
   }
}
