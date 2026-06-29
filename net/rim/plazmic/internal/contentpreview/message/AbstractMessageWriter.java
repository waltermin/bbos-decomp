package net.rim.plazmic.internal.contentpreview.message;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.OutputStream;
import net.rim.plazmic.internal.contentpreview.MishandleException;

public class AbstractMessageWriter {
   private final DataOutputStream _dout;
   public static final String rcsid = "$Id: //depot/dev/pbaldwin/advancedgraphics/src/net/rim/plazmic/internal/contentpreview/playback/AbstractMessageWriter.java#1 $";

   private final DataOutputStream getDataOutputStream() {
      return this._dout;
   }

   protected AbstractMessageWriter(OutputStream out) {
      this._dout = new DataOutputStream(out);
   }

   protected byte[] getHeader() {
      throw null;
   }

   private final void writeHeader() {
      this._dout.write(this.getHeader());
   }

   protected final void writeMessage(byte message) {
      this.writeMessage(message, null);
   }

   protected final void writeMessage(byte message, float param) {
      this.writeMessage(message, new Float(param));
   }

   protected final void writeMessage(byte message, int param) {
      this.writeMessage(message, new Integer(param));
   }

   protected final void writeMessage(byte message, long param) {
      this.writeMessage(message, new Long(param));
   }

   protected final void writeMessage(byte message, Object param1) {
      this.writeMessage(message, param1, null);
   }

   protected final void writeMessage(byte message, Object param1, Object param2) {
      this.writeMessage(message, param1, param2, null);
   }

   protected final void writeMessage(byte message, Object param1, Object param2, Object param3) {
      this.writeMessage(message, param1, param2, param3, null);
   }

   protected final void writeMessage(byte message, Object param1, Object param2, Object param3, Object param4) {
      this.writeMessage(message, param1, param2, param3, param4, null);
   }

   protected final void writeMessage(byte message, Object param1, Object param2, Object param3, Object param4, Object param5) {
      this.writeMessage(message, param1, param2, param3, param4, param5, null);
   }

   protected final void writeMessage(byte message, Object param1, Object param2, Object param3, Object param4, Object param5, Object param6) {
      this.writeMessage(message, param1, param2, param3, param4, param5, param6, null);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected final void writeMessage(byte message, Object param1, Object param2, Object param3, Object param4, Object param5, Object param6, Object param7) throws MishandleException {
      try {
         DataOutputStream dout = this.getDataOutputStream();
         this.writeHeader();
         dout.writeByte(message);
         if (param1 != null) {
            this.writeObject(param1);
            if (param2 != null) {
               this.writeObject(param2);
               if (param3 != null) {
                  this.writeObject(param3);
                  if (param4 != null) {
                     this.writeObject(param4);
                     if (param5 != null) {
                        this.writeObject(param5);
                        if (param6 != null) {
                           this.writeObject(param6);
                           if (param7 != null) {
                              this.writeObject(param7);
                           }
                        }
                     }
                  }
               }
            }
         }

         dout.flush();
      } catch (Throwable var11) {
         throw new MishandleException(e);
      }
   }

   private void writeString(String s) {
      DataOutputStream dout = this.getDataOutputStream();
      dout.writeInt(s.length());
      dout.writeChars(s);
   }

   private void writeStringArray(String[] sa) {
      DataOutputStream dout = this.getDataOutputStream();
      dout.writeInt(sa.length);

      for (int i = 0; i < sa.length; i++) {
         this.writeString(sa[i]);
      }
   }

   public void writeObject(Object obj) {
      DataOutput dout = this.getDataOutputStream();
      if (obj instanceof Boolean) {
         dout.writeBoolean((Boolean)obj);
      } else if (obj instanceof Byte) {
         dout.writeByte((Byte)obj);
      } else if (obj instanceof Short) {
         dout.writeShort((Short)obj);
      } else if (obj instanceof Integer) {
         dout.writeInt((Integer)obj);
      } else if (obj instanceof Long) {
         dout.writeLong((Long)obj);
      } else if (obj instanceof Float) {
         dout.writeFloat((Float)obj);
      } else if (obj instanceof Double) {
         dout.writeDouble((Double)obj);
      } else if (obj instanceof String) {
         this.writeString((String)obj);
      } else if (obj instanceof String[]) {
         this.writeStringArray((String[])obj);
      } else {
         throw new IllegalArgumentException("object type not supported: " + obj.toString());
      }
   }
}
