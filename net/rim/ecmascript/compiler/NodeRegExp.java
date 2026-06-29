package net.rim.ecmascript.compiler;

class NodeRegExp extends Node {
   protected String _flags;
   protected String _value;

   NodeRegExp(String value, String flags) {
      this._flags = flags;
      this._value = value;
   }

   @Override
   void generate(Function f) {
      f.addCode(103, f.addId("RegExp"));
      f.addCode(36, f.addId("RegExp"));
      f.addCode(104, f.addString(this._value));
      f.addCode(104, f.addString(this._flags));
      f.addCode(8, 2);
   }
}
