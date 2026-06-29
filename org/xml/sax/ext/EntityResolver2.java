package org.xml.sax.ext;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public interface EntityResolver2 extends EntityResolver {
   InputSource getExternalSubset(String var1, String var2);

   InputSource resolveEntity(String var1, String var2, String var3, String var4);
}
