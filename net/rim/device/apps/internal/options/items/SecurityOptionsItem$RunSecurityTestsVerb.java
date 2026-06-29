package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.crypto.fips.SelfTests;

public final class SecurityOptionsItem$RunSecurityTestsVerb extends Verb {
   SecurityOptionsItem$RunSecurityTestsVerb() {
      super(200976, OptionsResources.getResourceBundle(), 1455);
   }

   @Override
   public final Object invoke(Object parameter) {
      SelfTests selfTests = new SelfTests(false);
      selfTests.start();
      return null;
   }
}
