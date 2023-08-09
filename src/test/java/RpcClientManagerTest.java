import com.gw.core.reference.RpcServiceProxy;
import com.gw.core.service.HelloService;

/**
 * Description: Rpc客户端管理器测试
 *
 * @author LinHuiBa-YanAn
 * @date 2023/8/8 11:14
 */
public class RpcClientManagerTest {
    public static void main(String[] args) {
        HelloService service = RpcServiceProxy.getProxyService(HelloService.class);
        String result = service.sayHello("言安");
        System.out.println(result);
    }
}
