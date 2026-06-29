package javax.microedition.media.control;

import javax.microedition.media.Control;

public interface MetaDataControl extends Control {
   String AUTHOR_KEY;
   String COPYRIGHT_KEY;
   String DATE_KEY;
   String TITLE_KEY;

   String[] getKeys();

   String getKeyValue(String var1);
}
