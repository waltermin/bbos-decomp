package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class FlashVerb extends Verb {
   private String _data;

   public FlashVerb() {
      super(70224);
   }

   public final void setData(String data) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final Object invoke(Object parameter) {
      VoiceServices.flash(this._data);
      this._data = null;
      VoiceServices.broadcastEvent(3006);
      return null;
   }

   @Override
   public final String toString() {
      String label = PhoneResources.getString(6002);
      if (this._data != null) {
         label = label + ' ' + this._data;
      }

      return label;
   }
}
