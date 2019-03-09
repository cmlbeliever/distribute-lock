package com.cml.component.distribute.lock.redis.sample;

import com.cml.component.distribute.lock.core.DistributeLock;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @DistributeLock(category = "controller", key = "#arg0", timeoutInSeconds = 3)
    @RequestMapping("/testLock")
    public String testLock(@RequestParam String key) throws InterruptedException {
        System.out.println("=======testLock==========:" + key);
        Thread.sleep(5_000);
        return "xxx:" + key;
    }

}
