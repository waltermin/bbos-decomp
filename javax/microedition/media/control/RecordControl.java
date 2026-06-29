package javax.microedition.media.control;

import java.io.OutputStream;
import javax.microedition.media.Control;

public interface RecordControl extends Control {
   void setRecordStream(OutputStream var1);

   void setRecordLocation(String var1);

   String getContentType();

   void startRecord();

   void stopRecord();

   void commit();

   int setRecordSizeLimit(int var1);

   void reset();
}
