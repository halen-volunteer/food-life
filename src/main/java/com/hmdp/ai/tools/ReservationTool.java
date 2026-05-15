package com.hmdp.ai.tools;

import com.hmdp.ai.service.AiReservationService;
import com.hmdp.model.entity.Reservation;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationTool {

    @Resource
    private AiReservationService aiReservationService;

    @Tool("预约到店消费服务")
    public void addReservation(
            @P("用户姓名") String name,
            @P("用户手机号") String phone,
            @P("预约到店消费时间，格式为 yyyy-MM-dd'T'HH:mm") String communicationTime,
            @P("预约指定的商家名称") String shopName
    ) {
        Reservation reservation = new Reservation(null, name, phone, LocalDateTime.parse(communicationTime), shopName);
        aiReservationService.insert(reservation);
    }

    @Tool("根据用户手机号查询预约单")
    public List<Reservation> findReservation(@P("用户手机号") String phone) {
        return aiReservationService.findByPhone(phone);
    }
}
