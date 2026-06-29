package net.rim.wica.runtime.provisioning.internal.digester;

import org.xml.sax.Attributes;

public class ObjectCreateRule extends Rule {
   protected Class _resolvedClazz = null;

   public ObjectCreateRule(Digester digester, Class clazz) {
      super(digester);
      this._resolvedClazz = clazz;
   }

   @Override
   public void begin(Attributes attributes) {
      Object instance = this._resolvedClazz.newInstance();
      super._digester.push(instance);
   }

   @Override
   public void end() {
      super._digester.pop();
   }
}
