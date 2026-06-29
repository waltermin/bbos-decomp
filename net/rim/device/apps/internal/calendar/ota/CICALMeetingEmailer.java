package net.rim.device.apps.internal.calendar.ota;

import java.util.Enumeration;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.calendar.caldb.CalendarService;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Attendee;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.EventUtilities;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.MeetingInfo;
import net.rim.device.apps.api.calendar.ota.CICALConstants;
import net.rim.device.apps.api.calendar.ota.CICALEventLogger;
import net.rim.device.apps.api.calendar.ota.OTACalendarSyncDataManager;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.DescriptionProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.sync.OTASyncData;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailBuilderApi;
import net.rim.device.internal.proxy.Proxy;

class CICALMeetingEmailer implements CICALConstants {
   private static OTACalendarSyncDataManager _otaSyncDataManager = OTACalendarSyncDataManager.getInstance();
   private static CICALMeetingCancelConverter _cicalMeetingCancelConverter = new CICALMeetingCancelConverter();
   private static CICALMeetingRequestConverter _cicalMeetingRequestConverter = new CICALMeetingRequestConverter();

   private CICALMeetingEmailer() {
   }

   private static void addMeetingAttachmentToMessage(
      CalendarService calendarService, EmailMessageModel message, Event event, int type, byte[] outBoundAttachment
   ) {
      CICALEventLogger.logEvent(type, 4, outBoundAttachment);
      message.add(new MeetingEmailDataModel(event, type, outBoundAttachment));
      OTACalendarTransmissionService transmissionService = (OTACalendarTransmissionService)TransmissionServiceManager.get(
         calendarService.getTransmissionServiceID()
      );
      if (transmissionService != null) {
         transmissionService.logOutgoingMeetingPacket(message, Integer.toString(type), outBoundAttachment);
      }
   }

   private static boolean sendMessage(EmailMessageModel message, CalendarService calendarService) {
      ServiceRecord cicalServiceRecord = calendarService.getServiceRecord();
      if (cicalServiceRecord != null) {
         ServiceRecord emailServiceRecord = ServiceBook.getSB().getCIDAssociatedWithService("CMIME", cicalServiceRecord);
         if (emailServiceRecord != null) {
            Proxy proxy = Proxy.getInstance();
            proxy.invokeLater(new CICALMeetingEmailer$SendEmailThreaded(message, emailServiceRecord));
            return true;
         }
      }

      CICALEventLogger.logEvent(1397574982, 2);
      return false;
   }

   static void sendMeetingResponse(CICALMeetingAttachmentModel attachment, Event event, RIMModel originalEmail, int answer, String comment) {
      if (event == null) {
         event = attachment.getEvent(0);
      }

      CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarService(event);
      EmailMessageModel message = (EmailMessageModel)FactoryUtil.createInstance(-6822293833372928884L, null);
      message.setType((byte)1);
      RIMModel organizerAddress = (RIMModel)attachment.getSender().getAddress();
      EmailBuilderApi.addRecipient(message, 0, organizerAddress);
      if (originalEmail instanceof EmailMessageModel) {
         EmailBuilderApi.addOriginalMessageReferenceIdentifierStub(message, ((EmailMessageModel)originalEmail).getCMIMEReferenceIdentifier());
      }

      ResourceBundle resources = ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA");
      StringBuffer bodyText = new StringBuffer();
      StringBuffer subjectText = new StringBuffer();
      switch (answer) {
         case 1:
            break;
         case 2:
         default:
            bodyText.append(resources.getString(100));
            subjectText.append(resources.getString(101));
            break;
         case 3:
            bodyText.append(resources.getString(102));
            subjectText.append(resources.getString(103));
            break;
         case 4:
            bodyText.append(resources.getString(104));
            subjectText.append(resources.getString(105));
      }

      subjectText.append(attachment.getSubject());
      EmailBuilderApi.addSubjectLine(message, subjectText.toString());
      if (comment != null) {
         bodyText.append("\n\n");
         bodyText.append(comment);
      }

      EmailBuilderApi.addMessageBody(message, bodyText.toString());
      ContextObject contextObject = new ContextObject(12);
      contextObject.put(7849556394715590464L, new Integer(answer));
      if (comment != null) {
         contextObject.put(8925131257384216348L, comment);
      }

      removeFromMessageList(originalEmail);
      OTASyncData syncData = _otaSyncDataManager.get(event);
      if (syncData != null) {
         EventUtilities.incrementDeviceSequence(syncData, event);
         byte[] outBoundAttachment = new CICALMeetingResponseConverter().convert(event, contextObject);
         addMeetingAttachmentToMessage(calendarService, message, event, 2, outBoundAttachment);
         CICALEventLogger.logEvent(1397576275, 4, outBoundAttachment);
         sendMessage(message, calendarService);
      }
   }

   static void removeFromMessageList(RIMModel message) {
      if (message instanceof ActionProvider) {
         ActionProvider actionProvider = (ActionProvider)message;
         ContextObject context = new ContextObject();
         ContextObject.put(context, 2164559162753216116L, Boolean.TRUE);
         actionProvider.perform(6780594967363292755L, context);
      }
   }

