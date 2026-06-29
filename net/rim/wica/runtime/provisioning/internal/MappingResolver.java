package net.rim.wica.runtime.provisioning.internal;

import java.util.Vector;
import net.rim.wica.runtime.provisioning.internal.elements.AbstractElement;
import net.rim.wica.runtime.provisioning.internal.elements.WicletElement;
import net.rim.wica.runtime.util.Util;

public class MappingResolver {
   private String _mapping;
   private String[] _mappingTokens;
   private WicletElement _we;
   private int _rootReferenceStartsWithGlobalElement = -1;

   public MappingResolver(WicletElement we, String mapping) {
      this._mapping = mapping;
      this._we = we;
      this._mappingTokens = Util.split(mapping, '.');
   }

   public boolean isMappedToDataElement() {
      return this.mappingTokenCount() == 1 && !this.isMappedToGlobalElement();
   }

   public int mappingTokenCount() {
      return this._mappingTokens.length;
   }

   public boolean isMappedToGlobalElement() {
      return this.mappingTokenCount() == 1 && this.rootReferenceStartsWithGlobalElement();
   }

   public boolean isMappedToFieldElement() {
      return this.mappingTokenCount() > 1;
   }

   public boolean rootReferenceStartsWithGlobalElement() {
      if (this._rootReferenceStartsWithGlobalElement == -1) {
         String root = this._mappingTokens[0];
         this._rootReferenceStartsWithGlobalElement = this._we.getGlobalElement(root) != null ? 1 : 0;
      }

      return this._rootReferenceStartsWithGlobalElement == 1;
   }

   public AbstractElement resolveMapping() {
      AbstractElement mappingTarget = null;
      if (this.isMappedToDataElement()) {
         return this._we.getDataElement(this._mapping);
      }

      if (this.isMappedToGlobalElement()) {
         return this._we.getGlobalElement(this._mapping);
      }

      if (this.isMappedToFieldElement()) {
         Vector path = this._we.resolvePath(this._mapping);
         int pathSize = path.size();
         boolean globalFieldMappingResolvedProperly = this.rootReferenceStartsWithGlobalElement() && pathSize + 1 == this.mappingTokenCount();
         boolean dataFieldMappingResolvedProperly = pathSize == this.mappingTokenCount();
         if (globalFieldMappingResolvedProperly || dataFieldMappingResolvedProperly) {
            mappingTarget = (AbstractElement)path.lastElement();
         }
      }

      return mappingTarget;
   }

   public Vector resolveFieldMapping() {
      return this.isMappedToFieldElement() ? this._we.resolvePath(this._mapping) : new Vector();
   }
}
