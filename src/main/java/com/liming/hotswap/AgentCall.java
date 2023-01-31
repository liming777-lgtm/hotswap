package com.liming.hotswap;

import com.sun.tools.attach.VirtualMachine;

public class AgentCall {
    public static void main(String[] args) {
        try {
            //agentjar包路径
            String jar = "E:\\work\\huaqianguroot\\app\\jarTest\\hotswap-1.0-jar-with-dependencies.jar";
            //要重载的jar包路径|要重载的类
            String param = "E:\\work\\huaqianguroot\\app\\jarTest\\replace\\test.jar|hotswap.MainStart";
            //需要线程id 可用jps -l 获取
            VirtualMachine vm = VirtualMachine.attach("59492");
            // attach到新JVM
            // 加载agentmain所在的jar包
            vm.loadAgent(jar, param);
            // detach
            vm.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
