package net.rim.device.apps.internal.profiles;

import net.rim.device.api.media.control.AudioPathControl;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class SendToHandsetVerb extends Verb {
   private AudioPathControl _control;

   public SendToHandsetVerb(AudioPathControl control) {
      super(1266752);
      this._control = control;
   }

   @Override
   public final Object invoke(Object context) {
      try {
         this._control.setAudioPath(0);
         return null;
      } finally {
         ;
      }
   }

   @Override
   public final String toString() {
      return CommonResources.getString(12);
   }
}
