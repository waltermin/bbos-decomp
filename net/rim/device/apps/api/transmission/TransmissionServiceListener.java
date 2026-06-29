package net.rim.device.apps.api.transmission;

public interface TransmissionServiceListener {
   int DEFAULT_CICAL_EVENT_PRIORITY;
   int CICAL_MEETING_EMAIL_PRIORITY;
   int VOICEMAIL_EMAIL_MESSAGE_PRIORITY;
   int DEFAULT_CMIME_EMAIL_PRIORITY;
   int DEFAULT_PIN_MESSAGE_PRIORITY;

   boolean receiveObject(TransmissionService var1, Object var2, Object var3);

   void statusChanged(TransmissionService var1, int var2, Object var3);
}
