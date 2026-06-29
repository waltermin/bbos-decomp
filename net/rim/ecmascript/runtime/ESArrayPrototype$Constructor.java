package net.rim.ecmascript.runtime;

class ESArrayPrototype$Constructor extends Constructor {
   ESArrayPrototype$Constructor() {
      super("Array", GlobalObject.getInstance().arrayPrototype);
   }

   @Override
   public long run() {
      int nParms = this.getNumParms();
      ESArray array;
      if (nParms == 1 && this.getVersion() == 120) {
         array = new ESArray(1);
         array.putIndex(0, this.getParm(0));
      } else if (nParms <= 1) {
         long parm = this.getParm(0, Value.ZERO);
         switch (Value.getType(parm)) {
            case 0:
            case 7:
               long size = ESObject.toArrayIndex(parm);
               if (size == -1) {
                  throw ThrownValue.badArrayLength(Convert.toString(parm));
               }

               array = new ESArray(size);
               break;
            default:
               array = new ESArray(1);
               array.putIndex(0, parm);
         }
      } else {
         array = new ESArray(nParms);

         for (int i = 0; i < nParms; i++) {
            array.putIndex(i, this.getParm(i));
         }
      }

      return Value.makeObjectValue(array);
   }
}
