package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.Notation;

final class ESNotation extends ESNode {
   ESNotation(Notation entity) {
      super(entity, Names.Notation);
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.publicId) {
         return JavaScriptEngine.makeStringValue(((Notation)this.getNode()).getPublicId());
      } else {
         return name == Names.systemId ? JavaScriptEngine.makeStringValue(((Notation)this.getNode()).getSystemId()) : super.requestFieldValue(name);
      }
   }
}
