package net.rim.plazmic.internal.mediaengine.model.smil.v0_0.verbs;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.plazmic.internal.mediaengine.model.smil.v0_0.SMILPlayer;

public class SMILResumeVerb extends Verb {
   private SMILPlayer _smilPlayer;

   public SMILResumeVerb(SMILPlayer smilPlayer) {
      super(406794, 1239635442466553906L, "net.rim.device.apps.internal.resource.PMEPluggin", 13);
      this._smilPlayer = smilPlayer;
   }

   @Override
   public Object invoke(Object context) {
      this._smilPlayer.resumePlayback();
      return null;
   }
}
