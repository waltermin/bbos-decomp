package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.ProcessingInstruction;

final class ESProcessingInstruction extends ESNode {
   ESProcessingInstruction(ProcessingInstruction pi) {
      super(pi, Names.ProcessingInstruction);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.target) {
         return JavaScriptEngine.makeStringValue(((ProcessingInstruction)this.getNode()).getTarget());
      }

      if (name == Names.data) {
         try {
            return JavaScriptEngine.makeStringValue(((ProcessingInstruction)this.getNode()).getData());
         } catch (Throwable var4) {
            throw ESDOMException.createThrownValue(e);
         }
      } else {
         return super.requestFieldValue(name);
      }
   }
}
