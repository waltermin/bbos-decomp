package net.rim.device.apps.internal.browser.model;

import java.util.Vector;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ActiveFieldCookie;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.AbstractStringWrapper;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.device.apps.api.utility.framework.ControllerUtilities;
import net.rim.device.apps.internal.browser.verbs.SendDTMFVerb;
import net.rim.vm.Array;

public final class DTMFModel implements RIMModel, VerbProvider, ActiveFieldCookie {
   private byte[] _tone;

   @Override
   public final boolean invokeApplicationKeyVerb() {
      return ControllerUtilities.invokeApplicationKeyVerb(this);
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      Verb defaultVerb = null;
      Array.resize(verbs, 1);
      verbs[0] = new SendDTMFVerb(this._tone);
      if (ContextObject.getFlag(context, 2)) {
         defaultVerb = verbs[0];
      }

      return defaultVerb;
   }

   @Override
   public final MenuItem getFocusVerbs(CookieProvider provider, Object context, Vector items) {
      return CookieProviderUtilities.getFocusVerbsForActiveField(this, context, items);
   }

   public DTMFModel(Object initialData) {
      String str = (String)ContextObject.get(initialData, 253);
      if (str != null) {
         AbstractString absStr = AbstractStringWrapper.createInstance(str);
         int prefixLen = DTMFStringPattern.getPrefixLength(absStr);
         if (prefixLen > 0) {
            str = str.substring(prefixLen).trim();
         }

         int index = str.indexOf(33);
         if (index != -1) {
            this._tone = str.substring(0, index).getBytes();
            return;
         }

         this._tone = str.getBytes();
      }
   }
}
