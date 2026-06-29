package net.rim.device.apps.api.framework.verb;

import net.rim.device.apps.api.framework.registration.RIMModelFactory;

public class RIMModelFactoryCreateVerb extends Verb {
   RIMModelFactory _factory;

   public RIMModelFactoryCreateVerb(RIMModelFactory factory, int ordering, long rbId, String rbName, int rbKey) {
      super(ordering, rbId, rbName, rbKey);
      this._factory = factory;
   }

   @Override
   public Object invoke(Object context) {
      return this._factory.createInstance(context);
   }
}
