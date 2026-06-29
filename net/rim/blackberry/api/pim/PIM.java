package net.rim.blackberry.api.pim;

import java.io.InputStream;
import java.io.OutputStream;

public class PIM {
   static String VCARD_VERSION_2_1 = "VCARD/2.1";
   static String VCARD_VERSION_3_0 = "VCARD/3.0";
   static String DEFAULT_VCARD_VERSION = VCARD_VERSION_3_0;
   static String[] VCARD_VERSIONS = new Object[]{VCARD_VERSION_2_1, VCARD_VERSION_3_0};
   static String DEFAULT_VCAL_VERSION = "VCALENDAR/1.0";
   static String[] VCAL_VERSIONS = new Object[]{DEFAULT_VCAL_VERSION, "VCALENDAR/2.0"};
   public static final int READ_ONLY;
   public static final int WRITE_ONLY;
   public static final int READ_WRITE;
   public static final int CONTACT_LIST;
   public static final int EVENT_LIST;
   public static final int TODO_LIST;

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
