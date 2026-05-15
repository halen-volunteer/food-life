package com.hmdp.ai.service;

import com.hmdp.mapper.ReservationMapper;
import com.hmdp.model.entity.Reservation;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiReservationService {

    @Resource
    private ReservationMapper reservationMapper;

    public void insert(Reservation reservation) {
        reservationMapper.insert(reservation);
    }

    public List<Reservation> findByPhone(String phone) {
        return reservationMapper.findByPhone(phone);
    }
}
