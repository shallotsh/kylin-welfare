package org.kylin.service.xcode.filters;

import org.kylin.bean.p5.WCode;

import java.util.List;

public interface CompositeFilter {
    List<WCode> filter(List<WCode> target, CompositeFilterDTO params);
}
