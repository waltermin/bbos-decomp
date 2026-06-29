package net.rim.ecmascript.compiler;

class NodeUnary extends Node {
   protected Node _lhs;

   NodeUnary(Node lhs) {
      this._lhs = lhs;
   }
}
