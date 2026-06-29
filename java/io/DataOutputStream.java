package java.io;

import net.rim.device.api.util.StringUtilities;

public class DataOutputStream extends OutputStream implements DataOutput {
   protected OutputStream out;

   public DataOutputStream(OutputStream out) {
      this.out = out;
   }

   @Override
   public void write(int b) {
      this.out.write(b);
   }

   @Override
   public void write(byte[] b, int off, int len) {
      this.out.write(b, off, len);
   }

   @Override
   public void flush() {
      this.out.flush();
   }

   @Override
   public void close() {
      this.out.close();
   }

   @Override
   public final void writeBoolean(boolean v) {
      this.write(v ? 1 : 0);
   }

   @Override
   public final void writeByte(int v) {
      this.write(v);
   }

   @Override
   public final void writeShort(int v) {
      this.write(v >>> 8 & 0xFF);
      this.write(v >>> 0 & 0xFF);
   }

   @Override
   public final void writeChar(int v) {
      this.write(v >>> 8 & 0xFF);
      this.write(v >>> 0 & 0xFF);
   }

   @Override
   public final void writeInt(int v) {
      this.write(v >>> 24 & 0xFF);
      this.write(v >>> 16 & 0xFF);
      this.write(v >>> 8 & 0xFF);
      this.write(v >>> 0 & 0xFF);
   }

   @Override
   public final void writeLong(long v) {
      this.write((int)(v >>> 56) & 0xFF);
      this.write((int)(v >>> 48) & 0xFF);
      this.write((int)(v >>> 40) & 0xFF);
      this.write((int)(v >>> 32) & 0xFF);
      this.write((int)(v >>> 24) & 0xFF);
      this.write((int)(v >>> 16) & 0xFF);
      this.write((int)(v >>> 8) & 0xFF);
      this.write((int)(v >>> 0) & 0xFF);
   }

   @Override
   public final void writeFloat(float v) {
      this.writeInt(Float.floatToIntBits(v));
   }

   @Override
   public final void writeDouble(double v) {
      this.writeLong(Double.doubleToLongBits(v));
   }

   @Override
   public final void writeChars(String s) {
      int len = s.length();

      for (int i = 0; i < len; i++) {
         int v = s.charAt(i);
         this.write(v >>> 8 & 0xFF);
         this.write(v >>> 0 & 0xFF);
      }
   }

   @Override
   public final void writeUTF(String str) {
      StringUtilities.writeUTF(str, this);
   }
}
