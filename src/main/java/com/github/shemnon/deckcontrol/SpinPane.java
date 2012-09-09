/*
 * Copyright (c) 2008, 2011 Oracle and/or its affiliates.
 * Copyright (c) 2012 Danno Ferrin.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.shemnon.deckcontrol;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.layout.Pane;

public class SpinPane extends Pane {

    private PerspectiveTransform transform = new PerspectiveTransform();
    /**
     * Angle Non-Observable Property
     */
    public final DoubleProperty angle = new SimpleDoubleProperty(90);

    public SpinPane() {

        // create content
        setEffect(transform);
        setupTransform();
        angle.addListener(new InvalidationListener() {
            public void invalidated(Observable vm) {
                updateAngle();
            }
        });
        addListeners();
    }

    public double getAngle() {
        return angle.get();
    }

    public void setAngle(double angle) {
        this.angle.set(angle);
    }

    public DoubleProperty angleProperty() {
        return angle;
    }

    private void setupTransform() {
        getLayoutBounds();  // has side effect of setting width and height
        updateAngle();
    }

    private void updateAngle() {
        // calculate new transform
        double radiusH = getWidth() / 2;
        double back = getWidth() / 10;
        double lx = (radiusH - Math.sin(Math.toRadians(angle.get())) * radiusH);
        double rx = (radiusH + Math.sin(Math.toRadians(angle.get())) * radiusH);
        double uly = (-Math.cos(Math.toRadians(angle.get())) * back);
        double ury = -uly;
        transform.setUlx(lx);
        transform.setUly(uly);
        transform.setUrx(rx);
        transform.setUry(ury);
        transform.setLrx(rx);
        transform.setLry(getHeight() + uly);
        transform.setLlx(lx);
        transform.setLly(getHeight() + ury);
    }

    public void addListeners() {
        layoutBoundsProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                setupTransform();
            }
        });
    }

}
