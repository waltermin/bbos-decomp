package net.rim.ecmascript.compiler;

class Label {
   private int _id;
   private Block _block;
   private boolean _isGenerated;
   private Label _redirect;

   Label(Compiler _c) {
      _c.addLabel(this);
   }

   int getId() {
      return this._id;
   }

   void setId(int id) {
      this._id = id;
   }

   Block getBlock() {
      return this._block;
   }

   void setBlock(Block block) {
      this._block = block;
   }

   boolean getIsGenerated() {
      return this._isGenerated;
   }

   void setIsGenerated() {
      this._isGenerated = true;
   }

   void setRedirect(Label l) {
      for (Label next = l; next != null; next = next._redirect) {
         if (next == this) {
            return;
         }
      }

      this._redirect = l;
   }

   Label getRedirect() {
      return this._redirect;
   }

   void dump(Compiler c) {
      c.print("L" + this._id);
   }
}
