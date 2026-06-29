package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLDirectoryElement;

final class ESHTMLDirectoryElement extends ESHTMLElement {
   ESHTMLDirectoryElement(HTMLDirectoryElement element) {
      super(element, Names.HTMLDirectoryElement);
   }

   final HTMLDirectoryElement getDirectoryElement() {
      return (HTMLDirectoryElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      return name == Names.compact ? Value.makeBooleanValue(this.getDirectoryElement().getCompact()) : super.requestFieldValue(name);
   }
}
