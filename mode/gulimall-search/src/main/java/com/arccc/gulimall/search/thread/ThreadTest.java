package com.arccc.gulimall.search.thread;

import java.util.concurrent.*;

public class ThreadTest {
    static ExecutorService service = Executors.newFixedThreadPool(10);

    public static void main1(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main执行"+Thread.currentThread().getId());
//
//        Thread01 thread01 = new Thread01();
//        thread01.start();
//        new Thread(new Runable01()).start();
//        FutureTask<Integer> integerFutureTask = new FutureTask<>(new Callable01());
//        new Thread(integerFutureTask).start();
//        Integer integer = integerFutureTask.get();
//        System.out.println(integer);
//
//        new Thread(() -> System.out.println("ton")).start();
//        service.execute(new Runable01());
        /**
         * 线程池
         *  1、 线程池的创建
         *      1.1、通过Executors创建线程池
         *              Executors.newCachedThreadPool();//core是零，所有线程都可回收
         *              Executors.newFixedThreadPool(5);//固定大小，max=core
         *              Executors.newScheduledThreadPool(5);//定时任务线程池
         *              Executors.newSingleThreadExecutor();//单线程池，无界队列，后台获取任务挨个执行
         *      1.2、new ThreadPoolExecutor();创建线程池最多需要七大参数
         *          int corePoolSize,：核心线程数，线程池创建好之后准备就绪的线程，
         *                             相当于new Thread()创建了五个线程对象，只要传入Runnable实现类对象调用start才执行异步线程
         *                             核心线程一直存在，即便线程池很空闲，除非设置了allowCoreThreadTimeOut(允许核心线程超时)
         *          int maximumPoolSize,：最大线程数，池中允许存在的最大线程数，控制资源
         *          long keepAliveTime,：存活时间，当前线程数量大于核心线程数量时，空闲线程等待的最大等待时间，
         *                               超过该时间没有任务执行则释放空闲线程
         *          TimeUnit unit,：时间单位
         *          BlockingQueue<Runnable> workQueue,：阻塞队列，当任务数大于核心线程数时，超过的部分都将被放入阻塞队列里面，等待执行，
         *                                              只要有空闲线程，线程就会从阻塞队列里面接受任务执行
         *                                              此队列只接受execute方法提交的继承Runnable接口的实现类对象
         *                                              如果不指定容量，容量默认是Integer的最大值，可能会导致内存爆满
         *          ThreadFactory threadFactory,：创建线程使用的工厂
         *          RejectedExecutionHandler handler：如果阻塞队列满了且线程数已经到max了，按照指定策略，拒绝执行任务
         *运行流程：
         * 1、线程池创建，准备好 core 数量的核心线程，准备接受任务
         * 2、新的任务进来，用 core 准备好的空闲线程执行。
         * (1) 、core 满了，就将再进来的任务放入阻塞队列中。空闲的 core 就会自己去阻塞队列获取任务执行
         * (2) 、阻塞队列满了，就直接开新线程执行，最大只能开到 max 指定的数量
         * (3) 、max 都执行好了。Max-core 数量空闲的线程会在 keepAliveTime 指定的时间后自动销毁。最终保持到 core 大小
         * (4) 、如果线程数开到了 max 的数量，还有新任务进来，就会使用 reject 指定的拒绝策 略进行处理
         * 3、所有的线程创建都是由指定的 factory 创建的。
         *
         * 问题： core 7，max 20，workQueue 50，100并发流程
         *  7线程执行，50放入队列，13开辟新线程，剩余30执行handler拒绝策略
         *
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5,
                200,
                10L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());



        System.out.println("main执行"+Thread.currentThread().getId());
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main开始");
        /**
         * CompletableFuture 异步编排
         *      1、runAsync()无返回值
         *      2、supplyAsync()有返回值
         *          2.1、Future.get()是一个阻塞方法
         */
//        CompletableFuture.runAsync(() ->{
//            System.out.println("执行"+Thread.currentThread().getId());
//
//            System.out.println("结束"+Thread.currentThread().getId());
//        },service);
//        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("执行" + Thread.currentThread().getId());
//
//            System.out.println("结束" + Thread.currentThread().getId());
//            return 10 / 0;
//        }, service).whenComplete((result,error)->{
//            /**
//             * whenComplete 线程成功的回调，能得到两个参数，
//             *  参数1：执行成功后返回值
//             *  参数2：出现异常后的错误提示
//             */
//            System.out.println("异步完成结果:"+result);
//            System.out.println("异常是："+error);
//        }).exceptionally(throwable -> {
//            /**
//             * exceptionally 出现异常，回调此方法并返回默认值
//             */
//            return 10;
//        });
//        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("执行" + Thread.currentThread().getId());
//
//            System.out.println("结束" + Thread.currentThread().getId());
//            return 10 / 4;
//        }, service).handle((result,error)->{
//            /**
//             * handle: 结果处理方法，无论结果是什么都需要返回
//             *  参数1：执行成功后返回值
//             *  参数2：出现异常后的错误提示
//             *  return: 处理后的返回值
//             */
//            if (result!=null) {
//                return result*2;
//            }
//            if (error!=null) {
//                return 0;
//            }
//
//           return 0;
//        });
        /**
         * 串行化编排
         *  1、  thenRun，不能获取上一步执行的结果
         *      thenRun(Runnable)：任务执行完成后，当前线程继续执行新任务
         *      thenRunAsync(Runnable)：任务执行完成后，从当前线程池更换线程执行新任务
         *      thenRunAsync(Runnable,Executor)：任务执行完成后，将新任务放入指定线程池执行
         */
//        CompletableFuture.supplyAsync(() -> {
//            System.out.println("执行" + Thread.currentThread().getId());
//
//            System.out.println("结束" + Thread.currentThread().getId());
//            return 10 / 4;
//        }, service).thenRunAsync(()->{
//            System.out.println("任务二启动");
//        },service);
        /**
         * 串行化编排
         *  2、 thenAccept,可以获取上一部的返回结果，但没有返回值
         *      thenAccept(Runnable)：任务执行完成后，当前线程继续执行新任务
         *      thenAcceptAsync(Runnable)：任务执行完成后，从当前线程池更换线程执行新任务
         *      thenAcceptAsync(Runnable,Executor)：任务执行完成后，将新任务放入指定线程池执行
         */
//        CompletableFuture.supplyAsync(() -> {
//            System.out.println("执行" + Thread.currentThread().getId());
//
//            System.out.println("结束" + Thread.currentThread().getId());
//            return 10 / 4;
//        }, service).thenAcceptAsync(result ->{
//            System.out.println("任务二启动"+result);
//        },service);
        /**
         * 串行化编排
         *  3、thenApply : 可以获得上一次的返回结果，并且有返回值
         *      thenApply(Runnable)：任务执行完成后，当前线程继续执行新任务
         *      thenApplyAsync(Runnable)：任务执行完成后，从当前线程池更换线程执行新任务
         *      thenApplyAsync(Runnable,Executor)：任务执行完成后，将新任务放入指定线程池执行
         */
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("执行" + Thread.currentThread().getId());
//
//            System.out.println("结束" + Thread.currentThread().getId());
//            return 10 / 4;
//        }, service).thenApplyAsync(result -> {
//            return 0;
//        });
        /**
         * 两个组合任务，两个任务都要完成
         * runAfterBoth:无参无返回
         * thenAcceptBoth：有参无返回
         * thenCombine：有参有返回
         */
//        CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("future1线程s" + Thread.currentThread().getId());
//
//            System.out.println("future1线程e" + Thread.currentThread().getId());
//            return 10 / 4;
//        }, service);
//        CompletableFuture<Object> future2 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("future2线程s" + Thread.currentThread().getId());
//
//            System.out.println("future2线程e" + Thread.currentThread().getId());
//            return "hello";
//        }, service);

        //runAfterBoth:无参无返回
//        future1.runAfterBoth(future2,()->{
//            System.out.println("任务3");
//        });
        //thenAcceptBoth有参无返回
//        future1.thenAcceptBoth(future2,(f1,f2)->{
//            System.out.println("任务三开始,f1="+f1+ ",f2="+f2);
//        });
        //thenCombine有参有返回
//        CompletableFuture<String> stringCompletableFuture = future1.thenCombine(future2, (f1, f2) -> {
//            return f1 + ":" + f2;
//        });

        /**
         * 两个任务组合，只要有一个完成
         * runAfterEitherAsync：不感知结果，并且无返回值
         * acceptEitherAsync: 感知结果，无返回值
         * applyToEitherAsync: 感知结果，有返回值
         */
//        future1.runAfterEitherAsync(future2,()-> System.out.println("任务三"),service);
//        future1.acceptEitherAsync(future2, System.out::println,service);
//        CompletableFuture<String> stringCompletableFuture = future1.applyToEither(future2, f -> f.toString() + "hh");

        /**
         *  多任务组合
         * allOf：等待所有任务完成
         * anyOf：只要有一个任务完成
         */
        CompletableFuture<String> png = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询图片");
            return "a.png";
        }, service);
        CompletableFuture<String> attr = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询属性");
            return "黑色+256G";
        }, service);
        CompletableFuture<String> decp = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询详情");
            return "小米";
        }, service);
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(png, attr, decp);
        voidCompletableFuture.get();//等待结果


        System.out.println("main结束,");
    }

    public static class Thread01 extends Thread{
        @Override
        public void run() {
            System.out.println("thread01执行"+Thread.currentThread().getId());

            System.out.println("thread01结束"+Thread.currentThread().getId());
        }
    }

    public static class Runable01 implements Runnable{

        @Override
        public void run() {
            System.out.println("Runable01执行"+Thread.currentThread().getId());

            System.out.println("Runable01结束"+Thread.currentThread().getId());
        }
    }

    public static class Callable01 implements Callable<Integer>{

        @Override
        public Integer call() throws Exception {
            System.out.println("Callable01执行"+Thread.currentThread().getId());

            System.out.println("Callable01结束"+Thread.currentThread().getId());
            return 10 / 2;
        }
    }
}
