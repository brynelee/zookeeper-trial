import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import java.util.concurrent.CountDownLatch;

public class Simple_session implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception{
        //使用上述第一种构造方法实例化一个ZooKeeper对象
        ZooKeeper zooKeeper = new ZooKeeper("spark-master:2181", 5000, new Simple_session());
        System.out.println(zooKeeper.getState());
        try{
            connectedSemaphore.await();
        }catch(InterruptedException e){}
        System.out.println("ZooKeeper session established.");
    }

    //重写Watcher接口中的process()方法
    //该方法负责处理来自ZooKeeper服务端的Watcher通知，
    //收到服务端发来的SyncConnected事件之后，解除主程序在CountDownLatch上的等待阻塞
    public void process(WatchedEvent event){
        System.out.println("Receive watched event:" + event);
        if(Event.KeeperState.SyncConnected == event.getState()){
            connectedSemaphore.countDown();
        }
    }
}

