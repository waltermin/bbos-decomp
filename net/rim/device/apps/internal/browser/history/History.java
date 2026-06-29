package net.rim.device.apps.internal.browser.history;

import java.util.Vector;
import net.rim.device.api.util.DataBuffer;

public final class History {
   private Vector _nodes = (Vector)(new Object());
   private int _currentNode = -1;
   private int _lastViewedNode = -1;
   private static final int VERSION = 6;
   private static final int MAX_HISTORY_SIZE = 20;

   public final synchronized HistoryNode lookupNodeAt(int index) {
      return index >= 0 && index < this._nodes.size() ? (HistoryNode)this._nodes.elementAt(index) : null;
   }

   public final synchronized int lookupCurrentNodeId() {
      return this._currentNode;
   }

   public final synchronized int getSize() {
      return this._nodes.size();
   }

   public final synchronized void addNewNode(HistoryNode newNode) {
      this.addNewNode(newNode, true);
   }

   public final synchronized void addNewNode(HistoryNode newNode, boolean removeCurrentNodeIfSameURL) {
      if (newNode != null) {
         this._lastViewedNode = this._currentNode;
         if (removeCurrentNodeIfSameURL && this._currentNode >= 0 && ((HistoryNode)this._nodes.elementAt(this._currentNode)).getUrl().equals(newNode.getUrl())) {
            this._currentNode--;
         }

         this._currentNode++;
         this._nodes.setSize(this._currentNode);

         while (this._currentNode > 20) {
            this._nodes.removeElementAt(0);
            this._lastViewedNode--;
            this._currentNode--;
         }

         this._nodes.addElement(newNode);
      }
   }

   public final synchronized boolean canGoForward() {
      return this._currentNode < this._nodes.size() - 1;
   }

   public final synchronized boolean canGoBack() {
      return this._currentNode > 0;
   }

   public final synchronized HistoryNode currentNode() {
      return this._currentNode > -1 && this._currentNode < this._nodes.size() ? (HistoryNode)this._nodes.elementAt(this._currentNode) : null;
   }

   public final synchronized void replaceCurrentNode(HistoryNode newNode) {
      if (this._currentNode > -1 && this._currentNode < this._nodes.size()) {
         this._nodes.setElementAt(newNode, this._currentNode);
      }
   }

   public final synchronized HistoryNode previousNode() {
      if (!this.canGoBack()) {
         return null;
      }

      this._lastViewedNode = this._currentNode;
      return (HistoryNode)this._nodes.elementAt(--this._currentNode);
   }

   public final synchronized HistoryNode nextNode() {
      if (!this.canGoForward()) {
         return null;
      }

      this._lastViewedNode = this._currentNode;
      return (HistoryNode)this._nodes.elementAt(++this._currentNode);
   }

   public final synchronized void forceNextNode() {
      this._currentNode++;
   }

   public final synchronized HistoryNode getNode(int index) {
      if (index >= 0 && index < this._nodes.size()) {
         this._lastViewedNode = this._currentNode;
         this._currentNode = index;
         return (HistoryNode)this._nodes.elementAt(index);
      } else {
         return null;
      }
   }

   public final synchronized HistoryNode getNode(String url) {
      int count = this._nodes.size();

      for (int i = count - 1; i > -1; i--) {
         HistoryNode node = (HistoryNode)this._nodes.elementAt(i);
         if (node.getUrl().equals(url)) {
            return node;
         }
      }

      return null;
   }

   public final synchronized void resetToLastViewedNode() {
      if (this._lastViewedNode >= 0 && this._lastViewedNode < this._nodes.size()) {
         this._currentNode = this._lastViewedNode;
      }
   }

   public final synchronized void reset() {
      this._lastViewedNode = this._currentNode;
      this._currentNode = -1;
   }

   public final synchronized void clear() {
      this._currentNode = -1;
      this._lastViewedNode = -1;
      this._nodes.removeAllElements();
   }

   public static final boolean skipData(DataBuffer dataBuffer, int browserOptionsVersion) {
      try {
         int historyVersion = 0;
         if (browserOptionsVersion > 3) {
            historyVersion = dataBuffer.readCompressedInt();
         }

         int numNodes = dataBuffer.readCompressedInt();

         for (int i = 0; i < numNodes; i++) {
            if (browserOptionsVersion == 1) {
               dataBuffer.readUTF();
            } else {
               HistoryNode.skipData(dataBuffer, historyVersion);
            }
         }

         dataBuffer.readCompressedInt();
         return true;
      } finally {
         ;
      }
   }

   public static final void serialize(DataBuffer dataBuffer) {
      dataBuffer.writeCompressedInt(6);
      dataBuffer.writeCompressedInt(0);
      dataBuffer.writeCompressedInt(-1);
   }
}
