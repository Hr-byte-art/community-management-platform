package com.community.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class GenderConverter implements Converter<Integer> {
    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == null) return new WriteCellData<>("");
        switch (value) {
            case 1: return new WriteCellData<>("男");
            case 2: return new WriteCellData<>("女");
            default: return new WriteCellData<>("未知");
        }
    }
}
