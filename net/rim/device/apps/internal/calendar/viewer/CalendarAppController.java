package net.rim.device.apps.internal.calendar.viewer;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.apps.api.calendar.calconstants.CalOptionCache;
import net.rim.device.apps.api.calendar.caldb.CalendarOptions;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.vm.PersistentInteger;

final class CalendarAppController implements CalendarActions, GlobalEventListener {
   private CalendarApp _calendarApp;
   private CalendarView _day;
   private CalendarView _week;
   private CalendarView _month;
   private CalendarView _agenda;
   private CalendarView _curr;
   private int _currentViewId = -1;
   private TimeZone _currentTimeZone = TimeZone.getDefault();
   private CalendarAppController$EventViewerVerbQueueThread _verbQueueThread = new CalendarAppController$EventViewerVerbQueueThread(this);
   static final long VIEW_CALENDAR = 6359528386020392909L;
   private static final long LAST_VIEW_ID_KEY = -539080667505170584L;
   private static final String INVOKE_API_NEW_REQUEST = "newappt";
   private static int _lastViewID;

   final void initialize(CalendarApp calendarApp, boolean displayInitialView) {
      _lastViewID = PersistentInteger.getId(-539080667505170584L, 0);
      this._calendarApp = calendarApp;
      this._calendarApp.addGlobalEventListener(this);
      CalOptionCache.setTimeWithFocus(System.currentTimeMillis());
      CalOptionCache.setObjectWithFocus(null);
      if (displayInitialView) {
         RIMGlobalMessagePoster.postGlobalEvent(this._calendarApp.getProcessId(), 6359528386020392909L, 0, 0, null, null);
      }

      this._verbQueueThread.start();
   }

   private final int getInitialView() {
      int initialView = CalendarOptions.getOptions().getInitialView();
      if (initialView == 4) {
         initialView = PersistentInteger.get(_lastViewID);
      }

      if (initialView == 1) {
         return 3;
      } else if (initialView == 2) {
         return 1;
      } else {
         return initialView == 3 ? 4 : 2;
      }
   }

   public final CalendarApp getCalendarUIApplication() {
      return this._calendarApp;
   }

   @Override
   public final void switchViews(int viewId, boolean returnToPreviousView) {
      if (viewId >= 0) {
         if (viewId == this._currentViewId) {
            this._curr.refresh();
         } else {
            int previousViewId = this._currentViewId;
            CalendarView requestedView = null;
            int lastView = 0;
            switch (viewId) {
               case -1:
                  break;
               case 0:
                  if (this._currentViewId == 2) {
                     this.switchViews(3, returnToPreviousView);
                  } else if (this._currentViewId == 3) {
                     this.switchViews(1, returnToPreviousView);
                  } else if (this._currentViewId == 1) {
                     this.switchViews(4, returnToPreviousView);
                  } else if (this._currentViewId == 4) {
                     this.switchViews(2, returnToPreviousView);
                  }
                  break;
               case 1:
                  if (this._month == null) {
                     this._month = new MonthController(this.getCalendarUIApplication(), this);
                     this._month.initialize();
                  }

                  requestedView = this._month;
                  lastView = 2;
                  break;
               case 2:
               default:
                  if (this._day == null) {
                     this._day = new DayController(this.getCalendarUIApplication(), this);
                     this._day.initialize();
                  }

                  requestedView = this._day;
                  lastView = 0;
                  break;
               case 3:
                  if (this._week == null) {
                     this._week = new WeekController(this.getCalendarUIApplication(), this);
                     this._week.initialize();
                  }

                  requestedView = this._week;
                  lastView = 1;
                  break;
               case 4:
                  if (this._agenda == null) {
                     this._agenda = new AgendaController(this.getCalendarUIApplication(), this);
                     this._agenda.initialize();
                  }

                  requestedView = this._agenda;
                  lastView = 3;
            }

            if (requestedView != null) {
               if (this._curr != null) {
                  this._curr.remove();
               }

               this._curr = requestedView;
               this._currentViewId = viewId;
               this._curr.display(previousViewId, returnToPreviousView);
               PersistentInteger.set(_lastViewID, lastView);
            }
         }
      }
   }

