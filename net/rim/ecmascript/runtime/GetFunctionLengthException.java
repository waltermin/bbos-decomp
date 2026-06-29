package net.rim.ecmascript.runtime;

class GetFunctionLengthException extends Exception {
   private ESFunction _func;

   GetFunctionLengthException(ESFunction func) {
      this._func = func;
   }

   ESFunction getFunction() {
      return this._func;
   }
}
