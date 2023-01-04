package com.example.dakudemo;

import com.example.dakudemo.controller.UserController;
import com.example.dakudemo.entity.Device;
import com.example.dakudemo.entity.DocumentDevice;
import com.example.dakudemo.entity.Lend;
import com.example.dakudemo.service.LendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class DakudemoApplicationTests {

    @Autowired
    private LendService lendService;

    @Test
    void contextLoads() {
        UserController userController = new UserController();
        Lend lend = new Lend();
        lend.setDevice_user_id(1);
        lend.setDocument_id("JY000001");

        DocumentDevice documentDevice1 = new DocumentDevice();
        documentDevice1.setDocument_id("JY000001");
        documentDevice1.setDevice_number(100);
        documentDevice1.setId(1);
        documentDevice1.setDevice_id("dev007");
        documentDevice1.setDevice_status("1");
        List<DocumentDevice> documentDeviceList = new ArrayList<>();
        documentDeviceList.add(documentDevice1);
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String lend_time = sp.format(new Date());

        lend.setUse_reason("更换备件");
        lend.setUse_time(lend_time);
        lend.setDocumentDeviceList(documentDeviceList);
        lend.setApprove_type(1);
        lend.setDescription("借用单1");
        lendService.addLend(lend);
    }

}
