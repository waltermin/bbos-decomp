package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class DataBytes extends CodfileItem {
   private CodfileVectorHash _unicodeLiterals = new CodfileVectorHash(5);
   private CodfileVector _bytes = new CodfileVector();
   private CodfileVectorHash _identifiers = new CodfileVectorHash(131);
   private CodfileVectorHash _literals = new CodfileVectorHash(131);
   private Identifier _nullIdentifier = new Identifier();

   @Override
   public final void write(StructuredOutputStream out) {
      this.setOffset(out);
      this._unicodeLiterals.write(out, true);
      this._bytes.write(out, true);
      this._identifiers.write(out, true);
      this._literals.write(out, true);
      this.setExtent(out);
   }

   public final Identifier getNullIdentifier() {
      return this._nullIdentifier;
   }

   public final Identifier getIdentifier(String str) {
      if (str == null) {
         str = this._nullIdentifier.getString();
      }

      CodfileVectorHash vectorHash = this._identifiers;
      Identifier id = (Identifier)vectorHash.get(str);
      if (id == null) {
         id = new Identifier(str);
         vectorHash.put(str, id);
      }

      return id;
   }

   public final Bytes getBytes(byte[] bytes, int arrayType, boolean needsHeader) {
      Bytes b = null;
      CodfileVector vector = this._bytes;
      int num = vector.size();

      for (int i = 0; i < num; i++) {
         b = (Bytes)vector.elementAt(i);
         boolean hasHeader = b.getNeedsHeader();
         int aType = needsHeader && hasHeader ? arrayType : -1;
         if (b.matches(bytes, aType)) {
            if (needsHeader && !hasHeader) {
               b.setNeedsHeader();
               b.setArrayType(arrayType);
            }

            return b;
         }
      }

      b = new Bytes(bytes, arrayType, needsHeader);
      vector.addElementOrdered(b);
      return b;
   }

   public final Literal getLiteral(String str, boolean unicode, boolean needsHeader) {
      if (str == null) {
         str = this._nullIdentifier.getString();
      }

      CodfileVectorHash vectorHash = unicode ? this._unicodeLiterals : this._literals;
      Literal lit = (Literal)vectorHash.get(str);
      if (lit == null) {
         lit = new Literal(str, unicode, needsHeader);
         vectorHash.put(str, lit);
      } else if (needsHeader) {
         lit.setNeedsHeader();
      }

      return lit;
   }
}
