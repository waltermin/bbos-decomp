package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class EnhanceCallAudioVerb extends Verb {
   public EnhanceCallAudioVerb() {
      super(71760);
   }

   @Override
   public final Object invoke(Object parameter) {
      EnhanceCallAudioVerb$EnhanceCallAudioDialog ecaDlg = new EnhanceCallAudioVerb$EnhanceCallAudioDialog();
      ecaDlg.show();
      return null;
   }

   @Override
   public final String toString() {
      return PhoneResources.getString(6328);
   }
}
