package net.rim.device.apps.internal.mms.ui;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

class VideoPresentationElementField$MMSCloseVerb extends Verb {
   VideoPresentationElementField$MMSMediaPlayerDialog _player;

   public VideoPresentationElementField$MMSCloseVerb(VideoPresentationElementField$MMSMediaPlayerDialog player) {
      super(268501008, CommonResource.getBundle(), 9);
      this._player = player;
   }

   @Override
   public Object invoke(Object parameter) {
      this._player.close();
      return null;
   }
}
