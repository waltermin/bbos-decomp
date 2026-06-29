package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLMenuElement;

final class ESHTMLMenuElement extends ESHTMLElement {
   ESHTMLMenuElement(HTMLMenuElement element) {
      super(element, Names.HTMLMenuElement);
   }

   final HTMLMenuElement getMenuElement() {
      return (HTMLMenuElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      return name == Names.compact ? Value.makeBooleanValue(this.getMenuElement().getCompact()) : super.requestFieldValue(name);
   }
}
