package org.kylin.service.exporter.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.kylin.bean.p5.WCodeReq;
import org.kylin.service.exporter.AbstractDocumentExporter;
import org.kylin.service.exporter.DocHolder;

import java.util.Objects;

public class P5Select3DExporter extends AbstractDocumentExporter {

    @Override
    public void writeContentToDoc(DocHolder docHolder, WCodeReq data) {
        Objects.requireNonNull(docHolder);
        Objects.requireNonNull(data);

        if(CollectionUtils.isEmpty(data.getWCodes())){
            return;
        }
        // TODO: 2019-08-31
        // 提取3D码

        // 导出3D码

    }


}
