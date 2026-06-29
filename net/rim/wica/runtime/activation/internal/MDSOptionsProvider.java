package net.rim.wica.runtime.activation.internal;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsListItem;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;
import net.rim.wica.runtime.resources.RuntimeResources;

final class MDSOptionsProvider extends OptionsListItem implements OptionsProviderRegistration$OptionsProvider {
   private ActivationServiceImpl _activationService;

   MDSOptionsProvider(ActivationServiceImpl activationService) {
      super(RuntimeResources.getResourceBundleFamily(), 34, -1514481539159318190L);
      this._activationService = activationService;
   }

   @Override
   protected final void open() {
      this._activationService.run();
   }

   @Override
   public final Vector getOptionsItems() {
      Vector items = new Vector(1);
      items.addElement(this);
      return items;
   }
}
