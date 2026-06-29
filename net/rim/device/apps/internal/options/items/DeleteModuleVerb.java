package net.rim.device.apps.internal.options.items;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.InternalServices;

final class DeleteModuleVerb extends Verb {
   private int _moduleHandle;
   private String _name;

   DeleteModuleVerb(int moduleHandle, String name) {
      super(598352, CommonResource.getBundle(), 17);
      this._moduleHandle = moduleHandle;
      this._name = name;
   }

   @Override
   public final Object invoke(Object parameter) {
      String prompt = CommonResource.format(10025, this._name);
      if (Dialog.ask(2, prompt, -1) != 3) {
         return null;
      }

      Object[] values = new Object[]{this._name};
      switch (CodeModuleManager.deleteModuleEx(this._moduleHandle, false)) {
         case 7:
         case 8:
            prompt = MessageFormat.format(OptionsResources.getString(1460), values);
            if (Dialog.ask(3, prompt, -1) == 4 && CodeModuleManager.deleteModuleEx(this._moduleHandle, true) == 6) {
               prompt = OptionsResources.getString(1486);
               if (Dialog.ask(3, prompt, -1) == 4) {
                  InternalServices.initiateReset("DeleteModule");
               }
            }
         case 6:
            return prompt;
         case 9:
         default:
            Dialog.alert(MessageFormat.format(OptionsResources.getString(1490), values));
            return null;
      }
   }
}
