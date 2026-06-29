package net.rim.wica.runtime.provisioning.internal.digester;

import org.xml.sax.Attributes;

public class Rule {
   protected Digester _digester = null;

   public Rule(Digester digester) {
      this._digester = digester;
   }

   public void begin(Attributes attributes) {
   }

   public void body(String text) {
   }

   public void end() {
   }

   public void finish() {
   }
}
