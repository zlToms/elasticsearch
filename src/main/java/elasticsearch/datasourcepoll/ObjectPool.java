package elasticsearch.datasourcepoll;

import java.util.NoSuchElementException;

public interface ObjectPool<T> {
    //从池中获取一个对象，客户端在使用完对象后必须使用 returnObject方法返还获取的对象
    T borrowObject() throws Exception, NoSuchElementException,IllegalStateException ;

    //将对象返还到池中，对象必须是从 borrowObject 方法获得的
    void returnObject(T obj) throws Exception;

    //使池中的对象失效，当获取到的对象被确定无效时，（由于异常或者其他问题），应调用该方法
    void invaliddateObject(T obj) throws Exception;

    //池中当前闲置的对象数量
    int  getNumIdle();

    // 当前从池中借出对象的数量
    int getNumActive();

    //清除池中闲置对象
    void clear() throws Exception, UnsupportedOperationException;

    //关闭这个池，并释放与之相关的对象
    void close();

}
