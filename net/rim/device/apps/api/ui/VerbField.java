package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;

public class VerbField extends LabelField {
   private Verb _verb;
   private ContextObject _ctx;

   public VerbField(Verb verb, ContextObject ctx) {
      super(verb.toString(), 18014398509481984L);
      this._verb = verb;
      this._ctx = ctx;
   }

   @Override
   protected boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this._verb.invoke(this._ctx);
            return true;
         default:
            return false;
      }
   }
}
