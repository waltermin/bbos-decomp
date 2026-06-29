package net.rim.device.apps.internal.browser.debug;

import net.rim.device.apps.api.framework.verb.Verb;

final class DumpVerb extends Verb {
   private String _text;
   private String _url;

   DumpVerb(String text, String url) {
      super(0);
      this._text = text;
      this._url = url;
   }

   @Override
   public final Object invoke(Object context) {
      if (this._text != null) {
         System.out.println("URL: " + this._url);
         System.out.println(this._text);
      }

      return null;
   }

   @Override
   public final String toString() {
      return "Dump to sysout";
   }
}
