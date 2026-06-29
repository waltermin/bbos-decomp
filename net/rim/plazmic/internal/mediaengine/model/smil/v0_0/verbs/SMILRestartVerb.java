package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.verbs;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.SMILPlayer;

public class SMILRestartVerb extends Verb {
   private SMILPlayer _smilPlayer;

   public SMILRestartVerb(SMILPlayer smilPlayer) {
      super(406804, 1239635442466553906L, "net.rim.device.apps.internal.resource.PMEPluggin", 12);
      this._smilPlayer = smilPlayer;
   }

   @Override
   public Object invoke(Object context) {
      this._smilPlayer.restartPlayback();
      return null;
   }
}
