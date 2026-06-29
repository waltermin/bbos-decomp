package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.Entity;

final class ESEntity extends ESNode {
   ESEntity(Entity entity) {
      super(entity, Names.Entity);
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.publicId) {
         return JavaScriptEngine.makeStringValue(((Entity)this.getNode()).getPublicId());
      } else if (name == Names.systemId) {
         return JavaScriptEngine.makeStringValue(((Entity)this.getNode()).getSystemId());
      } else {
         return name == Names.notationName ? JavaScriptEngine.makeStringValue(((Entity)this.getNode()).getNotationName()) : super.requestFieldValue(name);
      }
   }
}
