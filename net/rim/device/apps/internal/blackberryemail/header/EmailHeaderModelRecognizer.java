package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.Recognizer;

final class EmailHeaderModelRecognizer implements Recognizer {
   private int[] _headerTypes;
   private boolean _noBlanks;

   EmailHeaderModelRecognizer(int headerType) {
      this._headerTypes = new int[1];
      this._headerTypes[0] = headerType;
   }

   EmailHeaderModelRecognizer(int headerType, boolean noBlanks) {
      this(headerType);
      this._noBlanks = noBlanks;
   }

   EmailHeaderModelRecognizer(int[] headerTypes) {
      this._headerTypes = Arrays.copy(headerTypes);
   }

   EmailHeaderModelRecognizer(int[] headerTypes, boolean noBlanks) {
      this(headerTypes);
      this._noBlanks = noBlanks;
   }

   @Override
   public final boolean recognize(Object o) {
      if (o instanceof EmailHeaderModel) {
         EmailHeaderModel headerModel = (EmailHeaderModel)o;

         for (int i = 0; i < this._headerTypes.length; i++) {
            if (headerModel.getHeaderType() == this._headerTypes[i] && (!this._noBlanks || !headerModel.isBlank())) {
               return true;
            }
         }
      }

      return false;
   }
}
