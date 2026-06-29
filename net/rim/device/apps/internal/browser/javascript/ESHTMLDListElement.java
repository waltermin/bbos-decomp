package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLDListElement;

final class ESHTMLDListElement extends ESHTMLElement {
   ESHTMLDListElement(HTMLDListElement element) {
      super(element, Names.HTMLDListElement);
   }

   final HTMLDListElement getDListElement() {
      return (HTMLDListElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      return name == Names.compact ? Value.makeBooleanValue(this.getDListElement().getCompact()) : super.requestFieldValue(name);
   }
}
