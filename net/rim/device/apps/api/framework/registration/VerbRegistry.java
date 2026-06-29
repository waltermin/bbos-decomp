package net.rim.device.apps.api.framework.registration;

import net.rim.device.apps.api.framework.verb.Verb;

public interface VerbRegistry {
   void register(Verb var1, long var2);

   void deregister(Verb var1, long var2);
}
