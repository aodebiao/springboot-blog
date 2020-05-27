package com.loocc;

import com.loocc.dao.BlogRepository;

import com.loocc.dao.RoleRepository;
import com.loocc.dao.TagRepository;
import com.loocc.dao.UserRepository;
import com.loocc.domain.*;
import com.loocc.esdao.BlogEsRepository;
import com.loocc.essevice.impl.BlogEsServiceImpl;
import com.loocc.service.CommentService;
import com.loocc.service.RoleService;
import com.loocc.service.UserService;
import com.loocc.util.Const;
import com.loocc.util.VerifyCode;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.awt.image.BufferedImage;
import java.util.*;

@SpringBootTest
@EnableElasticsearchRepositories(basePackages = {"com.loocc.esdao"})
@EnableJpaRepositories(basePackages = "com.loocc.dao")

public class BlogApplicationTests {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BlogRepository blogRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    BlogEsRepository blogEsRepository;
    @Autowired
    RoleService roleService;

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    BlogEsServiceImpl esService;
    @Autowired
    ElasticsearchTemplate template;
    @Autowired
    BlogEsServiceImpl blogEsService;

    @Autowired
    CommentService commentService;


    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void testUpdateStatus(){
      userService.updateStatus(false,1l);
    }

    @Test
    public void testSaveUser(){
        User user = new User();
        user.setId(7l);
        user.setPassword(1111111+"");
        user.setCheck_password(1111111+"");
        user.setCreateTime(new Date());
        user.setGender(1);
        user.setEmail("423fds@qq.com");
        userService.save(user);
    }

    @Test
    public void test1(){
        Role role = roleService.findByRoleName("admin");
        System.out.println(role);
    }
    //@Test
    //public void testView(){
    //    blogRepository.updateViews(1l);
    //}

    @Test
    public void printPassword(){
        System.out.println(new SimpleHash(Const.HASH_ALGORITHM_NAME,"ZHGZ999","呱呱呱",
                Const.HASH_ITERATIONS).toString());
   //userRepository.findAll().forEach(System.out::println);

    }

    //@Test
    //public void testBlog(){
    //    List<Blog> all = blogRepository.findAll();
    //    all.forEach(System.out::println);
    //}

    @Test
    public void testVerifyCode(){
        System.out.println(VerifyCode.drawRandomText(10,10,new BufferedImage(10,10,1)));
    }

    @Test
    public void testES(){
        //blogEsRepository.save(new EsBlog("1","测试es","嘿嘿哈哈跟趾步态上看到了","真的好啊，nice"));
    }


    @Test
    public void testESGet(){
        blogEsRepository.findAll().forEach(System.out::println);

    }

    @Test
    public void testFind(){
        Page<EsBlog> result = blogEsRepository.findByTitleOrContentOrDescription("功能","功能","功能", PageRequest.of(0,
                4));
        //Iterable<EsBlog> result = blogEsRepository.findAll();
        result.getContent().forEach(System.out::println);
    }

    @Test
    public void testHighLight()throws Exception{
        String[] searchField = {"title","content","description"};
        //if(template == null){
        //    return;
        //}
        String query = "测试";
        //Page<EsBlog> esBlogs = esService.findByTitleOrContentOrDescription(query, query, query,
        //        PageRequest.of(0,10));
        Page<EsBlog> esBlogs = esService.highLightQuery("ASC", "createTime", searchField, "测试", PageRequest.of(0, 2));
        System.out.println(esBlogs.getContent());
    }

    @Test
    public void testHighLight2(){
        Page<EsBlog> blogPage = blogEsRepository.findByTitleOrContentOrDescription("测", "测", "测", PageRequest.of(0, 2));
        blogPage.getContent().forEach(System.out::println);
        //List<EsBlog> esBlogs = blogEsService.search("测");
        //esBlogs.forEach(System.out::println);
    }

    @Test
    public void testSaveTags(){

        List<Tag> list = new ArrayList<>();
        list.add(new Tag("哈哈"));
        list.add(new Tag("嘿嘿"));
        list.add(new Tag(" 标配"));
        tagRepository.insertTags(list,jdbcTemplate);

    }



    @Test
    public void testJPA(){
        List<Comment> comments = commentService.listCommentByBlogId(1l);
        System.out.println(comments);
        commentService.saveComment(new Comment());
        comments =commentService.listCommentByBlogId(1l);
        System.out.println(comments);
    }

    @Test
    public void test8(){
        Map map = new HashMap<>();
        int i = 10__8;
        System.out.println(i);

    }

    @Test
    public void testRedis(){
        //redisTemplate.opsForValue().set
        //redisTemplate.opsForList()

    }


}
