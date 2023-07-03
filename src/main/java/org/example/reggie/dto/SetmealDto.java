package org.example.reggie.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.example.reggie.entity.Setmeal;
import org.example.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
@ApiModel("套餐dto")
public class SetmealDto extends Setmeal {

    @ApiModelProperty("套餐包含的菜品")
    private List<SetmealDish> setmealDishes;

    @ApiModelProperty("套餐所属分类")
    private String categoryName;
}
