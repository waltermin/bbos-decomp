package net.rim.device.internal.system;

public interface SmartCardPortListener {
   void cardInserted();

   void cardRemoved();

   void dataReceived();

   void dataSent();

   void openSuccessful();

   void setProtocolSuccessful();

   void setProtocolError();

   void openError(int var1);
}
