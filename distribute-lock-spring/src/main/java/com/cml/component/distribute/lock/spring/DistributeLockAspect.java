package com.cml.component.distribute.lock.spring;

import com.cml.component.distribute.lock.core.DistributeLock;
import com.cml.component.distribute.lock.core.DistributeLockHelper;
import com.cml.component.distribute.lock.core.DistributeLockService;
import com.cml.component.distribute.lock.core.LockHolder;
import com.cml.component.distribute.lock.core.exception.LockFailException;
import com.cml.component.distribute.lock.core.key.KeyGenerator;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

@Aspect
public class DistributeLockAspect {

    private static ExpressionParser parser = new SpelExpressionParser();
    private DistributeLockService distributeLockService;

    public DistributeLockAspect(DistributeLockService distributeLockService) {
        this.distributeLockService = distributeLockService;
    }

    @Around("@annotation(com.cml.component.distribute.lock.core.DistributeLock)")
    public Object lockAspect(ProceedingJoinPoint point) throws Throwable {

        StandardEvaluationContext context = new StandardEvaluationContext(parser);
        Object[] args = point.getArgs();
        if (args != null) {
            int len = args.length;
            for (int i = 0; i < len; i++) {
                context.setVariable("arg" + i, args[i]);
            }
        }

        Signature sig = point.getSignature();
        MethodSignature msig = (MethodSignature) sig;
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());

        DistributeLock lock = currentMethod.getAnnotation(DistributeLock.class);
        String key = (String) parser.parseExpression(lock.key()).getValue(context);

        LockHolder distributeLock = distributeLockService.getLock(lock.category(), key, lock.timeoutInMills());
        if (distributeLock.isLockSuccess()) {
            try {
                return point.proceed();
            } finally {
                distributeLockService.unLock(distributeLock);
            }
        }
        throw new LockFailException(lock.failMsg());
    }


}
