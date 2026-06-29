package net.rim.device.apps.api.messaging.implus;

import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.verb.Verb;

public interface IMPlusComposeModel {
   Verb getUseOnceVerb();

   Recognizer getRecognizer();

   String getUseEntryPrefix();

   long getObjectType();
}
