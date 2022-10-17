package org.kylin.service.p3;

import org.kylin.bean.p3.ExpertCodeReq;
import org.kylin.bean.p5.WCode;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ExpertCodeService {

    /**
     * 专家编码，所有数字取3位排列，结果为组三直选
     * @param riddleSeq
     * @return
     */
    List<WCode> expertEncode(List<Integer> riddleSeq);


    /**
     * 转组选
     *
     * @param wCodes
     * @return
     */
    List<WCode> convertToGroupCodesForEveryFreq(List<WCode> wCodes);


    /**
     * 常规杀码算法
     *
     * @param req
     * @return
     */
    List<WCode> killCode(ExpertCodeReq req);

    /**
     * 导出预测吗到文件
     *
     * @param req
     * @return
     * @throws IOException
     */
    Optional<String> exportCodeToFile(ExpertCodeReq req)  throws IOException;
}
