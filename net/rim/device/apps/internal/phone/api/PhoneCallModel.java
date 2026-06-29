package net.rim.device.apps.internal.phone.api;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;

public interface PhoneCallModel extends PersistableRIMModel, ReadableList, WritableSet {
   byte TYPE_INCOMING = 0;
   byte TYPE_OUTGOING = 1;
   byte TYPE_UNOPENED = 2;
   byte TYPE_OPENED = 3;
   byte TYPE_CONFERENCE = 4;
   byte TYPE_ALERT_RECEIVED = 5;
   byte TYPE_ALERT_HANDLED = 6;
   byte TYPE_DIRECT_CONNECT = 7;
   byte TYPE_KODIAK_PTT_INCOMING = 8;
   byte TYPE_KODIAK_PTT_OUTGOING = 9;

   byte getType();

   void setType(byte var1);

   int getRefId();

   void setElapsedTime(int var1);

   void setErrorCode(int var1);

   void addCallerIDInfo(Object var1);

   void updateCallerIDInfo(Object var1);

   int getLineId();
}
