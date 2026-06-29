package javax.microedition.pim;

import java.io.InputStream;
import java.io.OutputStream;
import net.rim.blackberry.api.pdap.PIMImpl;

public class PIM {
   public static final int READ_ONLY = 1;
   public static final int WRITE_ONLY = 2;
   public static final int READ_WRITE = 3;
   public static final int CONTACT_LIST = 1;
   public static final int EVENT_LIST = 2;
   public static final int TODO_LIST = 3;

   protected PIM() {
   }

   public static PIM getInstance() {
      return PIMImpl.getInstance();
   }

   public PIMList openPIMList(int _1, int _2) {
      throw null;
   }

   public PIMList openPIMList(int _1, int _2, String _3) {
      throw null;
   }

   public String[] listPIMLists(int _1) {
      throw null;
   }

   public PIMItem[] fromSerialFormat(InputStream _1, String _2) {
      throw null;
   }

   public void toSerialFormat(PIMItem _1, OutputStream _2, String _3, String _4) {
      throw null;
   }

   public String[] supportedSerialFormats(int _1) {
      throw null;
   }
}
