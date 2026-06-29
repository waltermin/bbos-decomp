package net.rim.ecmascript.compiler;

import java.util.Vector;

class GenerateCode {
   private Function _f;
   private Vector _blocks;

   GenerateCode(Function f) {
      this._f = f;
      this._blocks = this._f.getBlocks();
   }

   boolean shortenBranches() {
      boolean change = false;

      for (int i = this._blocks.size() - 1; i >= 0; i--) {
         if (((Block)this._blocks.elementAt(i)).shortenBranch()) {
            change = true;
         }
      }

      return change;
   }

   void generate() {
      int size = this._blocks.size();
      int sizeMinusOne = size - 1;

      int offset;
      do {
         for (int i = 0; i < sizeMinusOne; i++) {
            Block b = (Block)this._blocks.elementAt(i);
            Block next = (Block)this._blocks.elementAt(i + 1);
            b.setSize(b.generate(null, next.getLabel(), null, 0));
         }

         if (size != 0) {
            Block b = (Block)this._blocks.elementAt(sizeMinusOne);
            b.setSize(b.generate(null, null, null, 0));
         }

         offset = 0;

         for (int i = 0; i < size; i++) {
            offset = ((Block)this._blocks.elementAt(i)).setOffset(offset);
         }
      } while (this.shortenBranches());

      byte[] code = new byte[offset];
      this._f.setCode(code);
      int index = 0;

      for (int i = 0; i < sizeMinusOne; i++) {
         Block b = (Block)this._blocks.elementAt(i);
         Block next = (Block)this._blocks.elementAt(i + 1);
         index = b.generate(this._f, next.getLabel(), code, index);
      }

      if (size != 0) {
         Block b = (Block)this._blocks.elementAt(sizeMinusOne);
         index = b.generate(this._f, null, code, index);
      }
   }

   static int genShort(int offset, byte[] buffer, int index) {
      if (buffer != null) {
         buffer[index + 0] = (byte)(offset >> 8);
         buffer[index + 1] = (byte)(offset >> 0);
      }

      return index + 2;
   }

   static int genInt(int offset, byte[] buffer, int index) {
      if (buffer != null) {
         buffer[index + 0] = (byte)(offset >> 24);
         buffer[index + 1] = (byte)(offset >> 16);
         buffer[index + 2] = (byte)(offset >> 8);
         buffer[index + 3] = (byte)(offset >> 0);
      }

      return index + 4;
   }

   static int genByte(int op, byte[] buffer, int index) {
      if (buffer != null) {
         buffer[index] = (byte)op;
      }

      return index + 1;
   }

   static int genLabelOp(int op, boolean isShort, Label label, byte[] buffer, int index) {
      if (isShort) {
         index = genByte(op + 1, buffer, index);
         return genByte(label.getBlock().getOffset() - index, buffer, index);
      } else {
         index = genByte(op, buffer, index);
         return genShort(label.getBlock().getOffset(), buffer, index);
      }
   }

   static int genByteOp(int op, int operand, byte[] buffer, int index) {
      index = genByte(op, buffer, index);
      return genByte(operand, buffer, index);
   }

   static int genShortOp(int op, int operand, byte[] buffer, int index) {
      index = genByte(op, buffer, index);
      return genShort(operand, buffer, index);
   }

   static int genIntOp(int op, int operand, byte[] buffer, int index) {
      index = genByte(op, buffer, index);
      return genInt(operand, buffer, index);
   }

   static int genLabelOp(Compiler c, int op, boolean isShort, int operand, byte[] buffer, int index) {
      if (isShort) {
         index = genByte(op + 1, buffer, index);
         return genByte(c.getLabelFromId(operand).getBlock().getOffset() - index, buffer, index);
      } else {
         index = genByte(op, buffer, index);
         return genShort(c.getLabelFromId(operand).getBlock().getOffset(), buffer, index);
      }
   }

   static int genByteOrShortOp(int op, int operand, byte[] buffer, int index) {
      return operand >= 0 && operand <= 127 ? genByteOp(op, operand, buffer, index) : genShortOp(op + 1, operand, buffer, index);
   }
}
