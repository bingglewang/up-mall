/**
 * @filename:UserAddressController 2020年04月08日
 * @project up-mall板根商城  V1.0
 * Copyright(c) 2020 binggleWang Co. Ltd.
 * All right reserved.
 */
package com.zsl.upmall.web;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zsl.upmall.aid.JsonResult;
import com.zsl.upmall.aid.PageParam;
import com.zsl.upmall.context.RequestContext;
import com.zsl.upmall.context.RequestContextMgr;
import com.zsl.upmall.entity.AdministrativeRegion;
import com.zsl.upmall.entity.UserAddress;
import com.zsl.upmall.service.AdministrativeRegionService;
import com.zsl.upmall.service.UserAddressService;
import com.zsl.upmall.vo.in.address.AddressAddVo;
import com.zsl.upmall.vo.out.address.AddressInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * <p>自动生成工具：mybatis-dsc-generator</p>
 *
 * <p>说明： 收货地址表API接口层</P>
 * @version: V1.0
 * @author: binggleWang
 * @time    2020年04月08日
 *
 */
@RestController
@RequestMapping("/userAddress")
public class UserAddressController{

    @Autowired
    protected UserAddressService baseService;

    @Autowired
    protected AdministrativeRegionService administrativeRegionService;

    protected JsonResult<UserAddress> result = new JsonResult<UserAddress>();
    /**
     * @explain 查询对象  <swagger GET请求>
     * @param   id 对象参数：id
     * @return  JsonResult
     * @author  binggleWang
     * @time    2019年10月16日
     */
    @GetMapping("/getById/{id}")
    public JsonResult<AddressInfo> getById(@PathVariable("id")Long id){
        JsonResult<AddressInfo> resultAddress = new JsonResult<AddressInfo>();
        AddressInfo obj=baseService.addressInfo(id);
        if (null!=obj ) {
            resultAddress.success(obj);
        }else {
            resultAddress.error("查询对象不存在！");
        }
        return resultAddress;
    }

    /**
     * @explain 删除对象
     * @param   id 对象参数：id
     * @return  JsonResult
     * @author  binggleWang
     * @time    2019年10月16日
     */
    @PostMapping("/deleteById")
    public JsonResult<UserAddress> deleteById(Long id){
        JsonResult<UserAddress> result=new JsonResult<UserAddress>();
        UserAddress obj=baseService.getById(id);
        if (null!=obj) {
            boolean rsg = baseService.removeById(id);
            if (rsg) {
                result.success("删除成功");
            }else {
                result.error("删除失败！");
            }
        }else {
            result.error("删除的对象不存在！");
        }
        return result;
    }


    /**
     * @explain 修改地址为默认地址i
     * @param   id 对象参数：id
     * @return  JsonResult
     * @author  binggleWang
     * @time    2019年10月16日
     */
    @PostMapping("/updateDefault")
    public JsonResult<UserAddress> updateDefault(Long id){
        JsonResult<UserAddress> result=new JsonResult<UserAddress>();
        UserAddress obj = baseService.getById(id);
        if (null!=obj) {
            //获取用户id
            RequestContext requestContext = RequestContextMgr.getLocalContext();
            QueryWrapper<UserAddress> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", requestContext.getUserId());
            UserAddress update = new UserAddress();
            update.setIsDefault(0);
            baseService.update(update,queryWrapper);
            UserAddress updateDefault = new UserAddress();
            updateDefault.setId(obj.getId());
            updateDefault.setIsDefault(1);
            baseService.updateById(updateDefault);
            result.success("修改成功");
        }else {
            result.error("修改的对象不存在！");
        }
        return result;
    }


    /**
     * @explain 添加
     * @param   addressAddVo 对象参数：T
     * @return  Boolean
     * @author  binggleWang
     * @time    2019年10月16日
     */
    @PostMapping("/insert")
    public JsonResult<UserAddress> insert(@RequestBody AddressAddVo addressAddVo){
        JsonResult<UserAddress> result=new JsonResult<UserAddress>();
        UserAddress entity = new UserAddress();
        if (null!=addressAddVo) {
            QueryWrapper<AdministrativeRegion> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("country_code",addressAddVo.getCountryCode())
                    .eq("province_code",addressAddVo.getProvinceCode())
                    .eq("city_code",addressAddVo.getCityCode())
                    .eq("area_code",addressAddVo.getAreaCode());
            AdministrativeRegion region = administrativeRegionService.getOne(queryWrapper);
            if(region == null){
                return result.error("添加失败！(地址错误)");
            }
            //获取用户id
            RequestContext requestContext = RequestContextMgr.getLocalContext();
            entity.setUserId(requestContext.getUserId());
            entity.setAdministrativeRegionId(region.getId());
            entity.setAddTime(new Date());
            entity.setUpdateTime(new Date());
            BeanUtils.copyProperties(addressAddVo,entity);
            boolean rsg = baseService.save(entity);
            if (rsg) {
                result.success("添加成功");
            }else {
                result.error("添加失败！");
            }
        }else {
            result.error("请传入正确参数！");
        }
        return result;
    }

    /**
     * @explain 修改
     * @param   addressAddVo 对象参数：T
     * @return  Boolean
     * @author  binggleWang
     * @time    2019年10月16日
     */
    @PostMapping("/update")
    public JsonResult<UserAddress> update(@RequestBody AddressAddVo addressAddVo){
        JsonResult<UserAddress> result=new JsonResult<UserAddress>();
        UserAddress entity = new UserAddress();
        if (null!=addressAddVo) {
            QueryWrapper<AdministrativeRegion> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("country_code",addressAddVo.getCountryCode())
                    .eq("province_code",addressAddVo.getProvinceCode())
                    .eq("city_code",addressAddVo.getCityCode())
                    .eq("area_code",addressAddVo.getAreaCode());
            AdministrativeRegion region = administrativeRegionService.getOne(queryWrapper);
            if(region == null){
                return result.error("修改失败！(地址错误)");
            }
            entity.setAdministrativeRegionId(region.getId());
            BeanUtils.copyProperties(addressAddVo,entity);
            entity.setUpdateTime(new Date());
            boolean rsg = baseService.updateById(entity);
            if (rsg) {
                result.success("修改成功");
            }else {
                result.error("修改失败！");
            }
        }else {
            result.error("请传入正确参数！");
        }
        return result;
    }

    /**
     * @explain 分页条件查询用户
     * @return  PageInfo<UserFriend>
     * @author  binggleWang
     * @time    2019年10月16日
     */
    @GetMapping("/getPages")
    public JsonResult<IPage<AddressInfo>> getPages(PageParam param){
        JsonResult<IPage<AddressInfo>> returnPage=new JsonResult<IPage<AddressInfo>>();
        //获取用户id
        RequestContext requestContext = RequestContextMgr.getLocalContext();
        Page<AddressInfo> page=new Page<AddressInfo>(param.getPageNum(),param.getPageSize());
        //分页数据
        IPage<AddressInfo> pageData=baseService.addressPage(page,requestContext.getUserId());
        returnPage.success(pageData);

        return returnPage;
    }
    
}