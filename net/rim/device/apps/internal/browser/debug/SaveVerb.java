package net.rim.device.apps.internal.browser.debug;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.ui.SaveFileDialog;
import net.rim.device.internal.i18n.CommonResource;

final class SaveVerb extends Verb {
   private String _text;
   private String _url;

   SaveVerb(String text, String url) {
      super(332288, CommonResource.getBundle(), 18);
      this._text = text;
      this._url = url;
   }

   @Override
   public final Object invoke(Object context) {
      if (this._text != null) {
         SaveFileDialog.save(this._url, null, 0, false, this._text.getBytes());
      }

      return null;
   }
}
