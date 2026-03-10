package com.community.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class OrderTypeConverter implements Converter<String> {
    @Override
    public WriteCellData<?> convertToExcelData(String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == null) return new WriteCellData<>("");
        switch (value) {
            case "REPAIR": return new WriteCellData<>("报修");
            case "COMPLAINT": return new WriteCellData<>("投诉");
            case "SUGGESTION": return new WriteCellData<>("建议");
            case "OTHER": return new WriteCellData<>("其他");
            default: return new WriteCellData<>(value);
        }
    }
}
