package net.rim.device.apps.internal.browser.options;

import net.rim.device.api.ui.Screen;
import net.rim.device.apps.api.framework.verb.Verb;

public interface IBrowserProperty {
   Verb getOpenVerb(boolean var1);

   void saveProperty();

   String getLabel();

   Screen getScreen(boolean var1);
}
