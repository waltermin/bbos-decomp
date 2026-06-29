package net.rim.device.apps.internal.lbs;

import java.util.Vector;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ActiveFieldCookie;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.vm.Array;

public final class LocationDocument implements ActiveFieldCookie, VerbProvider {
   public String _link;

   @Override
   public final MenuItem getFocusVerbs(CookieProvider provider, Object context, Vector items) {
      return CookieProviderUtilities.getFocusVerbsForActiveField(this, context, items);
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      Verb verb = new ShowLocationDocumentVerb(this._link);
      int index = verbs.length;
      Array.resize(verbs, index + 1);
      verbs[index] = verb;
      return verb;
   }

   @Override
   public final boolean invokeApplicationKeyVerb() {
      return false;
   }

   public LocationDocument(String link) {
      this._link = link;
   }
}
