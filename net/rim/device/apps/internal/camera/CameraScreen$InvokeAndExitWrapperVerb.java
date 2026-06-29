package net.rim.device.apps.internal.camera;

import net.rim.device.api.system.Application;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;

final class CameraScreen$InvokeAndExitWrapperVerb extends Verb {
   private Verb _wrappedVerb;
   private final CameraScreen this$0;

   public CameraScreen$InvokeAndExitWrapperVerb(CameraScreen _1, Verb wrappedVerb) {
      super(wrappedVerb.getOrdering());
      this.this$0 = _1;
      this._wrappedVerb = wrappedVerb;
   }

   @Override
   public final int getOrdering() {
      return this._wrappedVerb.getOrdering();
   }

   @Override
   public final RIMModel getRIMModel() {
      return this._wrappedVerb.getRIMModel();
   }

   @Override
   public final int getVerbGroupId() {
      return this._wrappedVerb.getVerbGroupId();
   }

   @Override
   public final Object invoke(Object parameter) {
      this._wrappedVerb.invoke(parameter);
      Application.getApplication().invokeLater(new CameraScreen$InvokeAndExitWrapperVerb$1(this), 500, false);
      return null;
   }

   @Override
   public final String toString() {
      return this._wrappedVerb.toString();
   }
}
