package net.rim.device.apps.api.framework.model;

import net.rim.device.apps.api.framework.verb.Verb;

public interface VerbProvider extends RIMModel {
   Verb getVerbs(Object var1, Verb[] var2);
}
