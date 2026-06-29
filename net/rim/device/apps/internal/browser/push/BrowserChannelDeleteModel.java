package net.rim.device.apps.internal.browser.push;

import net.rim.device.apps.internal.browser.channel.Channels;

public final class BrowserChannelDeleteModel extends BrowserPushModel {
   private String _channelId;

   public BrowserChannelDeleteModel(String channel) {
      this._channelId = channel;
   }

   @Override
   public final void run() {
      this.run(28, this._channelId);
   }

   @Override
   protected final void doAction() {
      Channels.deleteChannel(this._channelId);
   }
}
