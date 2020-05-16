package com.zsl.upmall.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName GroupNoticeUnpaidTask
 * @Description 拼团通知
 * @Author binggleW
 * @Date 2020-05-16 9:53
 * @Version 1.0
 **/
public class GroupNoticeUnpaidTask extends Task {
    private final Logger logger = LoggerFactory.getLogger(GroupNoticeUnpaidTask.class);

    private long joinGroupId = -1;
    private Integer status;
    private Integer type;


    public GroupNoticeUnpaidTask(long joinGroupId, long delayInMilliseconds,Integer status,Integer type){
        super("GroupNoticeUnpaidTask-" + joinGroupId, delayInMilliseconds);
        this.joinGroupId = joinGroupId;
        this.status = status;
        this.type = type;
    }


    @Override
    public void run() {

    }
}
