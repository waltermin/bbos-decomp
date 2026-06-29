package javax.microedition.io;

import java.io.DataInputStream;
import java.io.InputStream;

public interface InputConnection extends Connection {
   InputStream openInputStream();

   DataInputStream openDataInputStream();
}
