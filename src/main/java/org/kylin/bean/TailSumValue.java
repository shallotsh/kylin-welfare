package org.kylin.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class TailSumValue {
    private List<Integer> tailSumValuesOf2d;
    private List<Integer> tailSumValuesOf3d;

    public TailSumValue() {
        this.tailSumValuesOf2d = Collections.emptyList();
        this.tailSumValuesOf3d = Collections.emptyList();
    }
}
