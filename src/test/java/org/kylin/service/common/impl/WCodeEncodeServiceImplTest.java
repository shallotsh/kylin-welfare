package org.kylin.service.common.impl;

import org.junit.jupiter.api.Test;
import org.kylin.bean.p5.WCode;
import org.kylin.service.common.IWCodeEncodeService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WCodeEncodeServiceImplTest {

    private IWCodeEncodeService iwCodeEncodeService = new WCodeEncodeServiceImpl();

    @Test
    void compositionEncode() {
        List<Set<Integer>> riddles = new ArrayList<>();
        riddles.add(new HashSet<>(Arrays.asList(0,2,4)));
        riddles.add(new HashSet<>(Arrays.asList(9,1)));
        riddles.add(new HashSet<>(Arrays.asList(3,2)));

        List<WCode> wCodes = iwCodeEncodeService.compositionEncode(riddles);
        System.out.println(wCodes);
    }
}