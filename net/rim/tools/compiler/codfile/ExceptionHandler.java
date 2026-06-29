package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class ExceptionHandler extends CodfileItemRelative {
   private CodfileLabel _start;
   private CodfileLabel _end;
   private CodfileLabel _handler;
   private ClassDef _classDef;
   public static final int EXCEPTION_TABLE_END;

   public ExceptionHandler(CodfileLabel start, CodfileLabel end, CodfileLabel handler, ClassDef classDef) {
      this._start = start;
      this._end = end;
      this._handler = handler;
      this._classDef = classDef;
   }

   @Override
   public final void writeRelative(StructuredOutputStream out, int relative) {
      this.setOffset(out);
      out.writeShort(this._start.getOffset() + relative);
      out.writeShort(this._end.getOffset() + relative);
      out.writeShort(this._handler.getOffset() + relative);
      this._classDef.writeAbsoluteClassDef(out);
      this.setExtent(out);
   }

   public static final int getSize() {
      return 6;
   }
}
