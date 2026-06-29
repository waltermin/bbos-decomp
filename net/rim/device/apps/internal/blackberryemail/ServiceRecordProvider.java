package net.rim.device.apps.internal.blackberryemail;

public interface ServiceRecordProvider {
   int INVALID_SERVICE_UID_HASH = -1;
   int INVALID_SERVICE_NAME_HASH = -1;
   int INVALID_SERVICE_USERID = -1;

   String getServiceContentIdentifier();

   int getServiceUserId();

   int getServiceUidHash();

   int getServiceNameHash();
}
