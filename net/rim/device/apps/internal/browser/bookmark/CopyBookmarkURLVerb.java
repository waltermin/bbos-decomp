package net.rim.device.apps.internal.browser.bookmark;

import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

final class CopyBookmarkURLVerb extends Verb {
   private String _url;

   public CopyBookmarkURLVerb(String url) {
      super(1312112, BrowserResources.getResourceBundle(), 466);
      this._url = url;
   }

   @Override
   public final Object invoke(Object context) {
      Clipboard.getClipboard().put(this._url);
      Dialog.alert(BrowserResources.getString(519));
      return null;
   }
}
