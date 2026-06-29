package net.rim.ecmascript.compiler;

class Block {
   protected Label _label;
   protected int _size;
   protected int _offset;
   protected ByteCode _code;
   private int _withNesting;
   private boolean _isReachable;

   Block(Block b) {
      this._label = b._label;
      this._size = b._size;
      this._offset = b._offset;
      this._code = b._code;
      this._withNesting = b._withNesting;
      this._isReachable = b._isReachable;
   }

   Block(Label l) {
      this._label = l;
      this._label.setBlock(this);
   }

   Label getLabel() {
      return this._label;
   }

   void attachCode(ByteCode code) {
      this._code = code;
   }

   void setWithNesting(int withNesting) {
      this._withNesting = withNesting;
   }

   boolean isEmpty() {
      return this._code.isEmpty();
   }

   void setReachable(boolean isReachable) {
      this._isReachable = isReachable;
   }

   boolean getReachable() {
      return this._isReachable;
   }

   void optimizeLocals(Function f) {
      if (this._withNesting == 0) {
         this._code.optimizeLocals(f);
      }
   }

   void postOptimize(Function f) {
      this._code.postOptimize(f);
      if (this.needsScope()) {
         f.setNeedsScope();
      }
   }

   int generateCode(Function f, byte[] buff, int index) {
      return this._code.generate(f, buff, index);
   }

   int generate(Function f, Label next, byte[] buff, int index) {
      index = this.generateCode(f, buff, index);
      return this.generateJumps(next, buff, index);
   }

   void setSize(int size) {
      this._size = size;
   }

   int setOffset(int offset) {
      this._offset = offset;
      return this._size + offset;
   }

   int getOffset() {
      return this._offset;
   }

   boolean shortenBranch() {
      return false;
   }

   boolean byteReachable(int insOffset, Label target) {
      int var10001 = insOffset + this._offset;
      int diff = target.getBlock()._offset - (var10001 + 1);
      return diff < -128 ? false : diff <= 127;
   }

   int getNumTargets() {
      throw null;
   }

   Label getTarget(int _1) {
      throw null;
   }

   void setTarget(int _1, Label _2) {
      throw null;
   }

   boolean redirectLabels() {
      throw null;
   }

   int generateJumps(Label _1, byte[] _2, int _3) {
      throw null;
   }

   void dump(Function f, Compiler c, int indent) {
      c.println("", indent - 4);
      this._label.dump(c);
      c.print(": ");
      if (this._withNesting != 0) {
         c.print(" - with");
      }

      if (this._code != null) {
         this._code.dump(f, c, indent);
      }
   }

   boolean needsScope() {
      return false;
   }
}
