package com.zsl.upmall.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zsl.upmall.aid.JsonResult;
import com.zsl.upmall.aid.PageParam;
import com.zsl.upmall.service.GrouponOrderMasterService;
import com.zsl.upmall.vo.out.GrouponListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName GrouponOrderMasterController
 * @Description 参团列表
 * @Author binggleW
 * @Date 2020-05-13 10:37
 * @Version 1.0
 **/
@RestController
@RequestMapping("groupon")
public class GrouponOrderMasterController {

    @Autowired
    private GrouponOrderMasterService grouponOrderMasterService;

    @GetMapping("list")
    public JsonResult list(PageParam param, Integer grouponOrderId){
        JsonResult result = new JsonResult();
        Page<GrouponListVo> page = new Page(param.getPageNum(), param.getPageSize());
        return result.success(grouponOrderMasterService.getGrouponListByPage(page,grouponOrderId));
    }

    @GetMapping("test")
    public JsonResult test(Long orderId,Integer userId){
        JsonResult result = new JsonResult();
        grouponOrderMasterService.doGrouponService(orderId,userId);
        return result.success(null);
    }
}
