package net.rim.device.apps.internal.videorecorder;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class VideoRecorderScreen$OptionsVerb extends Verb {
   public VideoRecorderScreen$OptionsVerb() {
      super(16986368, CommonResource.getBundle(), 20);
   }

   @Override
   public final Object invoke(Object parameter) {
      VideoRecorderOptionsScreen.showEditOptionsScreen();
      return null;
   }
}
