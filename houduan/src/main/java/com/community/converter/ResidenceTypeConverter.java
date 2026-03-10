package com.community.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class ResidenceTypeConverter implements Converter<String> {
    @Override
    public WriteCellData<?> convertToExcelData(String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == null) return new WriteCellData<>("");
        switch (value) {
            case "OWNER": return new WriteCellData<>("业主");
            case "TENANT": return new WriteCellData<>("租户");
            case "FAMILY": return new WriteCellData<>("家属");
            default: return new WriteCellData<>(value);
        }
    }
}
