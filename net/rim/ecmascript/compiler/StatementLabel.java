package net.rim.ecmascript.compiler;

class StatementLabel {
   private String _name;
   private Label _breakLabel;
   private Label _continueLabel;
   private Label _finallyLabel;
   private boolean _isWith;
   private boolean _isTry;
   private StatementLabel _parent;
   private int _jsrLocal;
   Compiler _c;

   StatementLabel(Compiler c, StatementLabel parent, String name) {
      this._parent = parent;
      this._name = name;
      this._c = c;
   }

   String getName() {
      return this._name;
   }

   void addLoopLabels() {
      this._continueLabel = new Label(this._c);
      this._breakLabel = new Label(this._c);
   }

   void addBreakLabel() {
      this._breakLabel = new Label(this._c);
   }

   void addTryLabels(int jsrLocal) {
      this._finallyLabel = new Label(this._c);
      this._isTry = true;
      this._jsrLocal = jsrLocal;
   }

   int getJsrLocal() {
      return this._jsrLocal;
   }

   Label getBreakLabel() {
      return this._breakLabel;
   }

   Label getContinueLabel() {
      return this._continueLabel;
   }

   Label getFinallyLabel() {
      return this._finallyLabel;
   }

   boolean getIsTry() {
      return this._isTry;
   }

   void clearIsTry() {
      this._isTry = false;
   }

   void setParent(StatementLabel parent) {
      this._parent = parent;
   }

   StatementLabel getParent() {
      return this._parent;
   }

   void setIsWith() {
      this._isWith = true;
   }

   void clearIsWith() {
      this._isWith = false;
   }

   boolean getIsWith() {
      return this._isWith;
   }
}
