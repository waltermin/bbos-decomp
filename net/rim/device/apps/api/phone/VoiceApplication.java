package net.rim.device.apps.api.phone;

import java.util.Vector;
import net.rim.device.api.ui.Screen;

public interface VoiceApplication {
   void run();

   boolean stopCurrentCall(Object var1);

   boolean stopConferenceCall();

   boolean stopAllCalls(Object var1);

   boolean inForeground();

   void displayScreen(Screen var1);

   void requestForeground(Runnable var1, Object var2);

   Object getCurrentCall();

   Object getIncomingCall();

   Object getInactiveCall();

   Vector getCurrentCalls();

   Object getCallCacheLock();

   void editCallNotes();

   Object getRedialInfo();

   void placeCall();

   void startEmergencyCall(String var1, boolean var2);

   Object getSpeedDialInfo(char var1);

   Object getSpeedDialInfo(char var1, boolean var2);

   void onVoiceAppExit();
}
