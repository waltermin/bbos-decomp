package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.controller.CalendarEventViewer;
import net.rim.device.apps.api.calendar.controller.CalendarEventViewerProvider;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.ota.OTACalendarSyncDataManager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.DeleteSingleItemVerb;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.vm.Array;

class OpenMeetingEmailAttachmentVerb extends Verb implements GlobalEventListener {
   private CICALMeetingAttachmentModel _attachment;
   private RIMModel _message;
   private VerbFactory _verbFactory;
   private CalendarEventViewer _viewer;

   OpenMeetingEmailAttachmentVerb(RIMModel message, CICALMeetingAttachmentModel attachment, Object context) {
      super(590080);
      this._attachment = attachment;
      this._message = message;
      this._verbFactory = (VerbFactory)ContextObject.get(context, -2846768035584909703L);
   }

   @Override
   public String toString() {
      return CommonResource.getString(15);
   }

   @Override
   public Object invoke(Object parameter) {
      int eventCount = this._attachment.getEventCount();
      Event event = (Event)this._attachment.getResolvedEvents(this._message)[eventCount > 0 ? eventCount - 1 : 0];
      ContextObject context = ContextObject.castOrCreate(parameter);
      if (event instanceof Object) {
         CalendarEventViewerProvider viewerProvider = (CalendarEventViewerProvider)event;
         CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarService(event);
         int deleteVerbIndex = -1;
         if (this._attachment.getType() != 1) {
            context.setFlag(27);
         } else {
            context.clearFlag(27);
         }

         this._viewer = viewerProvider.getCalendarEventViewer(context);
         if (this._viewer != null) {
            if (!ContextObject.getFlag(context, 64)) {
               CICALMeetingEmailer.markAsOpened(this._message);
            }

            int verbCount = 2;
            Verb[] verbs = new Object[verbCount];
            int defaultVerbIndex = -1;
            ResourceBundle resources = ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA");
            int resourceId = 0;
            switch (this._attachment.getType()) {
               case 0:
                  break;
               case 1:
               default:
                  if (calendarService.getCICALConfiguration().isMeetingSyncEnabled() && OTACalendarSyncDataManager.getInstance().get(event) != null) {
                     Array.resize(verbs, verbCount + 6);
                     defaultVerbIndex = verbCount;
                     verbs[verbCount++] = new OpenMeetingEmailAttachmentVerb$EventViewerWrapperVerb(
                        this, new MeetingResponseVerb(0, this._attachment, this._message)
                     );
                     verbs[verbCount++] = new OpenMeetingEmailAttachmentVerb$EventViewerWrapperVerb(
                        this, new MeetingResponseVerb(1, this._attachment, this._message)
                     );
                     verbs[verbCount++] = new OpenMeetingEmailAttachmentVerb$EventViewerWrapperVerb(
                        this, new MeetingResponseVerb(4, this._attachment, this._message)
                     );
                     verbs[verbCount++] = new OpenMeetingEmailAttachmentVerb$EventViewerWrapperVerb(
                        this, new MeetingResponseVerb(5, this._attachment, this._message)
                     );
                     verbs[verbCount++] = new OpenMeetingEmailAttachmentVerb$EventViewerWrapperVerb(
                        this, new MeetingResponseVerb(2, this._attachment, this._message)
                     );
                     verbs[verbCount++] = new OpenMeetingEmailAttachmentVerb$EventViewerWrapperVerb(
                        this, new MeetingResponseVerb(3, this._attachment, this._message)
                     );
                  }

                  Verb viewCalendarVerb = (Verb)ApplicationRegistry.getApplicationRegistry().get(8025740836317336000L);
                  if (viewCalendarVerb != null) {
                     Array.resize(verbs, verbCount + 1);
                     verbs[verbCount++] = new ViewCalendarVerbWrapper(viewCalendarVerb, event);
                  }

                  resourceId = 402;
                  break;
               case 2:
                  resourceId = 403;
                  break;
               case 3:
                  resourceId = 404;
            }

            String title = resources.getString(resourceId);
            if (this._verbFactory != null) {
               if (this._message instanceof Object) {
                  VerbProvider vp = (VerbProvider)this._message;
                  Verb[] emailVerbs = new Object[0];
                  ContextObject.setFlag(context, 12);
                  ContextObject.setFlag(context, 13);
                  ContextObject.setFlag(context, 87);
                  vp.getVerbs(context, emailVerbs);
                  ContextObject.clearFlag(context, 12);
                  ContextObject.clearFlag(context, 13);
                  ContextObject.clearFlag(context, 87);
                  if (emailVerbs != null && emailVerbs.length > 0) {
                     Array.resize(verbs, verbCount + emailVerbs.length);

                     for (int i = emailVerbs.length - 1; i >= 0; i--) {
                        verbs[verbCount++] = new OpenMeetingEmailAttachmentVerb$EventViewerWrapperVerb(this, emailVerbs[i]);
                     }
                  }
               }

               Verb[] additionalVerbs = this._verbFactory.getVerbs(context);
               if (additionalVerbs != null && additionalVerbs.length > 0) {
                  Array.resize(verbs, verbCount + additionalVerbs.length);

                  for (int i = additionalVerbs.length - 1; i >= 0; i--) {
                     if (deleteVerbIndex < 0 && additionalVerbs[i] instanceof Object) {
                        deleteVerbIndex = verbCount;
                        if (defaultVerbIndex < 0) {
                           DeleteSingleItemVerb deleteVerb = (DeleteSingleItemVerb)additionalVerbs[i];
                           RIMModel message = (RIMModel)ContextObject.get(context, 424670468422402792L);
                           if (message != null) {
                              deleteVerb.setTarget(message);
                           }

                           defaultVerbIndex = verbCount;
                        }
                     }

                     verbs[verbCount++] = new OpenMeetingEmailAttachmentVerb$EventViewerWrapperVerb(this, additionalVerbs[i]);
                  }
               }
            }

            if (verbs.length == 2) {
               verbs = null;
            }

            Application application = Application.getApplication();
            application.addGlobalEventListener(this);
            this._viewer.openViewer(title, verbs, -1, defaultVerbIndex, deleteVerbIndex, true);
            application.removeGlobalEventListener(this);
         }
      }

      return null;
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (object0 == this._message) {
         if (guid == -6275418955626563374L) {
            boolean deviceLocked = ApplicationManager.getApplicationManager().isSystemLocked();
            if (!deviceLocked) {
               CICALMeetingEmailer.markAsOpened(this._message);
               UiApplication.getUiApplication().requestForeground();
               return;
            }
         } else if (guid == 1202366544244619460L) {
            this._viewer.closeViewer(false);
            return;
         }
      } else if (guid == 6345609069135580235L) {
         CICALMeetingEmailer.markAsOpened(this._message);
         UiApplication.getUiApplication().requestForeground();
      }
   }
}
