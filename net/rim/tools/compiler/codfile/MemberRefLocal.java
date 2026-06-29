package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class MemberRefLocal extends MemberRef {
   public MemberRefLocal(ClassDef classDef, Member member) {
      super(classDef, member, member.getName(), member.getTypeList());
   }

   @Override
   public final void write(StructuredOutputStream out) {
      this.setOffset(out);
      super._classDef.writeRelativeOrdinal(out);
      super._member.writeOrdinal(out);
      this.setExtent(out);
   }
}