   static void markAsOpened(RIMModel message) {
      if (message instanceof ActionProvider) {
         ActionProvider actionProvider = (ActionProvider)message;
         actionProvider.perform(5803508244060051872L, null);
      }
   }

   static void sendMeetingRequest(Event event) {
      MeetingInfo meetingInfo = event.getMeetingInfo();
      String senderAddress = null;
      CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarService(event);
      if (calendarService != null) {
         senderAddress = EventUtilities.getEmailAddress(calendarService.getServiceRecord());
      }

      if (senderAddress != null) {
         EmailMessageModel message = (EmailMessageModel)FactoryUtil.createInstance(-6822293833372928884L, null);
         message.setTimestamp(System.currentTimeMillis());
         Enumeration attendees = meetingInfo.getAttendees();

         while (attendees.hasMoreElements()) {
            Attendee attendee = (Attendee)attendees.nextElement();
            Object o = attendee.getAddress();
            EmailAddressModel emailAddress = (EmailAddressModel)o;
            String address = emailAddress.getAddress();
            if (!StringUtilities.strEqualIgnoreCase(senderAddress, address)) {
               EmailBuilderApi.addRecipient(message, 0, (RIMModel)attendee.getAddress());
            }
         }

         StringBuffer subjectBuffer = new StringBuffer("");
         String subject = event.getSubject();
         if (subject == null || subject.length() == 0) {
            subject = ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA").getString(200);
         }

         if (isEventUpdated(event)) {
            String updatePrefix = ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA").getString(107);
            if (!subject.startsWith(updatePrefix.substring(0, updatePrefix.indexOf(":")))) {
               subjectBuffer.append(updatePrefix);
            }
         }

         subjectBuffer.append(subject);
         EmailBuilderApi.addSubjectLine(message, subjectBuffer.toString());
         ContextObject contextObject = new ContextObject();
         String bodyText = null;
         if (event instanceof DescriptionProvider) {
            DescriptionProvider descriptionProvider = event;
            bodyText = descriptionProvider.getStringForField(1589658722817992360L);
         }

         if (bodyText == null) {
            bodyText = ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA").getString(201);
         }

         EmailBuilderApi.addMessageBody(message, bodyText);
         byte[] attachment = _cicalMeetingRequestConverter.convert(event, contextObject);
         addMeetingAttachmentToMessage(calendarService, message, event, 1, attachment);
         CICALEventLogger.logEvent(1397576273, 4, attachment);
         sendMessage(message, calendarService);
      }
   }

   static void sendMeetingCancellation(Event event, ContextObject context) {
      MeetingInfo meetingInfo = event.getMeetingInfo();
      String senderAddress = null;
      CalendarService calendarService = CalendarServiceManager.getInstance().findCalendarService(event);
      if (calendarService != null) {
         senderAddress = EventUtilities.getEmailAddress(calendarService.getServiceRecord());
      }

      if (senderAddress != null) {
         EmailMessageModel message = (EmailMessageModel)FactoryUtil.createInstance(-6822293833372928884L, null);
         message.setPriority((byte)1);
         Enumeration attendees = meetingInfo.getAttendees();

         while (attendees.hasMoreElements()) {
            Attendee attendee = (Attendee)attendees.nextElement();
            Object o = attendee.getAddress();
            EmailAddressModel emailAddress = (EmailAddressModel)o;
            String address = emailAddress.getAddress();
            if (!StringUtilities.strEqualIgnoreCase(address, senderAddress)) {
               EmailBuilderApi.addRecipient(message, 0, (RIMModel)attendee.getAddress());
            }
         }

         StringBuffer subjectBuffer = new StringBuffer(
            ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA").getString(106)
         );
         String subject = event.getSubject();
         if (subject != null) {
            subjectBuffer.append(subject);
         }

         EmailBuilderApi.addSubjectLine(message, subjectBuffer.toString());
         String bodyText = null;
         if (event instanceof DescriptionProvider) {
            DescriptionProvider descriptionProvider = event;
            bodyText = descriptionProvider.getStringForField(1589658722817992360L);
         }

         if (bodyText == null) {
            bodyText = ResourceBundle.getBundle(8008824311162635875L, "net.rim.device.apps.internal.resource.CalendarOTA").getString(201);
         }

         EmailBuilderApi.addMessageBody(message, bodyText);
         byte[] attachment = _cicalMeetingCancelConverter.convert(event, context);
         addMeetingAttachmentToMessage(calendarService, message, event, 3, attachment);
         CICALEventLogger.logEvent(1397572417, 4, attachment);
         sendMessage(message, calendarService);
      }
   }

   static boolean isEventUpdated(Event event) {
      OTASyncData syncData = OTACalendarSyncDataManager.getInstance().get(event);
      return syncData.getHostSequence() > 0 || syncData.getDeviceSequence() > 2 || event.getRelatedLUID() != 0;
   }
}
