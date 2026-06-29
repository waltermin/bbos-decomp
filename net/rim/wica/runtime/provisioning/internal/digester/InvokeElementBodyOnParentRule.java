package net.rim.wica.runtime.provisioning.internal.digester;

import net.rim.wica.runtime.provisioning.internal.elements.AbstractElement;

public class InvokeElementBodyOnParentRule extends Rule {
   private String _pattern;
   private String _bodyText = null;

   public InvokeElementBodyOnParentRule(String pattern, Digester digester) {
      super(digester);
      this._pattern = pattern;
   }

   @Override
   public void body(String text) {
      this._bodyText = text;
   }

   @Override
   public void end() {
      AbstractElement parent = (AbstractElement)super._digester.peek(0);
      parent.addElementBody(this._pattern, this._bodyText);
   }
}
