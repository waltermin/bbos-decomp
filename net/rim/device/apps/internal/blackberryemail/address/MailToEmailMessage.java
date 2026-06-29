package net.rim.device.apps.internal.blackberryemail.address;

import java.util.Vector;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ActiveFieldCookie;
import net.rim.device.api.ui.component.CookieProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CookieProviderUtilities;
import net.rim.vm.Array;

final class MailToEmailMessage implements ActiveFieldCookie, VerbProvider {
   RIMModel[] _toArray;
   RIMModel[] _ccArray;
   String _subject;
   String _body;

   @Override
   public final boolean invokeApplicationKeyVerb() {
      return false;
   }

   @Override
   public final Verb getVerbs(Object data, Verb[] verbs) {
      MailToEmailMessage$MailToEmailMessageVerb verb = new MailToEmailMessage$MailToEmailMessageVerb(this);
      int index = verbs.length;
      Array.resize(verbs, index + 1);
      verbs[index] = verb;
      return verb;
   }

   @Override
   public final MenuItem getFocusVerbs(CookieProvider provider, Object context, Vector items) {
      return CookieProviderUtilities.getFocusVerbsForActiveField(this, context, items);
   }

   MailToEmailMessage(RIMModel[] toArray, RIMModel[] ccArray, String subject, String body) {
      this._toArray = toArray;
      this._ccArray = ccArray;
      this._subject = subject;
      this._body = body;
   }
}
