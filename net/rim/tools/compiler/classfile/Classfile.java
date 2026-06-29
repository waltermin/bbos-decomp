package net.rim.tools.compiler.classfile;

import java.io.IOException;
import net.rim.tools.compiler.io.StructuredInputStream;
import net.rim.tools.compiler.vm.Constants;

public final class Classfile implements Constants {
   private int _nMajorVersion;
   private ConstantPool _constantPool;
   private int _accessFlags;
   private int _thisClass;
   private int _superClass;
   private int[] _interfaces;
   private ClassfileField[] _fields;
   private ClassfileMethod[] _methods;
   private AttributeList _attributes;

   public Classfile(byte[] inBytes, boolean shallow) throws IOException {
      StructuredInputStream in = new StructuredInputStream(inBytes, false);
      if (in.readByte() == -54 && in.readByte() == -2 && in.readByte() == -70 && in.readByte() == -66) {
         in.readUnsignedShort();
         this._nMajorVersion = in.readUnsignedShort();
         if (this._nMajorVersion >= 45 && this._nMajorVersion <= 49) {
            this._constantPool = new ConstantPool(in, shallow);
            this._accessFlags = in.readUnsignedShort();
            this._thisClass = in.readUnsignedShort();
            this._superClass = in.readUnsignedShort();
            int n = in.readUnsignedShort();
            if (n > 0) {
               this._interfaces = new int[n];

               for (int i = 0; i < n; i++) {
                  this._interfaces[i] = in.readUnsignedShort();
               }
            }

            n = in.readUnsignedShort();
            if (n > 0) {
               this._fields = new ClassfileField[n];

               for (int i = 0; i < n; i++) {
                  this._fields[i] = new ClassfileField(in, this._constantPool, shallow);
               }
            }

            n = in.readUnsignedShort();
            if (n > 0) {
               this._methods = new ClassfileMethod[n];

               for (int i = 0; i < n; i++) {
                  this._methods[i] = new ClassfileMethod(in, this._constantPool, shallow);
               }
            }

            this._attributes = new AttributeList(in, this._constantPool, 1, shallow);
            if (in.read() != -1) {
               throw new IOException("Extra bytes in class file");
            }

            in.close();
         } else {
            throw new IOException("Incorrect classfile version");
         }
      } else {
         throw new IOException("Not a classfile");
      }
   }

   public final int getAccessFlags() {
      return this._accessFlags;
   }

   public final String getFullClassName() throws IOException {
      String name = null;
      if (this._thisClass != 0) {
         name = this._constantPool.getClassName(this._thisClass);
         if (name.charAt(0) == '[') {
            throw new IOException("invalid class name: " + name);
         }
      }

      return name;
   }

   public final String getFullBaseClassName() throws IOException {
      String name = null;
      if (this._superClass != 0) {
         name = this._constantPool.getClassName(this._superClass);
         if (name.charAt(0) == '[') {
            throw new IOException("invalid class name: " + name);
         }
      }

      return name;
   }

   public final ConstantPool getConstantPool() {
      return this._constantPool;
   }

   public final int getNumInterfaces() {
      return this._interfaces == null ? 0 : this._interfaces.length;
   }

   public final String getFullInterfaceName(int index) throws IOException {
      String name = this._constantPool.getClassName(this._interfaces[index]);
      if (name.charAt(0) == '[') {
         throw new IOException("invalid interface name: " + name);
      } else {
         return name;
      }
   }

   public final int getNumFields() {
      return this._fields == null ? 0 : this._fields.length;
   }

   public final ClassfileField getField(int index) {
      return this._fields[index];
   }

   public final int getNumMethods() {
      return this._methods == null ? 0 : this._methods.length;
   }

   public final ClassfileMethod getMethod(int index) {
      return this._methods[index];
   }

   public final boolean hasAttribute(String attributeName) {
      return this.getAttribute(attributeName) != null;
   }

   public final Attribute getAttribute(String attributeName) {
      return this._attributes.getAttribute(attributeName);
   }
}
