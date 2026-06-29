package net.rim.device.apps.internal.memorycleaner;

import net.rim.device.apps.api.options.OptionsItemVerb;

final class MemoryCleanerOptionsItem$CleanNowVerb extends OptionsItemVerb {
   MemoryCleanerOptionsItem$CleanNowVerb(String displayString) {
      super(displayString, 10000);
   }

   @Override
   public final Object invoke(Object parameter) {
      new MemoryCleaner().start(false);
      return null;
   }
}
