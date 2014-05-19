package uk.co.lecafeautomatique.zedogg.gui;

import java.awt.Adjustable;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class TrackingAdjustmentListener implements AdjustmentListener {
  protected int _lastMaximum = -1;

  public void adjustmentValueChanged(AdjustmentEvent e) {
    Adjustable bar = e.getAdjustable();
    int currentMaximum = bar.getMaximum();
    if (currentMaximum == this._lastMaximum) {
      return;
    }
    int bottom = bar.getValue();
    bottom += bar.getVisibleAmount();
    bottom += bar.getUnitIncrement();

    if (bottom >= this._lastMaximum) {
      bar.setValue(currentMaximum);
    }
    this._lastMaximum = currentMaximum;
  }
}
