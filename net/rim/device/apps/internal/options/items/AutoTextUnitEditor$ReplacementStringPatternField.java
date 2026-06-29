package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.tid.awt.im.InputContext;
import net.rim.vm.Array;

final class AutoTextUnitEditor$ReplacementStringPatternField extends AutoTextEditField implements VerbProvider {
   public AutoTextUnitEditor$ReplacementStringPatternField(String initialValue) {
      super(
         OptionsResources.getString(304),
         initialValue,
         Integer.MAX_VALUE,
         InputContext.getInstance().getActiveInputMethodID() == 4096 ? 4505798651150336L : 4505799724892160L
      );
      this.setCookie(this);
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      } else if (ContextObject.getFlag(context, 0)) {
         Array.resize(verbs, 1);
         verbs[0] = new AutoTextUnitEditor$InsertMacroVerb(this);
         return null;
      } else {
         Array.resize(verbs, 0);
         return null;
      }
   }
}
