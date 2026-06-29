package net.rim.tid.im.conv.europe.repository;

import java.io.DataInputStream;
import java.util.Vector;

public final class Reader$SubstitutionTable {
   Vector substitutions;

   public final void read(DataInputStream aDis) {
      this.substitutions = (Vector)(new Object());
      byte size = 0;
      int nLength = 2;
      StringBuffer sb = (StringBuffer)(new Object());

      do {
         size = aDis.readByte();
         int toRead = size;
         if (size < 0) {
            toRead = size & 127;
         }

         for (int i = 0; i < toRead; i++) {
            sb.setLength(0);

            for (int j = 0; j < nLength; j++) {
               sb.append(aDis.readChar());
            }

            this.substitutions.addElement(sb.toString());
         }

         nLength++;
      } while (size < 0);
   }

   public final int getSubstIndex(String subst) {
      return this.substitutions.indexOf(subst);
   }

   public final String getSubstitution(int index, int shift) {
      return (String)this.substitutions.elementAt(index - shift);
   }
}
