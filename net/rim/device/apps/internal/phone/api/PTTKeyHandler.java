package net.rim.device.apps.internal.phone.api;

import net.rim.device.apps.api.framework.verb.Verb;

public interface PTTKeyHandler {
   long GUID = -7975050928526187730L;

   boolean isPTTKey(int var1);

   boolean isPTTKeyContext(Object var1);

   boolean isPTTCallActive();

   void onPTTKeyPressedAndHeld(Object var1);

   void onPTTKeyReleased();

   Verb[] getActiveCallVerbs();
}
