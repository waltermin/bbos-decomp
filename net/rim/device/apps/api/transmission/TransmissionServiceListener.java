package net.rim.device.apps.api.transmission;

public interface TransmissionServiceListener {
   int DEFAULT_CICAL_EVENT_PRIORITY = 100;
   int CICAL_MEETING_EMAIL_PRIORITY = 60;
   int VOICEMAIL_EMAIL_MESSAGE_PRIORITY = 95;
   int DEFAULT_CMIME_EMAIL_PRIORITY = 100;
   int DEFAULT_PIN_MESSAGE_PRIORITY = 70;

   boolean receiveObject(TransmissionService var1, Object var2, Object var3);

   void statusChanged(TransmissionService var1, int var2, Object var3);
}
