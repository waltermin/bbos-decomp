package net.rim.device.apps.internal.phone.api;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;

public interface PhoneCallModel extends PersistableRIMModel, ReadableList, WritableSet {
   byte TYPE_INCOMING;
   byte TYPE_OUTGOING;
   byte TYPE_UNOPENED;
   byte TYPE_OPENED;
   byte TYPE_CONFERENCE;
   byte TYPE_ALERT_RECEIVED;
   byte TYPE_ALERT_HANDLED;
   byte TYPE_DIRECT_CONNECT;
   byte TYPE_KODIAK_PTT_INCOMING;
   byte TYPE_KODIAK_PTT_OUTGOING;

   byte getType();

   void setType(byte var1);

   int getRefId();

   void setElapsedTime(int var1);

   void setErrorCode(int var1);

   void addCallerIDInfo(Object var1);

   void updateCallerIDInfo(Object var1);

   int getLineId();
}
