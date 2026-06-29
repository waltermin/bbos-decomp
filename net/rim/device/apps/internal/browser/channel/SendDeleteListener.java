package net.rim.device.apps.internal.browser.channel;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;
import net.rim.device.apps.internal.browser.core.PendingRequestListener;
import net.rim.device.apps.internal.browser.page.PageModel;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.CacheResult;

final class SendDeleteListener implements PendingRequestListener, Persistable {
   private ChannelModel _channel;
   private int _retries;

   public SendDeleteListener(ChannelModel channel, int retries) {
      this._channel = channel;
      this._retries = retries;
   }

   @Override
   public final void notify(PageModel pageModel) {
      CacheResult cacheResult = pageModel.getModelResult().getCacheResult();
      if (cacheResult != null && cacheResult.getStatus() == 200) {
         EventLogger.logEvent(
            1907089860548946979L,
            ((StringBuffer)(new Object("CMdc - ")))
               .append(this._channel.getID())
               .append('\n')
               .append(this._channel.getDeleteURL())
               .append('\n')
               .append(cacheResult.getStatus())
               .toString()
               .getBytes(),
            0
         );
      } else {
         String logString = cacheResult == null
            ? "Null cache result"
            : ((StringBuffer)(new Object())).append(cacheResult.getStatus()).append(": ").append(cacheResult.getExceptionString()).toString();
         EventLogger.logEvent(
            1907089860548946979L,
            ((StringBuffer)(new Object("CMdc - ")))
               .append(this._channel.getID())
               .append('\n')
               .append(this._channel.getDeleteURL())
               .append('\n')
               .append(logString)
               .toString()
               .getBytes(),
            0
         );
         if (this._retries > 0) {
            Channels.sendDeleteNotification(this._channel, this._retries - 1);
         } else {
            String message = MessageFormat.format(BrowserResources.getString(818), new Object[]{this._channel.getTitle()});
            Dialog dialog = (Dialog)(new Object(0, message, 0, null, 33554432));
            dialog.setIcon(ThemeManager.getThemeAwareImage("dialog_exclamation"));
            BrowserDaemonRegistry.getInstance().invokeLater(new SendDeleteListener$1(this, dialog));
         }
      }
   }
}
