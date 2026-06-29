package net.rim.device.apps.internal.browser.wml;

import net.rim.device.apps.api.framework.verb.Verb;

public interface WMLInputField {
   void submit(WMLContext var1);

   void setAutoExecVerb(Verb var1);

   boolean validate();

   void refresh();

   boolean isModified();

   void setFocus();
}
