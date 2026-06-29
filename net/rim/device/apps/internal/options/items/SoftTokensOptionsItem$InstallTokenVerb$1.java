package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.model.Recognizer;

class SoftTokensOptionsItem$InstallTokenVerb$1 implements Recognizer {
   private final SoftTokensOptionsItem$InstallTokenVerb this$1;

   SoftTokensOptionsItem$InstallTokenVerb$1(SoftTokensOptionsItem$InstallTokenVerb _1) {
      this.this$1 = _1;
   }

   @Override
   public boolean recognize(Object obj) {
      if (obj instanceof Object) {
         String filename = (String)obj;
         int extPos = filename.lastIndexOf(46);
         if (extPos > 0) {
            String extension = filename.substring(extPos + 1, filename.length());
            return extension.equals("sdtid");
         }
      }

      return false;
   }
}
