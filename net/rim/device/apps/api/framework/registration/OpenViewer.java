package net.rim.device.apps.api.framework.registration;

import net.rim.device.apps.api.framework.model.RIMModel;

public interface OpenViewer {
   RIMModel getOpenedModel(Object var1);

   long getModelType(Object var1);

   void notifyOfOpenedModelChange(RIMModel var1, RIMModel var2, Object var3);
}
