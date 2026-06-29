package net.rim.device.apps.internal.secureemail.sendmethods;

public interface MessageEncoder {
   int MAXIMUM_MESSAGE_BODY_SIZE_BYTES = 32768;

   boolean encodeMessage();

   Object getSendContext();

   long getEncodingUID();

   int getEncodingAction();
}
