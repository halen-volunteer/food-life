package com.hmdp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hmdp.model.entity.VoucherOrder;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Volunteer
 *
 */
public interface VoucherOrderMapper extends BaseMapper<VoucherOrder> {

    @Select("select voucher_id from tb_voucher_order where phone = #{phone}")
    List<Long> findVoucherIdsByPhone(String phone);
}
