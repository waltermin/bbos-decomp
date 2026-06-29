package net.rim.device.api.util;

import java.util.Enumeration;

public class StringTokenizer implements Enumeration {
   private int currentPosition;
   private int newPosition;
   private int maxPosition;
   private String str;
   private String delimiters;
   private boolean retDelims;
   private boolean delimsChanged;
   private char maxDelimChar;
   private char[] oneChar = new char[]{' ', '3'};

   public boolean hasMoreTokens() {
      this.newPosition = this.skipDelimiters(this.currentPosition);
      return this.newPosition < this.maxPosition;
   }

   public String nextToken() {
      this.currentPosition = this.newPosition >= 0 && !this.delimsChanged ? this.newPosition : this.skipDelimiters(this.currentPosition);
      this.delimsChanged = false;
      this.newPosition = -1;
      if (this.currentPosition >= this.maxPosition) {
         return null;
      }

      int start = this.currentPosition;
      this.currentPosition = this.scanToken(this.currentPosition);
      return this.str.substring(start, this.currentPosition);
   }

   public String nextToken(String delim) {
      this.delimiters = delim;
      this.delimsChanged = true;
      this.setMaxDelimChar();
      return this.nextToken();
   }

   public int countTokens() {
      int count = 0;

      for (int currpos = this.currentPosition; currpos < this.maxPosition; count++) {
         currpos = this.skipDelimiters(currpos);
         if (currpos >= this.maxPosition) {
            return count;
         }

         currpos = this.scanToken(currpos);
      }

      return count;
   }

   @Override
   public Object nextElement() {
      return this.nextToken();
   }

   @Override
   public boolean hasMoreElements() {
      return this.hasMoreTokens();
   }

   private int skipDelimiters(int startPos) {
      int position;
      for (position = startPos; !this.retDelims && position < this.maxPosition; position++) {
         char c = this.str.charAt(position);
         if (c > this.maxDelimChar) {
            break;
         }

         if (this.delimiters.indexOf(c) < 0) {
            return position;
         }
      }

      return position;
   }

   private int scanToken(int startPos) {
      int position;
      for (position = startPos; position < this.maxPosition; position++) {
         char c = this.str.charAt(position);
         if (c <= this.maxDelimChar && this.delimiters.indexOf(c) >= 0) {
            break;
         }
      }

      if (this.retDelims && startPos == position) {
         char c = this.str.charAt(position);
         if (c <= this.maxDelimChar && this.delimiters.indexOf(c) >= 0) {
            position++;
         }
      }

      return position;
   }

   private void setMaxDelimChar() {
      if (this.delimiters == null) {
         this.maxDelimChar = 0;
      } else {
         char m = 0;
         int delimitersLength = this.delimiters.length();

         for (int i = 0; i < delimitersLength; i++) {
            char c = this.delimiters.charAt(i);
            if (m < c) {
               m = c;
            }
         }

         this.maxDelimChar = m;
      }
   }

   public StringTokenizer(String str, String delim, boolean returnDelims) {
      this.initializer(str, delim, returnDelims);
   }

   public StringTokenizer(String str, char delim) {
      this.oneChar[0] = delim;
      this.initializer(str, new String(this.oneChar), false);
   }

   private void initializer(String aString, String delim, boolean returnDelims) {
      if (aString != null && delim != null) {
         this.currentPosition = 0;
         this.newPosition = -1;
         this.str = aString;
         this.maxPosition = this.str.length();
         this.delimiters = delim;
         this.retDelims = returnDelims;
         this.setMaxDelimChar();
      } else {
         throw new NullPointerException();
      }
   }

   public StringTokenizer(String str) {
      this.initializer(str, " \t\n\r\f", false);
   }

   public StringTokenizer(String str, String delim) {
      this.initializer(str, delim, false);
   }
}
