/*    */ package emssn00p.viewer;
/*    */ 
/*    */ import java.awt.Adjustable;
/*    */ import java.awt.event.AdjustmentEvent;
/*    */ import java.awt.event.AdjustmentListener;
/*    */ 
/*    */ public class TrackingAdjustmentListener
/*    */   implements AdjustmentListener
/*    */ {
/* 37 */   protected int _lastMaximum = -1;
/*    */ 
/*    */   public void adjustmentValueChanged(AdjustmentEvent e)
/*    */   {
/* 52 */     Adjustable bar = e.getAdjustable();
/* 53 */     int currentMaximum = bar.getMaximum();
/* 54 */     if (currentMaximum == this._lastMaximum) {
/* 55 */       return;
/*    */     }
/* 57 */     int bottom = bar.getValue();
/* 58 */     bottom += bar.getVisibleAmount();
/* 59 */     bottom += bar.getUnitIncrement();
/*    */ 
/* 61 */     if (bottom >= this._lastMaximum) {
/* 62 */       bar.setValue(currentMaximum);
/*    */     }
/* 64 */     this._lastMaximum = currentMaximum;
/*    */   }
/*    */ }

/* Location:           /Users/tnarik/Desktop/emssn00p.jar
 * Qualified Name:     emssn00p.viewer.TrackingAdjustmentListener
 * JD-Core Version:    0.6.1
 */