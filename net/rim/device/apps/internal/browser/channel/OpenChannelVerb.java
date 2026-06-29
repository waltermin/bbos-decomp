package net.rim.device.apps.internal.browser.channel;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class OpenChannelVerb extends Verb {
   private ChannelModel _channel;

   public OpenChannelVerb(ChannelModel channel, int resourceID, int ordering) {
      super(ordering, BrowserResources.getResourceBundle(), resourceID);
      this._channel = channel;
   }

   @Override
   public final Object invoke(Object context) {
      switch (super._rbKey) {
         case 309:
            Channels.openChannel(this._channel);
            return null;
         case 313:
            Channels.channelProperties(this._channel);
         default:
            return null;
      }
   }
}
