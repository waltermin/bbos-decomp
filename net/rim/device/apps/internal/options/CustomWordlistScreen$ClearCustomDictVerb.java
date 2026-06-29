package net.rim.device.apps.internal.options;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.tid.awt.im.InputContext;
import net.rim.tid.im.SLControlObject;

final class CustomWordlistScreen$ClearCustomDictVerb extends Verb {
   public CustomWordlistScreen$ClearCustomDictVerb(ResourceBundleFamily rb, int key) {
      super(629072, rb, key);
   }

   @Override
   public final Object invoke(Object parm) {
      if (Dialog.ask(3, OptionsResources.getString(1871), -1) != -1) {
         SLControlObject co = (SLControlObject)InputContext.getInstance().getInputMethodControlObject();
         if (co != null) {
            if (CustomWordlistScreen._screenType == 3) {
               co.actionPerformed(-79, null);
               return this;
            }

            co.actionPerformed(103, new Object(CustomWordlistScreen.getDataType()));
            return this;
         }
      }

      return null;
   }
}
