package net.rim.device.api.ui.theme;

class VersionTokenizer {
   private String _versionString;
   private int _index;

   public VersionTokenizer(String versionString) {
      this._versionString = versionString;
      this._index = 0;
   }

   public Integer nextToken() {
      Integer result = null;
      if (this._index < this._versionString.length()) {
         int decimal = this._versionString.indexOf(46, this._index);
         String candidate = decimal == -1 ? this._versionString.substring(this._index) : this._versionString.substring(this._index, decimal);

         label38:
         try {
            result = Integer.valueOf(candidate);
         } finally {
            break label38;
         }

         this._index = decimal == -1 ? this._versionString.length() : decimal + 1;
      }

      return result;
   }
}
