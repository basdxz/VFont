package com.basdxz.vfont.data;

import lombok.*;
import org.joml.Vector2f;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public class Curve {
    protected final Vector2f start;
    protected final Vector2f control;
    protected final Vector2f end;
    protected final List<Vector2f> points;

    public Curve(Vector2f start, Vector2f control, Vector2f end) {
        this.start = start;
        this.control = control;
        this.end = end;
        points = Collections.unmodifiableList(Arrays.asList(start, control, end));
    }
}
