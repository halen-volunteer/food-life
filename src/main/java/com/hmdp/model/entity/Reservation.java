package com.hmdp.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("reservation")
public class Reservation {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String phone;

    private LocalDateTime communicationTime;

    private String shopName;
}
