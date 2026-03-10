package com.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.community.entity.PointsAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PointsAccountMapper extends BaseMapper<PointsAccount> {

    @Update("UPDATE points_account SET total_points = total_points + #{delta} WHERE user_id = #{userId}")
    int changePoints(@Param("userId") Long userId, @Param("delta") Integer delta);
}

