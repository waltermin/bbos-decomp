package net.rim.device.apps.internal.browser.options;

import net.rim.device.apps.api.framework.verb.Verb;

final class ClearVerb extends Verb {
   public ClearVerb(int id) {
      super(1314816, -229261654107783483L, "net.rim.device.apps.internal.resource.Browser", id);
   }

   @Override
   public final Object invoke(Object context) {
      switch (super._rbKey) {
         case 538:
            System.exit(0);
         default:
            return null;
      }
   }
}
