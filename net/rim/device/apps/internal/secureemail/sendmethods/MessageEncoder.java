package net.rim.device.apps.internal.secureemail.sendmethods;

public interface MessageEncoder {
   int MAXIMUM_MESSAGE_BODY_SIZE_BYTES;

   boolean encodeMessage();

   Object getSendContext();

   long getEncodingUID();

   int getEncodingAction();
}
