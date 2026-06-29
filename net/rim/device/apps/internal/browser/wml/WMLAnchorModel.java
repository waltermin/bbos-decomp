package net.rim.device.apps.internal.browser.wml;

import java.util.Vector;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ActiveFieldCookie;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.URLProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.api.utility.framework.ControllerUtilities;
import net.rim.vm.Array;

final class WMLAnchorModel implements RIMModel, VerbProvider, URLProvider, ActiveFieldCookie {
   private WMLAnchorVerb _anchorVerb;

   @Override
   public final String getURL() {
      return this.getAnchorVerb().getURL();
   }

   @Override
   public final int getURLType() {
      return 1;
   }

   final WMLAnchorVerb getAnchorVerb() {
      return this._anchorVerb;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (this._anchorVerb.getTask() instanceof Go) {
         Go go = (Go)this._anchorVerb.getTask();
         Verb[] tempVerbs = new Object[0];
         Verb defaultVerb = go.getVerbs(context, tempVerbs);
         if (defaultVerb != null) {
            int size = tempVerbs.length;
            Array.resize(verbs, size);

            for (int i = 0; i < size; i++) {
               Verb verb = tempVerbs[i];
               verbs[i] = (WMLAnchorVerb)this._anchorVerb.clone();
               ((WMLAnchorVerb)verbs[i]).setVerb(verb);
               if (verb == defaultVerb) {
                  defaultVerb = verbs[i];
               }
            }

            return defaultVerb;
         }
      }

      Array.resize(verbs, 1);
      verbs[0] = this.getAnchorVerb();
      return verbs[0];
   }

   @Override
   public final boolean invokeApplicationKeyVerb() {
      return ControllerUtilities.invokeApplicationKeyVerb(this);
   }

   @Override
   public final MenuItem getFocusVerbs(CookieProvider provider, Object context, Vector items) {
      return CookieProviderUtilities.getFocusVerbsForActiveField(this, context, items);
   }

   WMLAnchorModel(WMLAnchorVerb anchorVerb) {
      this._anchorVerb = anchorVerb;
   }
}
