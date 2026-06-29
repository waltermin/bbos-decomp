package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.system.Application;
import net.rim.device.apps.internal.browser.html.HTMLCollectionImpl;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.Node;
import org.w3c.dom.html2.HTMLCollection;
import org.w3c.dom.html2.HTMLSelectElement;

class ESHTMLSelectElement$OptionArray extends ESCollection {
   private final ESHTMLSelectElement this$0;

   public ESHTMLSelectElement$OptionArray(ESHTMLSelectElement _1, HTMLCollection collection) {
      super(collection);
      this.this$0 = _1;
   }

   @Override
   public boolean notifyFieldChanged(String name, long value) {
      HTMLSelectElement select = this.this$0.getSelectElement();
      if (Value.getType(value) == 6) {
         Object newObject = Value.getObjectValue(value);
         if (newObject instanceof ESHTMLOptionElement) {
            ESHTMLOptionElement newOption = (ESHTMLOptionElement)newObject;

            try {
               int index = Integer.parseInt(name);
               Node oldItem = ((HTMLCollectionImpl)super._collection).item(index);
               Application.getApplication().invokeAndWait(new ESHTMLSelectElement$OptionArray$1(this, oldItem, select, newOption));
               return true;
            } finally {
               ;
            }
         }
      } else if (name == Names.length) {
         try {
            int newSize = Convert.toInt32(value);
            Application.getApplication().invokeAndWait(new ESHTMLSelectElement$OptionArray$2(this, select, newSize));
            return true;
         } finally {
            return true;
         }
      }

      return true;
   }
}
