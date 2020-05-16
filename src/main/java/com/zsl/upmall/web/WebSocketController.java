package com.zsl.upmall.web;

import com.zsl.upmall.aid.JsonResult;
import com.zsl.upmall.config.WebSocket;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName WebSocketController
 * @Description 消息通知
 * @Author binggleW
 * @Date 2019-12-30 16:33
 * @Version 1.0
 **/
@RestController
@RequestMapping("test")
public class WebSocketController {

    protected JsonResult result = new JsonResult();

    @GetMapping("send")
    public JsonResult sendMessage(String message){
        WebSocket.sendMessageAll(message);
        return result.success(message);
    }
}
