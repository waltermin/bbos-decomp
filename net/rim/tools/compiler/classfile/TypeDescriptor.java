package net.rim.tools.compiler.classfile;

public final class TypeDescriptor {
   private String _descriptor;
   private int _length;
   private int _offset;

   public TypeDescriptor(String descriptor) {
      this._descriptor = descriptor.replace('/', '.');
      this._length = descriptor.length();
      this._offset = 0;
   }

   public final String getString() {
      return this._descriptor;
   }

   public final boolean matches(char match) {
      if (this._offset < this._length && this._descriptor.charAt(this._offset) == match) {
         this._offset++;
         return true;
      } else {
         return false;
      }
   }

   public final int getCharsRemaining() {
      return this._offset < this._length ? this._length - this._offset : 0;
   }

   public final char getNextChar() {
      return this._offset < this._length ? this._descriptor.charAt(this._offset++) : '\u0000';
   }

   public final String getClassName() {
      int index = this._descriptor.indexOf(59, this._offset);
      String className = this._descriptor.substring(this._offset, index);
      this._offset = index + 1;
      return className;
   }
}
