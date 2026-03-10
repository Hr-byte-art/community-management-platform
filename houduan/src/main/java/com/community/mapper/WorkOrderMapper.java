package com.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.entity.WorkOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;
import java.util.Map;

@Mapper
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {
    
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m') as month, COUNT(*) as count " +
            "FROM work_order " +
            "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m') " +
            "ORDER BY month")
    List<Map<String, Object>> countByMonth();

    @Select("SELECT COUNT(*) FROM work_order WHERE status IN (0,1) AND is_overtime = 1")
    long countOvertimeOpen();

    @Select("SELECT COUNT(*) FROM work_order WHERE status IN (0,1) AND deadline IS NOT NULL AND DATE(deadline) = CURDATE()")
    long countTodayTodo();

    @Update("UPDATE work_order SET is_overtime = 1, update_time = NOW() " +
            "WHERE status IN (0,1) AND is_overtime = 0 AND deadline IS NOT NULL AND deadline < NOW()")
    int markOvertime();
}
