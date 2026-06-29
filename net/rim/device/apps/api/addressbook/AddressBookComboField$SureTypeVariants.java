package net.rim.device.apps.api.addressbook;

import net.rim.tid.im.SLControlObject;
import net.rim.vm.Array;

class AddressBookComboField$SureTypeVariants {
   private String[][][] _variantSet;
   private String _text;
   private String[][][][][][] _cache;
   private boolean _ignoreVariants;
   private final AddressBookComboField this$0;

   public AddressBookComboField$SureTypeVariants(AddressBookComboField _1) {
      this.this$0 = _1;
      this._variantSet = new Object[0][][];
      this._text = "";
      this._cache = new Object[1][][][];
      this._ignoreVariants = false;
      this._cache[0] = new Object[0][][];
   }

   public void ignoreVariants() {
      this._ignoreVariants = true;
      this._variantSet = new Object[1][1][];
      this._cache = (Object[][][])null;
   }

   public void update(String text) {
      if (this._ignoreVariants) {
         this._variantSet[0][0] = text;
      } else {
         this._text = text;
         String[][][] cachedVariants = this.getCachedVariants();
         if (cachedVariants != null) {
            this._variantSet = cachedVariants;
         } else {
            SLControlObject controlObject = (SLControlObject)this.this$0.getInputContext().getInputMethodControlObject();
            String[][][] varsContainer = new Object[1][][];
            controlObject.actionPerformed(107, varsContainer);
            String[] variants = varsContainer[0];
            String[] components = this.makeComponents(text);
            int count = components.length;
            Array.resize(this._variantSet, count);
            if (variants != null && count > 0) {
               this._variantSet[count - 1] = variants;
            }

            this.overlayComponents(components);
            this.setCachedVariants();
         }
      }
   }

   public String[][][] getVariantSet() {
      return this._variantSet;
   }

   private String[] makeComponents(String text) {
      StringBuffer[] components = new Object[0];
      int count = 0;
      boolean findNonSpace = true;

      for (int i = 0; i < text.length(); i++) {
         char ch = text.charAt(i);
         boolean isSpace = this.isSeparator(ch);
         if (findNonSpace) {
            if (!isSpace) {
               Array.resize(components, ++count);
               components[count - 1] = (StringBuffer)(new Object());
               components[count - 1].append(ch);
               findNonSpace = false;
            }
         } else if (isSpace) {
            findNonSpace = true;
         } else {
            components[count - 1].append(ch);
         }
      }

      String[] strComponents = new Object[components.length];

      for (int i = 0; i < strComponents.length; i++) {
         strComponents[i] = components[i].toString();
      }

      return strComponents;
   }

   private void overlayComponents(String[] components) {
      for (int i = 0; i < components.length; i++) {
         if (this._variantSet[i] == null) {
            this._variantSet[i] = new Object[1];
            this._variantSet[i][0] = components[i];
         }
      }
   }

   private boolean isSeparator(char ch) {
      switch (ch) {
         case ' ':
         case ',':
         case '.':
         case ':':
         case ';':
         case '@':
         case '_':
            return true;
         default:
            return false;
      }
   }

   private void setCachedVariants() {
      if (this._cache != null) {
         int index = this._text.length();
         Array.resize(this._cache, index + 1);
         this._cache[index] = this.copy(this._variantSet);
      }
   }

   private String[][][] getCachedVariants() {
      if (this._cache == null) {
         return (Object[][])null;
      }

      int index = this._text.length();
      if (this._cache.length < index + 1) {
         return (Object[][])null;
      }

      Array.resize(this._cache, index + 1);
      return this.copy(this._cache[index]);
   }

   private String[][][] copy(String[][][] varSet) {
      if (varSet == null) {
         return (Object[][])null;
      }

      int vsLength = varSet.length;
      String[][][] copy = new Object[vsLength][][];

      for (int i = 0; i < vsLength; i++) {
         if (varSet[i] == null) {
            copy[i] = null;
         } else {
            int subLength = varSet[i].length;
            copy[i] = new Object[subLength];

            for (int j = 0; j < subLength; j++) {
               copy[i][j] = varSet[i][j];
            }
         }
      }

      return copy;
   }

   public void dump() {
      this.dumpVariants(this._variantSet);
      System.out.println();
   }

   private void dumpVariants(String[][][] vars) {
      if (vars == null) {
         System.out.print("null");
      } else {
         System.out.print("[");

         for (int i = 0; i < vars.length; i++) {
            if (vars[i] == null) {
               System.out.print("null_set");
            } else {
               System.out.print("[");

               for (int j = 0; j < vars[i].length; j++) {
                  if (j != 0) {
                     System.out.print(", ");
                  }

                  System.out.print(vars[i][j]);
               }

               System.out.print("]");
            }
         }

         System.out.print("]");
      }
   }
}
