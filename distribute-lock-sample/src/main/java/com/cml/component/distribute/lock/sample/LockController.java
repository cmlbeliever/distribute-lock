package com.cml.component.distribute.lock.sample;

import com.cml.component.distribute.lock.sample.service.LockTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LockController {

    @Autowired
    private LockTestService lockTestService;

    @GetMapping("/lock")
    public String lock(@RequestParam String key) {
        return lockTestService.testLock(key);
    }

    @GetMapping("/lock2")
    public String lock2(@RequestParam String key) {
        return lockTestService.testLock2(key);
    }
}
