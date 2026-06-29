package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.apps.api.framework.model.Recognizer;

class TemplateComposeVerb$1 implements Recognizer {
   @Override
   public boolean recognize(Object obj) {
      if (obj instanceof Object) {
         String filename = (String)obj;
         int extPos = filename.lastIndexOf(46);
         if (extPos > 0) {
            String extension = filename.substring(extPos + 1, filename.length());
            return extension.equals("mms");
         }
      }

      return false;
   }
}
