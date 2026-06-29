package net.rim.device.apps.internal.api.serialformats;

class PARAMETER {
   public int _encoding;
   public int _value;
   public String _charset;
   public String _language;
   public EXTENSION _extension;

   public void setExtension(String name, String value) {
      if (this._extension == null) {
         this._extension = new EXTENSION(name, value);
      } else {
         EXTENSION previous = null;

         for (EXTENSION current = this._extension; current != null; current = current.getNext()) {
            previous = current;
         }

         previous.setNext(new EXTENSION(name, value));
      }
   }
}
