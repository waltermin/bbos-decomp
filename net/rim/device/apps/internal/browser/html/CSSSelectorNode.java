package net.rim.device.apps.internal.browser.html;

import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.internal.browser.css.CSSString;

final class CSSSelectorNode {
   int _element;
   CSSString _classString;
   CSSString _idString;

   CSSSelectorNode(int element) {
      this._element = element;
   }

   final boolean matches(int element, String classString, String idString) {
      if ((this._element == 0 || this._element == element) && (this._idString == null || this._idString.equals(idString))) {
         if (this._classString == null) {
            return true;
         }

         if (classString != null) {
            if (classString.indexOf(32) != -1) {
               StringTokenizer classes = (StringTokenizer)(new Object(classString, ' '));

               while (classes.hasMoreTokens()) {
                  if (this._classString.equals(classes.nextToken())) {
                     return true;
                  }
               }
            } else if (this._classString.equals(classString)) {
               return true;
            }
         }
      }

      return false;
   }
}
