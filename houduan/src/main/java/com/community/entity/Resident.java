package com.community.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.*;
import com.community.converter.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("resident")
public class Resident {
    @TableId(type = IdType.AUTO)
    @ExcelProperty("编号")
    private Long id;
    
    @ExcelProperty("姓名")
    private String name;
    
    @ExcelProperty("身份证号")
    @ColumnWidth(20)
    private String idCard;
    
    @ExcelProperty(value = "性别", converter = GenderConverter.class)
    private Integer gender;
    
    @ExcelProperty("出生日期")
    @ColumnWidth(15)
    private LocalDate birthDate;
    
    @ExcelProperty("联系电话")
    @ColumnWidth(15)
    private String phone;
    
    @ExcelProperty("地址")
    @ColumnWidth(25)
    private String address;
    
    @ExcelProperty("楼栋号")
    private String buildingNo;
    
    @ExcelProperty("单元号")
    private String unitNo;
    
    @ExcelProperty("房间号")
    private String roomNo;
    
    @ExcelProperty(value = "居住类型", converter = ResidenceTypeConverter.class)
    private String residenceType;
    
    @ExcelProperty("入住日期")
    @ColumnWidth(15)
    private LocalDate moveInDate;
    
    @ExcelProperty(value = "状态", converter = CommonStatusConverter.class)
    private Integer status;
    
    @ExcelIgnore
    private String photo;
    
    @ExcelProperty("备注")
    @ColumnWidth(20)
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    @ExcelProperty("创建时间")
    @ColumnWidth(20)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ExcelIgnore
    private LocalDateTime updateTime;
}
