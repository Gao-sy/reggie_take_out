package org.example.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
