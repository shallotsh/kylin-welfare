package org.kylin.service.exporter;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.kylin.constant.ExportPatternEnum;

import javax.print.Doc;
import java.io.IOException;
import java.util.List;

public interface IDocExportTool<T> {
    /**
     * 写入默认格式的文件头
     *
     * @param docHolder
     * @param title
     */
    void writeTitleAsDefaultFormat(DocHolder docHolder, String title);

    /**
     * 写文件的主要内容
     *
     * @param docHolder
     * @param data
     */
    void writeContentToDoc(DocHolder docHolder, T data);

    /**
     * 导出文档到文件
     *
     * @param docHolder
     * @param fullPath
     * @param fileName
     * @return
     * @throws IOException
     */
    String exportDocAsFile(DocHolder docHolder, String fullPath, String fileName) throws IOException;

    /**
     * 导出文档到默认位置
     *
     * @param docHolder
     * @return
     * @throws IOException
     */
    default String exportDocAsFile(DocHolder docHolder) throws IOException{
        return exportDocAsFile(docHolder, null, null);
    }

    /**
     * 获取支持的导出模式
     *
     * @return
     */
    List<ExportPatternEnum> getSupportedExportPatterns();
}