   @Override
   public final void close() {
      if (this._curr != null) {
         this._curr.remove();
      }

      this.uninitializeViews();
      CalOptionCache.setObjectWithFocus(null);
      CalOptionCache.setSuggestedUserText(null);
      this._calendarApp.removeGlobalEventListener(this);
      System.exit(0);
   }

   private final void uninitializeViews() {
      if (this._day != null) {
         this._day.uninitialize();
         this._day = null;
      }

      if (this._week != null) {
         this._week.uninitialize();
         this._week = null;
      }

      if (this._month != null) {
         this._month.uninitialize();
         this._month = null;
      }

      if (this._agenda != null) {
         this._agenda.uninitialize();
         this._agenda = null;
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid != 3596208183088439728L) {
         if (guid == 6359528386020392909L) {
            int requestedView = this.getInitialView();
            long requestedDate = 0;
            boolean requestedNew = false;
            Event requestedEvent = null;
            if (object0 instanceof Object) {
               ContextObject context = (ContextObject)object0;
               Object data = context.get(4143325197084129318L);
               if (data instanceof Object) {
                  CalendarOptions.getOptions().resetCalendarServiceFilter();
                  requestedDate = ((CalendarExtensions)data).getTimeLong();
               } else if (data instanceof Object) {
                  requestedEvent = (Event)data;
               }

               requestedView = context.getIntegerData(requestedView);
               String invokeData = (String)context.get(-8485899342890396495L);
               if (invokeData != null && invokeData.equals("newappt")) {
                  requestedNew = true;
               }
            }

            this.switchViews(requestedView, false);
            if (requestedDate != 0) {
               this._curr.setTimeWithFocus(requestedDate);
            }

            if (requestedNew) {
               Verb newEvtVerb = (Verb)(new Object());
               if (requestedEvent != null) {
                  this._verbQueueThread.addVerbToQueue(newEvtVerb, requestedEvent);
                  return;
               }

               if (requestedDate != 0) {
                  CalOptionCache.setTimeWithFocus(requestedDate);
               }

               CalOptionCache.setSuggestedUserDuration(3600000);
               this._verbQueueThread.addVerbToQueue(newEvtVerb, null);
               return;
            }

            if (requestedEvent != null) {
               CalOptionCache.setTimeWithFocus(requestedEvent.getStart(this._currentTimeZone));
               Verb editEventVerb = (Verb)(new Object(requestedEvent));
               this._verbQueueThread.addVerbToQueue(editEventVerb, null);
            }
         }
      } else {
         TimeZone newTimeZone = TimeZone.getDefault();
         if (!this._currentTimeZone.getID().equals(newTimeZone.getID())) {
            int currentViewId = this._currentViewId;
            this._currentViewId = -1;
            Calendar cal = null;
            if (this._curr != null) {
               cal = Calendar.getInstance(this._currentTimeZone);
               ((CalendarExtensions)cal).setTimeLong(this._curr.getTimeWithFocus());
               this._curr.remove();
               this._curr = null;
            }

            this.uninitializeViews();
            this.switchViews(currentViewId, false);
            if (cal != null) {
               int year = cal.get(1);
               int month = cal.get(2);
               int day = cal.get(5);
               int hour = cal.get(11);
               int min = cal.get(12);
               cal.setTimeZone(newTimeZone);
               cal.set(1, year);
               cal.set(2, month);
               cal.set(5, day);
               cal.set(11, hour);
               cal.set(12, min);
               cal.set(13, 0);
               cal.set(14, 0);
               if (this._curr != null) {
                  this._curr.setTimeWithFocus(((CalendarExtensions)cal).getTimeLong());
               }
            }

            this._currentTimeZone = newTimeZone;
         }
      }
   }
}
