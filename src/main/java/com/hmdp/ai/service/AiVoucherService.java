package com.hmdp.ai.service;

import com.hmdp.mapper.VoucherMapper;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.model.entity.Shop;
import com.hmdp.model.entity.Voucher;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiVoucherService {

    @Resource
    private AiShopService aiShopService;

    @Resource
    private VoucherMapper voucherMapper;

    @Resource
    private VoucherOrderMapper voucherOrderMapper;

    public List<Voucher> findVoucherByShopName(String shopName) {
        Shop shop = aiShopService.findShop(shopName);
        if (shop == null) {
            return List.of();
        }
        return voucherMapper.queryVoucherOfShop(shop.getId());
    }

    public List<Voucher> findVoucherByUserPhone(String userPhone) {
        List<Long> voucherIds = voucherOrderMapper.findVoucherIdsByPhone(userPhone);
        if (voucherIds == null || voucherIds.isEmpty()) {
            return List.of();
        }
        List<Voucher> vouchers = new ArrayList<>(voucherIds.size());
        for (Long voucherId : voucherIds) {
            Voucher voucher = voucherMapper.selectById(voucherId);
            if (voucher != null) {
                vouchers.add(voucher);
            }
        }
        return vouchers;
    }
}
