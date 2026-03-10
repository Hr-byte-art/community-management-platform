package com.community.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

public class ActivityTypeConverter implements Converter<String> {
    @Override
    public WriteCellData<?> convertToExcelData(String value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == null) return new WriteCellData<>("");
        switch (value) {
            case "CULTURE": return new WriteCellData<>("文化活动");
            case "SPORTS": return new WriteCellData<>("体育活动");
            case "VOLUNTEER": return new WriteCellData<>("志愿服务");
            case "EDUCATION": return new WriteCellData<>("教育培训");
            case "OTHER": return new WriteCellData<>("其他");
            default: return new WriteCellData<>(value);
        }
    }
}
