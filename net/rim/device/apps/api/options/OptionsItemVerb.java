package net.rim.device.apps.api.options;

import net.rim.device.apps.api.framework.verb.Verb;

public class OptionsItemVerb extends Verb {
   private String _displayString;

   public OptionsItemVerb(String displayString, int ordering) {
      super(ordering);
      this._displayString = displayString;
   }

   @Override
   public String toString() {
      return this._displayString;
   }
}
