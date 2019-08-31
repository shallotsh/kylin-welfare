package org.kylin.service.exporter;

import org.kylin.constant.ExportPatternEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ExportToolSelector {

    @Autowired
    private List<IDocExportTool> exporters;


    public Optional<IDocExportTool> getByExportPattern(ExportPatternEnum exportPatternEnum){

        return exporters.stream()
                .filter(iDocExportTool -> iDocExportTool.getSupportedExportPatterns().contains(exportPatternEnum))
                .findFirst();
    }
}
