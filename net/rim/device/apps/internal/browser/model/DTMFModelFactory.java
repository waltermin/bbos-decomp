package net.rim.device.apps.internal.browser.model;

import net.rim.device.api.ui.component.ActiveFieldContext;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.ContextObject;

public final class DTMFModelFactory implements Factory {
   @Override
   public final Object createInstance(Object initialData) {
      if (initialData instanceof ActiveFieldContext) {
         ActiveFieldContext afc = (ActiveFieldContext)initialData;
         initialData = new ContextObject();
         ContextObject.put(initialData, 253, afc.getData());
      }

      ContextObject.verifyNonNull(initialData);
      return new DTMFModel(initialData);
   }
}
