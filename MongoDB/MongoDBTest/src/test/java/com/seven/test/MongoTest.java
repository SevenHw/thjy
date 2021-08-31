package com.seven.test;

import com.seven.domian.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class MongoTest {
    /**
     * 1.注入MongoTemplate
     * 2.调用其方法完成数据的crud
     */
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存
     */
    @Test
    public void testSave() {
        Person person = new Person();
        person.setName("罗列");
        person.setAge(20);
        person.setAddress("湖南长沙");
        mongoTemplate.save(person);
    }

    /**
     * 查询
     */
    @Test
    public void testFindAll() {
        List<Person> list = mongoTemplate.findAll(Person.class);
        for (Person person : list) {
            System.out.println(person);
        }
    }

    /**
     * 条件查询
     */
    @Test
    public void testFind() {
        //1.创建Criteria对象,并设置查询条件
        Criteria criteria = Criteria.where("myname").is("罗列");//is 相当于sql语句中的=
        //根据Criteria创建Query对象
        Query query = Query.query(criteria);
        //查询
        List<Person> list = mongoTemplate.find(query, Person.class);
        for (Person person : list) {
            System.out.println(person);
        }
    }

    /**
     * 分页查询
     */
    @Test
    public void testFindPage() {
        int page = 1;
        int size = 3;
        //1.创建Criteria对象,并设置查询条件
        Criteria criteria = Criteria.where("age").lt(50);//is 相当于sql语句中的=
        //根据Criteria创建Query对象
        Query query = Query.query(criteria)
                .skip((page - 1) * size)//从第几条开始查
                .limit(size)//每页查询条数
                .with(Sort.by(Sort.Order.desc("age")));
        //查询
        List<Person> list = mongoTemplate.find(query, Person.class);
        for (Person person : list) {
            System.out.println(person);
        }
    }

    /**
     * 更新
     */
    @Test
    public void testUpdate() {
        //1.创建Query对象
        Query query = Query.query(Criteria.where("myname").is("罗列"));
        //2.设置需要更新的数据内容
        Update update = new Update();
        update.set("myname", "luolie");
        //调用放法
        mongoTemplate.updateFirst(query, update, Person.class);
    }

    /**
     * 删除
     */
    @Test
    public void testDelete() {
        //1.创建Query对象
        Query query = Query.query(Criteria.where("myname").is("luolie"));
        //2.调用方法
        mongoTemplate.remove(query, Person.class);
    }
}
