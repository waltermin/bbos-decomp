package net.rim.device.apps.internal.blackberryemail;

public interface ServiceRecordProvider {
   int INVALID_SERVICE_UID_HASH;
   int INVALID_SERVICE_NAME_HASH;
   int INVALID_SERVICE_USERID;

   String getServiceContentIdentifier();

   int getServiceUserId();

   int getServiceUidHash();

   int getServiceNameHash();
}
