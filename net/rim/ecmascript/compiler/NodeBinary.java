package net.rim.ecmascript.compiler;

class NodeBinary extends Node {
   protected Node _lhs;
   protected Node _rhs;

   NodeBinary(Node lhs, Node rhs) {
      this._lhs = lhs;
      this._rhs = rhs;
   }
}
