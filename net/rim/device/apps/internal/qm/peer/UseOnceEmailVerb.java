package net.rim.device.apps.internal.qm.peer;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.editor.UseOnceEditorScreen;

final class UseOnceEmailVerb extends Verb {
   public UseOnceEmailVerb() {
      super(327968);
   }

   @Override
   public final String toString() {
      return PeerResources.getString(2017);
   }

   @Override
   public final Object invoke(Object parameter) {
      String initialValue = (String)ContextObject.get(parameter, 253);
      return UseOnceEditorScreen.showUseOnceScreen(PeerResources.getString(2020), -2985347935260258684L, initialValue);
   }
}
