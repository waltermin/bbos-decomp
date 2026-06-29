package simulationservicebook;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

class InsertInternetServiceBook$3 implements InsertInternetServiceBook$ApplicationDataProvider {
   private final InsertInternetServiceBook this$0;

   InsertInternetServiceBook$3(InsertInternetServiceBook _1) {
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public byte[] get() {
      ByteArrayOutputStream bout = (ByteArrayOutputStream)(new Object());
      DataOutputStream dout = (DataOutputStream)(new Object(bout));

      try {
         dout.writeShort(4);
         dout.writeByte(1);
         dout.writeInt(1024);
         dout.writeShort(4);
         dout.writeByte(2);
         dout.writeInt(120000);
         dout.writeShort(4);
         dout.writeByte(3);
         dout.writeInt(50);
         dout.writeShort(4);
         dout.writeByte(4);
         dout.writeInt(50);
         bout.close();
      } catch (Throwable var5) {
         System.out.println(e);
         return bout.toByteArray();
      }

      return bout.toByteArray();
   }
}
