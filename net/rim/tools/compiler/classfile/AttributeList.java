package net.rim.tools.compiler.classfile;

import java.util.Hashtable;
import net.rim.tools.compiler.exec.CharacterHelper;
import net.rim.tools.compiler.io.StructuredInputStream;

public final class AttributeList {
   private Hashtable _table;
   static final int LIST_CLASSFILE = 1;
   static final int LIST_FIELD = 2;
   static final int LIST_METHOD = 3;
   static final int LIST_CODE = 4;
   public static String NAME_CODE = CharacterHelper.intern("Code");
   public static String NAME_DEPRECATED = CharacterHelper.intern("Deprecated");
   public static String NAME_EXCEPTIONS = CharacterHelper.intern("Exceptions");
   public static String NAME_INNERCLASSES = CharacterHelper.intern("InnerClasses");
   public static String NAME_LINENUMBERTABLE = CharacterHelper.intern("LineNumberTable");
   public static String NAME_LOCALVARIABLETABLE = CharacterHelper.intern("LocalVariableTable");
   public static String NAME_SOURCEFILE = CharacterHelper.intern("SourceFile");
   public static String NAME_STACKMAP = CharacterHelper.intern("StackMap");
   public static String NAME_SYNTHETIC = CharacterHelper.intern("Synthetic");

   public AttributeList(StructuredInputStream in, ConstantPool constantPool, int list, boolean shallow) {
      int numAtts = in.readUnsignedShort();
      if (numAtts > 0) {
         this._table = (Hashtable)(new Object(numAtts * 2));

         for (int i = 0; i < numAtts; i++) {
            Attribute attr = Attribute.read(in, constantPool, list, shallow);
            if (this._table.put(attr.getName(), attr) != null) {
               throw new Object(((StringBuffer)(new Object("duplicate "))).append(attr.getName()).append(" attribute").toString());
            }
         }
      }
   }

   public final Attribute getAttribute(String attributeName) {
      return (Attribute)(this._table == null ? null : this._table.get(attributeName));
   }
}
