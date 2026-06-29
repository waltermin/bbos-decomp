package net.rim.device.apps.internal.mms.api;

public interface MMSTask extends Runnable {
   long NOTIFY_GUID;
   long READ_GUID;
   long WRITE_GUID;

   long getTaskThreadGuid();

   boolean requiresRadioCoverage();
}
