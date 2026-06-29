package net.rim.device.apps.internal.blackberryemail.email;

public interface MorePartModel {
   int getMorePartID();

   void setMorePartID(int var1);

   boolean isMoreAvailable();

   boolean isTruncated();

   int getLengthOnDevice();

   void setLengthOnDevice(int var1);

   int getTrueLength();

   void setTrueLength(int var1);

   int getAvailableLength();

   void setAvailableLength(int var1);

   boolean getMoreRequestSent();

   void setMoreRequestSent();

   void clearMoreRequestSent();

   void receiveMore(Object var1, Object var2);

   boolean isAutoMoreAvailable();

   boolean suppressNotification();
}
