package net.rim.device.apps.api.utility.framework;

import net.rim.device.apps.api.framework.model.RIMModel;

public class ModelScreen$NotificationRunnable implements Runnable {
   RIMModel _oldModel;
   RIMModel _newModel;
   Object _context;
   private final ModelScreen this$0;

   public ModelScreen$NotificationRunnable(ModelScreen _1, RIMModel oldModel, RIMModel newModel, Object context) {
      this.this$0 = _1;
      this._oldModel = oldModel;
      this._newModel = newModel;
      this._context = context;
   }

   @Override
   public void run() {
      this.this$0.notifyOfOpenedModelChange(this._oldModel, this._newModel, this._context);
   }
}
