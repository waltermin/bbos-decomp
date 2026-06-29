package net.rim.device.api.system;

public interface SIMCardPhoneBookListener extends SIMCardListener {
   void responsePhoneBookRead(boolean var1);

   void responsePhoneBookWrite(boolean var1, int var2, int var3);

   void responsePhoneBookDelete(boolean var1, int var2);
}
