package net.rim.device.apps.internal.browser.wml;

import java.util.Vector;

final class SetVar {
   private WMLContextManager _contextManager;
   private Vector _names = (Vector)(new Object());
   private Vector _values = (Vector)(new Object());

   SetVar(WMLContextManager contextManager) {
      this._contextManager = contextManager;
   }

   final void add(WMLVariable name, WMLVariable value) {
      if (name != null && value != null) {
         this._names.addElement(name);
         this._values.addElement(value);
      }
   }

   final void invoke() {
      Vector tmpNames = (Vector)(new Object());
      Vector tmpValues = (Vector)(new Object());

      for (int i = 0; i < this._names.size(); i++) {
         tmpNames.addElement(((WMLVariable)this._names.elementAt(i)).getName());
         tmpValues.addElement(((WMLVariable)this._values.elementAt(i)).getName());
      }

      for (int i = 0; i < tmpNames.size(); i++) {
         String name = (String)tmpNames.elementAt(i);
         if (name.length() > 0) {
            char ch = name.charAt(0);
            if (Character.isLowerCase(ch) || Character.isUpperCase(ch) || ch == '_') {
               this._contextManager.put(name, (String)tmpValues.elementAt(i));
            }
         }
      }
   }
}
