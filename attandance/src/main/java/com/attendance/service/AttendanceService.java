package com.attendance.service;

import com.attendance.controller.CustomType;
import com.attendance.dao.AttendanceMapper;
import com.attendance.dao.PermissionMapper;
import com.attendance.dao.SchedulingMapper;
import com.attendance.dao.UserInfoMapper;
import com.attendance.pojo.Permission;
import com.attendance.pojo.Scheduling;
import com.attendance.pojo.UserAttendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true,propagation = Propagation.NOT_SUPPORTED)
public class AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private SchedulingMapper schedulingMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    // 查询今天的打卡记录
    public UserAttendance find(Integer aUserID,Date nowTime){
        return attendanceMapper.findNow(aUserID,nowTime);
    }

    // 修改打卡记录
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public int modify(Integer aUserID,String attRemark,Date nowTime){
        return attendanceMapper.modify(aUserID,attRemark,nowTime);
    }

    // 查询本人的打卡记录
    public List<UserAttendance> find(Integer aUserID){
        return attendanceMapper.findById(aUserID);
    }

    // 模糊查询
    public List<UserAttendance> find(Integer aUserID,Date beforeTime,Date afterTime){
        return attendanceMapper.findByParam(aUserID,beforeTime,afterTime);
    }

    // 查询所有的打卡记录
    public List<UserAttendance> find(){
        return attendanceMapper.findAll();
    }

    // 判断是否打卡
    public UserAttendance findByTime(Date beginTime,String useID){
        return attendanceMapper.findByTime(beginTime,useID);
    }

    // 上班
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public int addAttenDance(UserAttendance userAttendance){
        userAttendance.setBeginTime(new Date());
        // 1.判断打卡的时间 决定 amType 的类型
        System.out.println(userAttendance.getBeginTime());
        Date beginTime = userAttendance.getBeginTime();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String sbeginTime = sdf.format(beginTime);
        System.out.println("sss = "+sbeginTime);
        // 获取班次表的数据
        Scheduling scheduling = schedulingMapper.findType("A");
        String startTime = scheduling.getsStartTime();

        // 获取班次表的数据
        Scheduling scheduling1 = schedulingMapper.findType("B");
        String end = scheduling.getsEndTime();
        // 下午小时处理
        Integer ph = Integer.parseInt(end.substring(0,2));
        // 小时处理
        Integer hours = Integer.parseInt(sbeginTime.substring(0,2));
        Integer h = Integer.parseInt(startTime.substring(0,2));
        // 分钟处理
        Integer minutes = Integer.parseInt(sbeginTime.substring(3,5));
        Integer m = Integer.parseInt(startTime.substring(3,5));
        // 秒处理
        Integer seconds = Integer.parseInt(sbeginTime.substring(6,8));
        Integer s = Integer.parseInt(startTime.substring(6,8));
        System.out.println("系统时间："+hours+" "+minutes+" "+seconds);
        System.out.println("对比时间："+h+" "+m+" "+s);
        // 开始日期的处理
        if(hours<h){
           userAttendance.setAmType("Y");
        }else if(hours==h && minutes==0 && seconds==0){
            userAttendance.setAmType("Y");
        }else if(hours==h && minutes<=60){
            // 迟到
            userAttendance.setAmType("N");
        }else if(hours>=h+1){
            userAttendance.setAmType("T");
        }else if(hours>ph){
            // 下班不规定打卡
            System.out.println("下午不能打卡");
        }
        return attendanceMapper.addAttenDance(userAttendance);
    }

    // 下班
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public int modifyAttendance(UserAttendance userAttendance){
        // 判断打卡的时间 决定 pmType 的类型
        userAttendance.setEndTime(new Date());
        // 1.判断打卡的时间 决定 amType 的类型
        System.out.println(userAttendance.getEndTime());
        Date endTime = userAttendance.getEndTime();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        String sendTime = sdf.format(endTime);
        System.out.println("sss = "+sendTime);
        // 获取班次表的数据
        Scheduling scheduling = schedulingMapper.findType("B");
        String end = scheduling.getsEndTime();
        // 小时处理
        Integer hours = Integer.parseInt(sendTime.substring(0,2));
        Integer h = Integer.parseInt(end.substring(0,2));
        // 分钟处理
        Integer minutes = Integer.parseInt(sendTime.substring(3,5));
        Integer m = Integer.parseInt(end.substring(3,5));
        // 秒处理
        Integer seconds = Integer.parseInt(sendTime.substring(6,8));
        Integer s = Integer.parseInt(end.substring(6,8));
        System.out.println("系统时间："+hours+" "+minutes+" "+seconds);
        System.out.println("对比时间："+h+" "+m+" "+s);
        // 开始日期的处理
        if(hours<h){
            userAttendance.setPmType("L");
        }else if(hours>=h){
            userAttendance.setPmType("Y");
        }
        return attendanceMapper.modifyAttendance(userAttendance);
    }

    /**
     * 打卡记录
     * @param attendance
     * @return
     *//*
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public int add(UserAttendance attendance){
        return attendanceMapper.addAttenDance(attendance);
    }*/
}
