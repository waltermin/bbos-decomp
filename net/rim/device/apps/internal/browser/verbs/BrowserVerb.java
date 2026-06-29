package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.verb.Verb;

public class BrowserVerb extends Verb {
   protected BrowserVerb(int ordering) {
   }

   protected BrowserVerb(int ordering, ResourceBundleFamily rb, int rbKey) {
   }

   protected BrowserVerb(int ordering, long rbId, String rbName, int rbKey) {
      super(ordering, rbId, rbName, rbKey);
   }

   public boolean isEnabled() {
      return true;
   }

   public boolean isModal() {
      return false;
   }

   public void cleanup() {
   }
}
