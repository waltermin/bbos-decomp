package net.rim.device.apps.internal.mms.api;

public interface MMSTask extends Runnable {
   long NOTIFY_GUID = -3436621066262173388L;
   long READ_GUID = -1385708141614342777L;
   long WRITE_GUID = -1627158063255138358L;

   long getTaskThreadGuid();

   boolean requiresRadioCoverage();
}
