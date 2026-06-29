package net.rim.device.api.i18n;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.IntHashtable;

class Locale$Locales {
   public Locale[] available = new Locale[0];
   public Locale[] inputAvailable = new Locale[0];
   public IntHashtable used = new IntHashtable();
   public Locale current;
   public Locale currentInput;
   public StringBuffer buffer = new StringBuffer(10);
   public Comparator comparator = new Locale$Locales$1(this);
}
