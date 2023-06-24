package org.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.example.reggie.common.R;
import org.example.reggie.dto.OrdersDto;
import org.example.reggie.entity.OrderDetail;
import org.example.reggie.entity.Orders;
import org.example.reggie.service.OrderDetailService;
import org.example.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize, HttpSession session){
        log.info("page={},pageSize={}",page,pageSize);
        //查询当前用户id
        Long id=(Long) session.getAttribute("user");
        log.info("userid={}",id);
        //构造分页构造器
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        Page<OrdersDto> dtoPage=new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper<>();

        //设置查询条件
        queryWrapper.eq(null!=id,Orders::getUserId,id);
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //执行查询
        orderService.page(pageInfo,queryWrapper);
        List<Orders> records=pageInfo.getRecords();
        //获取订单号
        List<OrdersDto> ordersDtos=records.stream().map((item)->{
            OrdersDto ordersDto = new OrdersDto();
            //对象拷贝
            BeanUtils.copyProperties(item,ordersDto);
            //获取订单id
            Long orderId=item.getId();
            //构造条件构造器
            LambdaQueryWrapper<OrderDetail> detailqueryWrapper=new LambdaQueryWrapper<>();
            //设置查询条件
            detailqueryWrapper.eq(null!=orderId,OrderDetail::getOrderId,orderId);
            ordersDto.setOrderDetails(orderDetailService.list(detailqueryWrapper));
            return ordersDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(ordersDtos);
        return R.success(dtoPage);

    }


    /**
     * 查询订单明细
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("page")
    public R<Page> page(int page, int pageSize, String number, String beginTime,String endTime){
        log.info("page = {},pageSize = {},number = {},beginTime = {},endTime = {}" ,page,pageSize,number,beginTime,endTime);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(number),Orders::getNumber,number);
        queryWrapper.ge(StringUtils.isNotEmpty(beginTime),Orders::getOrderTime,beginTime);
        queryWrapper.le(StringUtils.isNotEmpty(endTime),Orders::getOrderTime,endTime);
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);

        //执行查询
        orderService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(@RequestBody Orders orders){
        log.info(orders.toString());
        long id = Thread.currentThread().getId();
        log.info("线程id为：{}",id);
        orderService.updateById(orders);
        return R.success("订单状态修改成功");
    }


}