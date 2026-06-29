package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class InstalledModulesOptionsItem$ModuleInformationVerb extends Verb {
   private int _moduleHandle;

   InstalledModulesOptionsItem$ModuleInformationVerb(int moduleHandle) {
      super(598288, OptionsResources.getResourceBundle(), 1431);
      this._moduleHandle = moduleHandle;
   }

   @Override
   public final Object invoke(Object parameter) {
      ModuleInformation moduleInformation = new ModuleInformation(this._moduleHandle);
      moduleInformation.open();
      return null;
   }
}
