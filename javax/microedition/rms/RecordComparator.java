package javax.microedition.rms;

public interface RecordComparator {
   int EQUIVALENT;
   int FOLLOWS;
   int PRECEDES;

   int compare(byte[] var1, byte[] var2);
}
