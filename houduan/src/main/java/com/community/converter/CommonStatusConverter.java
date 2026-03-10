package com.community.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class CommonStatusConverter implements Converter<Integer> {
    @Override
    public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == null) return new WriteCellData<>("");
        switch (value) {
            case 1: return new WriteCellData<>("正常");
            case 0: return new WriteCellData<>("禁用");
            default: return new WriteCellData<>("未知");
        }
    }
}
