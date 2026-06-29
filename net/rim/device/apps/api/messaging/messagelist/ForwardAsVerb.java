package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.ConditionalVerb;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.resources.MessageResources;
import net.rim.device.apps.api.utility.general.SetParameter;

public class ForwardAsVerb extends Verb implements ConditionalVerb {
   private Object _originalMessage;
   private Verb[] _verbs;
   private String _messageTypeName;
   private boolean _canInvokeMethodCalled = false;

   public ForwardAsVerb(Object message) {
      this(message, 602880, MessageResources.getBundle(), 201);
   }

   public ForwardAsVerb(Object message, int menuOrdering, ResourceBundleFamily rbFamily, int rbID) {
      super(menuOrdering, rbFamily, rbID);
      this._originalMessage = message;
      if (this._originalMessage instanceof MessagePartsProvider) {
         this._messageTypeName = ((MessagePartsProvider)this._originalMessage).getName();
      }

      this._verbs = VerbRepository.getVerbRepository(-110058785485458643L).getVerbs(null);
   }

   @Override
   public String toString() {
      if (this._verbs != null && this._verbs.length == 1) {
         StringBuffer str = (StringBuffer)(new Object(super.toString()));
         str.append(' ');
         str.append(StringUtilities.removeChars(this._verbs[0].toString(), "̲"));
         return str.toString();
      } else {
         return super.toString();
      }
   }

   @Override
   public boolean canInvoke(Object parameter) {
      boolean isBrowser = false;
      if (parameter instanceof Object) {
         isBrowser = ((ContextObject)parameter).getFlag(61);
      }

      if (ITPolicy.getBoolean(24, 38, false) && !isBrowser) {
         return false;
      }

      if (this._messageTypeName == null) {
         return false;
      }

      if (this._verbs != null && this._verbs.length != 0) {
         if (this._verbs.length == 1 && this._verbs[0].toString().equals(this._messageTypeName)) {
            return false;
         }

         this._canInvokeMethodCalled = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public Object invoke(Object context) {
      if (!this._canInvokeMethodCalled) {
         return null;
      }

      Verb defaultVerb = null;

      for (int i = 0; i < this._verbs.length; i++) {
         Verb var10000 = this._verbs[i];
         if (this._verbs[i] instanceof Object) {
            ((SetParameter)var10000).setParameter(this._originalMessage);
            if (this._verbs[i].toString().equals(this._messageTypeName)) {
               defaultVerb = this._verbs[i];
            }
         }
      }

      if (this._verbs.length == 1) {
         return this._verbs[0].invoke(context);
      }

      ForwardVerbCombiner verbCombiner = new ForwardVerbCombiner(this);
      return verbCombiner.createWrapperVerb(this._verbs, defaultVerb).invoke(context);
   }
}
