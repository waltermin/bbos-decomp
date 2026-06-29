package net.rim.device.apps.api.framework.model;

public interface EncodingProvider {
   long ENCODING_PLAIN_TEXT = 182808770805039415L;
   int ENCODING_ACTION_SIGN = 1;
   int ENCODING_ACTION_ENCRYPT = 2;

   String getEncodingString();

   long getEncodingUID();

   int getEncodingAction();
}
