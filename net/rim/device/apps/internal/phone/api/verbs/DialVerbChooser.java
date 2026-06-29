package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.framework.verb.PopupVerbWrapper;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.WrapperVerb;

final class DialVerbChooser extends PopupVerbWrapper {
   DialVerbChooser(String menuDescription, String prompt, int ordering, Verb[] verbs, String[] descriptions, Verb defaultVerb) {
      super(menuDescription, prompt, ordering, verbs, descriptions, defaultVerb, true);
   }

   private final DialVerb extractDialVerb(Verb verb) {
      if (verb instanceof DialVerb) {
         return (DialVerb)verb;
      }

      while (verb instanceof WrapperVerb) {
         verb = ((WrapperVerb)verb).getInnerVerb();
         if (verb instanceof DialVerb) {
            return (DialVerb)verb;
         }
      }

      return null;
   }

   @Override
   protected final int promptUser(String prompt, String[] descriptions, int defaultChoiceIndex) {
      if (super._verbs != null && super._verbs.length > 0) {
         DialVerb dialVerb = this.extractDialVerb(super._verbs[0]);
         if (dialVerb != null && dialVerb.requiresUserConfirmation()) {
            if (!dialVerb.confirm()) {
               return -1;
            }

            dialVerb.setRequiresUserConfirmation(false);

            for (int i = 1; i < super._verbs.length; i++) {
               dialVerb = this.extractDialVerb(super._verbs[i]);
               if (dialVerb != null) {
                  dialVerb.setRequiresUserConfirmation(false);
               }
            }
         }
      }

      Dialog d = new DialVerbChooser$1(this, prompt, descriptions, defaultChoiceIndex, true);
      d.setIcon(ThemeManager.getThemeAwareImage("dialog_question"));
      return d.doModal();
   }
}
