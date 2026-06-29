package net.rim.device.apps.api.framework.model;

public interface EncodingProvider {
   long ENCODING_PLAIN_TEXT;
   int ENCODING_ACTION_SIGN;
   int ENCODING_ACTION_ENCRYPT;

   String getEncodingString();

   long getEncodingUID();

   int getEncodingAction();
}
