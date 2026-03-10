package com.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.entity.PointsRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface PointsRecordMapper extends BaseMapper<PointsRecord> {

    @Select("SELECT pa.user_id AS userId, pa.total_points AS totalPoints, su.username AS username, " +
            "su.real_name AS realName, su.avatar AS avatar " +
            "FROM points_account pa " +
            "LEFT JOIN sys_user su ON pa.user_id = su.id " +
            "ORDER BY pa.total_points DESC, pa.update_time ASC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> rankList(@Param("limit") Integer limit);
}

