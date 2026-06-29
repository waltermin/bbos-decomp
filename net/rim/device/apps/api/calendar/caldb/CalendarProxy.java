package net.rim.device.apps.api.calendar.caldb;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.RIMGlobalMessagePoster;

public class CalendarProxy {
   public static final long ASK_CALENDAR_TO_CLOSE = -2923539428699811595L;
   public static final String SET_CLOSEABLE = "closeable";
   public static final String DEFAULT_SERVICE = "DEFAULT";
   protected static final long ID = 3987486768607224712L;
   private static CalendarProxy _calendarProxy;

   public static CalendarProxy getInstance() {
      if (_calendarProxy == null) {
         ApplicationRegistry reg = ApplicationRegistry.getApplicationRegistry();
         synchronized (reg) {
            while (_calendarProxy == null) {
               _calendarProxy = (CalendarProxy)reg.getOrWaitFor(3987486768607224712L);
            }
         }
      }

      return _calendarProxy;
   }

   public CalDB getCalendarDatabase(CalendarService _1) {
      throw null;
   }

   public CalDB getCalendarDatabase() {
      return this.getCalendarDatabase(null);
   }

   public void addFactoryID(long _1) {
      throw null;
   }

   public boolean removeFromRepository(long _1, Object _3) {
      throw null;
   }

   public long[] getFactoryIDs() {
      throw null;
   }

   public void addToRepository(long _1, Object _3) {
      throw null;
   }

   public Object[] getRepositoryCopy(long _1) {
      throw null;
   }

   public static int getCalendarUIAppProcessId() {
      ApplicationManager appManager = ApplicationManager.getApplicationManager();
      ApplicationDescriptor[] descriptors = appManager.getVisibleApplications();

      for (int i = 0; i < descriptors.length; i++) {
         if (descriptors[i].getModuleName().equals("net_rim_bb_calendar_app")) {
            return appManager.getProcessId(descriptors[i]);
         }
      }

      return 0;
   }

   public static boolean isCalendarRunning() {
      return getCalendarUIAppProcessId() != 0;
   }

   public static void closeCalendar() {
      int processId = getCalendarUIAppProcessId();
      if (processId != 0) {
         RIMGlobalMessagePoster.postGlobalEvent(processId, -2923539428699811595L, 0, 0, null, null);
      }
   }

   public static boolean startCalendar(String[] args) {
      int moduleHandle = CodeModuleManager.getModuleHandle("net_rim_bb_calendar_app");
      ApplicationDescriptor[] descriptors = CodeModuleManager.getApplicationDescriptors(moduleHandle);
      if (descriptors.length < 1) {
         return false;
      }

      ApplicationDescriptor descriptor = descriptors[0];
      ApplicationManager appManager = ApplicationManager.getApplicationManager();
      if (descriptor != null) {
         try {
            if (args != null) {
               descriptor = (ApplicationDescriptor)(new Object(descriptor, args));
            }

            appManager.runApplication(descriptor);
            return true;
         } finally {
            ;
         }
      } else {
         return true;
      }
   }
}
