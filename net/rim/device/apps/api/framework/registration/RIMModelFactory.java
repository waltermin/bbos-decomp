package net.rim.device.apps.api.framework.registration;

import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.verb.Verb;

public class RIMModelFactory implements Recognizer, Factory {
   @Override
   public boolean recognize(Object _1) {
      throw null;
   }

   public int getMaximumCount(Object context) {
      return Integer.MAX_VALUE;
   }

   public Verb[] getVerbs(Object context) {
      return null;
   }

   public int getMinimumCount(Object context) {
      return 1;
   }

   @Override
   public Object createInstance(Object _1) {
      throw null;
   }
}
