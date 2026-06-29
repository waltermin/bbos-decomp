package net.rim.device.apps.internal.browser.channel;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class DeleteChannelVerb extends Verb {
   private ChannelModel _channel;

   public DeleteChannelVerb(ChannelModel channel) {
      super(1315664, -229261654107783483L, "net.rim.device.apps.internal.resource.Browser", 312);
      this._channel = channel;
   }

   @Override
   public final Object invoke(Object context) {
      String title = this._channel.getTitle();
      String[] name = new String[]{title != null && title.length() != 0 ? title : this._channel.getURL()};
      String deleteUrl = this._channel.getDeleteURL();
      String message;
      if (deleteUrl != null && deleteUrl.length() > 0) {
         message = MessageFormat.format(BrowserResources.getString(817), name);
      } else {
         message = MessageFormat.format(BrowserResources.getString(282), name);
      }

      if (Dialog.ask(2, message) == 3) {
         Channels.deleteChannel(this._channel);
      }

      return null;
   }
}
