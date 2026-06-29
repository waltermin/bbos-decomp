package net.rim.tools.compiler.codfile;

import java.io.OutputStream;
import net.rim.tools.compiler.io.StructuredOutputStream;
import net.rim.tools.compiler.io.StructuredOutputStreamCounter;
import net.rim.tools.compiler.vm.Constants;

public final class Codfile implements Constants {
   protected int _flashId;
   protected int _timeStamp;
   protected int _userVersion;
   protected int _maxTypeListSize;
   protected int _version;
   protected int _flags;
   protected CodfileVector _routines;
   protected DataSection _dataSection;
   protected int _codeSize;
   protected int _dataSize;

   private final void init() {
      this._flashId = -16162;
      this._routines = new CodfileVector();
      this._dataSection = new DataSection(this);
   }

   public Codfile(boolean target36) {
      this.init();
      this._version = 78;
   }

   private Codfile() {
   }

   public final int write(StructuredOutputStream out) {
      out.resetOffset();
      out.writeInt(this._flashId);
      out.writeInt(0);
      out.writeInt(0);
      out.writeInt(this._timeStamp);
      out.writeInt(this._userVersion);
      out.writeInt(0);
      out.writeShort(this._maxTypeListSize);
      out.writeShort(-1);
      out.writeInt(-1);
      out.writeInt(-1);
      out.writeShort(this._version);
      out.writeShort(this._codeSize);
      out.writeShort(this._dataSize);
      out.writeShort(this._flags);
      int totalSize = out.getOffset();
      out.resetOffset();
      out.setWritingCode(true);
      this._dataSection.assignClassRefOrdinals();
      this._dataSection.harvestRoutines();
      this._routines.write(out, true);
      out.writeSlack(4);
      this._routines.setExtent(out);
      this._codeSize = this._routines.getExtent();
      totalSize += out.getOffset();
      out.resetOffset();
      out.setWritingCode(false);
      this._dataSection.write(out);
      this._dataSize = this._dataSection.getExtent();
      totalSize += out.getOffset();
      out.close();
      return totalSize;
   }

   public final void write(OutputStream raw_out) {
      StructuredOutputStream out = null;
      out = new StructuredOutputStream(raw_out, true);
      this.write(out);
   }

   public final int count(String name, StringBuffer errorBuffer) {
      StructuredOutputStreamCounter count = new StructuredOutputStreamCounter(this._dataSection);
      int totalSize = this.write(count);
      if (this._codeSize > 65000 || this._dataSize > 65000) {
         boolean conjunction = false;
         errorBuffer.append("output file: ");
         errorBuffer.append(name);
         errorBuffer.append(".cod ");
         if (this._codeSize > 65000) {
            errorBuffer.append("code section too large: ");
            errorBuffer.append(Integer.toString(this._codeSize));
            errorBuffer.append(" bytes");
            conjunction = true;
         }

         if (this._dataSize > 65000) {
            if (conjunction) {
               conjunction = false;
               errorBuffer.append(", ");
            }

            errorBuffer.append("data section too large: ");
            errorBuffer.append(Integer.toString(this._dataSize));
            errorBuffer.append(" bytes.\n");
         }

         if (conjunction) {
            errorBuffer.append(".\n");
         }
      }

      return totalSize;
   }

   public final void setTimeStamp(int timeStamp) {
      this._timeStamp = timeStamp;
   }

   public final void setMaxTypeListSize(int maxTypeListSize) {
      this._maxTypeListSize = maxTypeListSize;
   }

   public final void setFlags(int flags) {
      this._flags |= flags;
      this._dataSection.setAttributes(flags);
   }

   public final DataSection getDataSection() {
      return this._dataSection;
   }

   public final CodfileVector getRoutines() {
      return this._routines;
   }
}
