package com.arccc.gulimall.product.web;

import com.arccc.gulimall.product.entity.CategoryEntity;
import com.arccc.gulimall.product.service.CategoryService;
import com.arccc.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @GetMapping({"/","index.html"})
    public String index(Model model, HttpSession session){
        //TODO 查出一级分类
        List<CategoryEntity> list = categoryService.getLevel1Categorys();
        model.addAttribute("categorys",list);
        String id = session.getId();
        System.out.println("indexSessionId=>"+id);
        Object loginUser = session.getAttribute("loginUser");
        System.out.println(loginUser);
        return "index";
    }
    @ResponseBody
    @GetMapping("index/catalog.json")
    public Map<String ,List<Catelog2Vo>> getCatelogJson(){
        Map<String ,List<Catelog2Vo>> map = null;

        map = categoryService.getCatelogJson();

        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        //通过getlock获取锁，只要名字一样，所有人就共用同一把锁
        RLock mylock = redissonClient.getLock("mylock");
        //加锁
//        mylock.lock();
        //推荐
        mylock.lock(10, TimeUnit.SECONDS);
        try {
            System.out.println("加锁成功,执行业务");
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            mylock.unlock();
        }
        return "hello";
    }

    /**
     * 测试读写锁，读
     * @return
     */
    @ResponseBody
    @GetMapping("/read")
    public String read(){

        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        RReadWriteLock readWriteLock_ = redissonClient.getReadWriteLock("readWriteLock ");
        //加锁
        RLock rLock = readWriteLock_.readLock();
        rLock.lock();
        String s = "";
        try {
            s = ops.get("uuid");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            //释放
            rLock.unlock();
        }


        return s;
    }

    /**
     * 测试书写锁 ，写
     * @return
     */
    @ResponseBody
    @GetMapping("/write")
    public String write(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        RReadWriteLock readWriteLock_ = redissonClient.getReadWriteLock("readWriteLock ");
        //加锁
        RLock rLock = readWriteLock_.writeLock();
        rLock.lock();
        String s = UUID.randomUUID().toString();

        try {
            ops.set("uuid",s);
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            //释放
            rLock.unlock();
        }


        return s;
    }



}
