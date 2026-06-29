package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.exec.CharacterHelper;
import net.rim.tools.compiler.io.StructuredInputStream;

public final class ConstantPoolUTF8 extends ConstantPoolEntry {
   private byte[] _bytes;
   private int _start;
   private int _length;
   private String _string;

   public ConstantPoolUTF8(int tag, StructuredInputStream in) {
      super(tag);
      this._length = in.readUnsignedShort();
      this._start = in.getOffset();
      this._bytes = in.getBytes();
      in.skipBytes(this._length);
   }

   @Override
   public final void resolve(ConstantPool constantPool) {
      if (this._string == null) {
         this._string = CharacterHelper.utf8ToString(this._bytes, this._start, this._length);
         this._bytes = null;
      }
   }

   public final String getString() {
      return this._string;
   }
}
