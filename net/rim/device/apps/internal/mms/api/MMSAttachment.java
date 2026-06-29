package net.rim.device.apps.internal.mms.api;

public interface MMSAttachment {
   String getName();

   int getType();

   byte[] getData();

   String getCharset();

   int getDataSize();
}
