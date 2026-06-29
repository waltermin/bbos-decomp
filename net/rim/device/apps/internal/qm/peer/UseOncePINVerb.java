package net.rim.device.apps.internal.qm.peer;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.editor.UseOnceEditorScreen;

final class UseOncePINVerb extends Verb {
   public UseOncePINVerb() {
      super(327968);
   }

   @Override
   public final String toString() {
      return PeerResources.getString(2019);
   }

   @Override
   public final Object invoke(Object parameter) {
      String initialValue = (String)ContextObject.get(parameter, 253);
      return UseOnceEditorScreen.showUseOnceScreen(PeerResources.getString(2022), 4246852237058296601L, initialValue);
   }
}
