package net.fxft.webgateway;

import org.junit.Test;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Iterator;
import java.util.Set;

public class JmxTest {

    @Test

    public void test1() {

        try {
            //create mbean server
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
//            //create object name
//            ObjectName objectName = new ObjectName("jmxBean:name=hello");
//            //create mbean and register mbean
//            server.registerMBean(new Hello(), objectName);
            /**
             * JMXConnectorServer service
             */
            //这句话非常重要，不能缺少！注册一个端口，绑定url后，客户端就可以使用rmi通过url方式来连接JMXConnectorServer
            Registry registry = LocateRegistry.createRegistry(1099);
//            //构造JMXServiceURL
//            JMXServiceURL jmxServiceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi");
//            //创建JMXConnectorServer
//            JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(jmxServiceURL, null, server);
//            //启动
//            cs.start();

            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi");

            JMXConnector jmxc = JMXConnectorFactory.connect(url,null);

            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

            Set set = mbsc.queryMBeans(null, null);

            for (Iterator it = set.iterator(); it.hasNext();) {

                ObjectInstance oi = (ObjectInstance)it.next();

                System.out.println(oi.getObjectName());

            }

            Thread.sleep(1000);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
