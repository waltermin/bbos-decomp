package net.rim.ecmascript.runtime;

class ESErrorPrototype$Constructor extends Constructor {
   private ESObject _errorPrototype;
   private String _name;

   ESErrorPrototype$Constructor(String name, ESObject prototype) {
      super(name, prototype);
      this._errorPrototype = prototype;
      this._name = name;
   }

   @Override
   public long run() {
      long parm = this.getParm(0);
      String message = null;
      if (Value.getType(parm) != 2) {
         message = Convert.toString(parm);
      }

      return Value.makeObjectValue(new ESError(this._name, message, this._errorPrototype));
   }
}
