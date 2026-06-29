package net.rim.device.apps.api.framework.verb;

import net.rim.device.apps.api.framework.model.Recognizer;

public interface VerbCombiner extends Recognizer {
   Verb createWrapperVerb(Verb[] var1, Verb var2);
}
