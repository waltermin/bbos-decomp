package net.rim.ecmascript.runtime;

class BuildArgumentsException extends Exception {
   private ESFunction _func;

   BuildArgumentsException(ESFunction func) {
      this._func = func;
   }

   ESFunction getFunction() {
      return this._func;
   }
}
