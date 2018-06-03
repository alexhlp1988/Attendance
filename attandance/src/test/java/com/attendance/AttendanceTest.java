package com.attendance;

import com.attendance.pojo.UserAttendance;
import com.attendance.service.AttendanceService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AttendanceTest {

    private AttendanceService service;

    @Before
    public void init(){
        ApplicationContext ctx =
                new ClassPathXmlApplicationContext("applicationContext.xml");
        service = ctx.getBean("attendanceService",AttendanceService.class);
    }

    @Test
    public void find(){
        List<UserAttendance> users = service.find(3);
        for(UserAttendance s:users){
            System.out.println(s.getAuserName());
        }
    }

    @Test
    public void test(){
        UserAttendance attendance = new UserAttendance();
        /*attendance.setAuserName("Paul");
        attendance.setaDept("销售部");
        attendance.setDistance(34d);*/
        /*attendance.setBeginTime(new Date());*/
        attendance.setaID(3);
        Date endDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            endDate = sdf.parse("2018-3-24 17:30:00");
            attendance.setEndTime(endDate);
            int count = service.modifyAttendance(attendance);
            System.out.println(count);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
