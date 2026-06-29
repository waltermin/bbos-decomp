package net.rim.device.apps.internal.phone.api;

import net.rim.device.apps.api.framework.verb.Verb;

public interface PTTKeyHandler {
   long GUID;

   boolean isPTTKey(int var1);

   boolean isPTTKeyContext(Object var1);

   boolean isPTTCallActive();

   void onPTTKeyPressedAndHeld(Object var1);

   void onPTTKeyReleased();

   Verb[] getActiveCallVerbs();
}
