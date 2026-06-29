package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.Text;

class ESText extends ESCharacterData {
   ESText(Text text) {
      this(text, Names.Text);
   }

   ESText(Text text, String name) {
      super(text, name, JavaScriptEngine.getInstance()._textPrototype);
   }
}
