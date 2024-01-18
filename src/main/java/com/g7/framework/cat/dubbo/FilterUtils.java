package com.g7.framework.cat.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;
import com.dianping.cat.Cat;

import java.util.List;

/**
 * @author dreamyao
 * @title
 * @date 2020-03-23 10:36
 * @since 1.0.0
 */
public class FilterUtils {

    public static String APP_NAME = "AppName";
    public static String getTargetApp(RpcContext context)  {
        try {
            List<Invoker<?>> listInvoker = context.getInvokers();
            for (int i = 0; i < listInvoker.size(); i++) {
                Invoker<?> prov = listInvoker.get(i);
                if(prov instanceof com.alibaba.dubbo.rpc.protocol.InvokerWrapper){
					// 通过实例判断 减少循环时间
                    URL url = invokeMethod(prov, "getProviderUrl");
                    if (url != null) {
                        return url.getParameter(Constants.APPLICATION_KEY);
                    }
                }

            }
        } catch (Exception e) {
            Cat.logError("getTargetApp",e);
        }
        return "Unknow";
    }

    private static URL invokeMethod(Object obj, String methodName) throws Exception {
        Class c = obj.getClass();
        Class types[] = new Class[0];
        // 动态调用sayHello方法
        java.lang.reflect.Method m = c.getMethod(methodName, types);
        m.setAccessible(true);
        // 传给方法的的参数
        return (URL) m.invoke(obj);
    }
}
