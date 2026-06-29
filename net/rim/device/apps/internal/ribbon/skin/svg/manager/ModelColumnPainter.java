package net.rim.device.apps.internal.ribbon.skin.svg.manager;

import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.ColumnPainter;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbDescriptionProvider;
import net.rim.device.internal.ui.IconCollection;
import net.rim.plazmic.internal.mediaengine.service.node.TextNode;

class ModelColumnPainter extends ColumnPainter {
   boolean timeEmpty;
   boolean subjEmpty;
   boolean addrEmpty;
   protected TextNode[] _nodes = new TextNode[3];
   private Handler _handler;

   public ModelColumnPainter(Handler handler, TextNode node1, TextNode node2, TextNode node3) {
      this.timeEmpty = this.subjEmpty = this.addrEmpty = true;
      this._nodes[0] = node1;
      this._nodes[1] = node2;
      this._nodes[2] = node3;
      this._handler = handler;
   }

   @Override
   public void setPriority(int priority) {
   }

   @Override
   public void setLevelOne(boolean on) {
   }

   @Override
   public void setEmphasis(boolean emphasis) {
   }

   @Override
   public void drawIcon(int columnId, IconCollection icons, int index) {
   }

   @Override
   public void drawText(int columnId, String text, boolean annex) {
      switch (columnId) {
         case 2:
            break;
         case 3:
            if (this._nodes[1] != null) {
               this._nodes[1].setString(text.toCharArray());
               this.addrEmpty = false;
            }
            break;
         case 4:
         default:
            if (this._nodes[2] != null) {
               this._nodes[2].setString(text.trim().toCharArray());
               this.subjEmpty = false;
               return;
            }
      }
   }

   @Override
   public void drawTime(int columnId, long time) {
      if (this._nodes[0] != null) {
         this._nodes[0].setString(this._handler.formattedTime(time).toCharArray());
         this.timeEmpty = false;
      }
   }

   @Override
   public void drawModel(int columnId, RIMModel model, Object context) {
      switch (columnId) {
         case 3:
            if (!(model instanceof VerbDescriptionProvider)) {
               if (model instanceof PersonNameModel) {
                  String text = this._handler.getFullName((PersonNameModel)model);
                  if (this._nodes[1] != null) {
                     this._nodes[1].setString(text.toCharArray());
                  }
               }
            } else {
               VerbDescriptionProvider vdp = (VerbDescriptionProvider)model;
               String text = vdp.getVerbDescription(null);
               if (this._nodes[1] != null) {
                  this._nodes[1].setString(text.toCharArray());
                  return;
               }
            }
      }
   }

   @Override
   public void drawModel(int columnId, RIMModel model, Object context, boolean annex) {
      this.drawModel(columnId, model, context);
   }

   @Override
   public boolean isColumnEmpty(int columnId) {
      switch (columnId) {
         case 1:
            return true;
         case 2:
         default:
            return this.timeEmpty;
         case 3:
            return this.addrEmpty;
         case 4:
            return this.subjEmpty;
      }
   }

   @Override
   public void clear(int columnId) {
      switch (columnId) {
         case 2:
         default:
            this.timeEmpty = true;
            return;
         case 3:
            this.addrEmpty = true;
         case 1:
            return;
         case 4:
            this.subjEmpty = true;
      }
   }

   @Override
   public void setColumnFilled(int columnId) {
      this.clear(columnId);
   }

   void clear() {
      this.timeEmpty = this.subjEmpty = this.addrEmpty = true;
   }
}
