package net.rim.device.apps.internal.browser.push;

import net.rim.device.api.browser.push.PushOptions;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.browser.channel.ChannelModel;
import net.rim.device.apps.internal.browser.channel.Channels;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.CacheResult;

public final class BrowserChannelModel extends BrowserPushModel {
   private ChannelModel _channelModel;
   private CacheResult _cacheResult;

   public BrowserChannelModel(ChannelModel channelModel, CacheResult cacheResult) {
      this._channelModel = channelModel;
      this._cacheResult = cacheResult;
   }

   @Override
   public final void run() {
      this.run(29, this._channelModel.getID());
      if (PushOptions.getOptions().getAcceptMode(2, this.mapPushSourceToProtocolType(super._connectionType)) == 0 && this._channelModel.getPriority() == 3) {
         NotificationsManager.triggerImmediateEvent(4665536253483290822L, 0, null, null);
         String notificationMsg = MessageFormat.format(BrowserResources.getString(635), new String[]{this._channelModel.getTitle()});
         UiApplication.getUiApplication().invokeLater(new BrowserChannelModel$1(this, notificationMsg));
      }
   }

   @Override
   protected final void doAction() {
      if (this._cacheResult != null) {
         BrowserPushUtilities.addToCache(this._cacheResult, true);
      }

      Channels.addChannel(this._channelModel);
   }
}
