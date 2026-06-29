package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.system.ApplicationManager;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.verb.Verb;

final class ConvenienceKeyOptionsImpl$SwitchApplicationVerbFactory implements VerbFactory {
   private ConvenienceKeyOptionsImpl$SwitchApplicationVerb _verb;
   private final ConvenienceKeyOptionsImpl this$0;

   ConvenienceKeyOptionsImpl$SwitchApplicationVerbFactory(ConvenienceKeyOptionsImpl _1) {
      this.this$0 = _1;
      this._verb = new ConvenienceKeyOptionsImpl$SwitchApplicationVerb();
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      ApplicationManager appManager = ApplicationManager.getApplicationManager();
      return !appManager.isSystemLocked() && !this.this$0.isApplicationSwitcherOnConvenienceKey() ? new Verb[]{this._verb} : null;
   }
}
