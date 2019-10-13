package org.kylin.bean;

import lombok.*;

import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelValue<T> {
    @Getter
    @Setter
    private String label;

    @Getter
    @Setter
    private T data;
}
