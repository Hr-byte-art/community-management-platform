package com.community.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class WorkOrderStatusConverter implements Converter<Integer> {
    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == null) return new WriteCellData<>("");
        switch (value) {
            case 0: return new WriteCellData<>("待处理");
            case 1: return new WriteCellData<>("处理中");
            case 2: return new WriteCellData<>("已完成");
            case 3: return new WriteCellData<>("已关闭");
            default: return new WriteCellData<>("未知");
        }
    }
}
