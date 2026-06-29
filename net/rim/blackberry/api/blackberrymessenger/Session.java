package net.rim.blackberry.api.blackberrymessenger;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.ui.Field;

public interface Session {
   int STATE_CLOSED;
   int STATE_REQUESTING;
   int STATE_OPEN;

   boolean isOpen();

   int getState();

   void close();

   void send(Message var1);

   void addListener(SessionListener var1, ApplicationDescriptor var2);

   void removeListener(SessionListener var1);

   void sendRequest(SessionSetupListener var1, ApplicationDescriptor var2, String var3);

   void display(String var1);

   void display(String var1, Field var2);

   MessengerContact getContact();
}
