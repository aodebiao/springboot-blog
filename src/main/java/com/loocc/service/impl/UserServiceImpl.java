package com.loocc.service.impl;

import com.loocc.dao.UserRepository;
import com.loocc.domain.Role;
import com.loocc.domain.User;
import com.loocc.service.UserService;
import com.loocc.util.Const;
import com.loocc.util.StringUtil;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleServiceImpl roleService;

    @Override
    public User checkUser(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, new SimpleHash(Const.HASH_ALGORITHM_NAME, password,
                username,
                Const.HASH_ITERATIONS).toString());
        return user;
    }

    @Override
    public User findByUserName(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Page<User> searchUser(String username, Pageable pageable) {

        return userRepository.searchUser("%" + username + "%", pageable);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    @Override
    public User save(User user) {
        //设置头像
        if (user.getAvatar() == null || StringUtil.isEmpty(user.getAvatar())) {
            if (user.getGender() == 0) {
                user.setAvatar(Const.GIRL_AVATAR);
            } else {
                user.setAvatar(Const.BOY_AVATAR);
            }
        }
        user.setStatus(true);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        User dbUser = userRepository.findByUsername(user.getUsername());

        if(dbUser == null || dbUser.getRole() == null){//非user用户扫码登录后，会重置为user
                user.setRole(roleService.findByRoleName("user"));
        }else{
            user.setRole(dbUser.getRole());
        }

        if (user.getOpenId() != null && user.getId() == null) {//qq登录的，未绑定用户。设置默认密码
            user.setPassword(new SimpleHash(Const.HASH_ALGORITHM_NAME, Const.DEFAULT_PASSWORD, user.getUsername(),
                    Const.HASH_ITERATIONS).toString());
        } else if(user.getOpenId() != null && (user.getPassword() == null || user.getPassword().equals(""))){//登录后再绑定qq
            user.setPassword(userRepository.findByUsername(user.getUsername()).getPassword());
        }else if((user.getId() != null && user.getPassword().trim().equals(""))){//未填写密码字段，还是原密码
                user.setPassword(dbUser.getPassword());
        }else if(user.getId() != null && !user.getPassword().trim().equals("")){//修改密码
            user.setPassword(new SimpleHash(Const.HASH_ALGORITHM_NAME,user.getPassword(),user.getUsername(),Const.HASH_ITERATIONS).toString());
        }
        else if(user.getId() == null && user.getPassword().trim().length() < 32){//注册用户
            user.setPassword(new SimpleHash(Const.HASH_ALGORITHM_NAME,user.getPassword(),user.getUsername(),Const.HASH_ITERATIONS).toString());
        }

        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.get();

    }

    @Override
    public Set<String> findRoleNameByUsername(String username) {
        Set<String> rname = userRepository.findRoleNameByUsername(username);
        return rname.size() == 1 ? rname : null;
    }

    @Override
    public List<User> findTop(Pageable pageable) {
        return userRepository.findTop(pageable);
    }

    @Override
    public Page<User> findAllByRoleNot(Pageable pageable) {
        Page<User> userPage = userRepository.findAllByRoleNot(pageable);
        //把用户密码置为空，再传到前台
        userPage.get().forEach(user -> {
            user.setPassword(null);
        });
        return userPage;
    }

    @Transactional
    @Override
    public int updateStatus(boolean status, Long id) {
        return userRepository.updateStatus(status, id);
    }

    @Transactional
    @Override
    public int updateRole(Role role, Long userId) {

        return userRepository.updateRole(role, userId);
    }

    @Transactional
    @Override
    public User updateUser(User user) {
        if (user.getPassword() != null && !user.getPassword().trim().equals("")) {//修改密码
            user.setPassword(new SimpleHash(Const.HASH_ALGORITHM_NAME, user.getPassword(), user.getUsername(),
                    Const.HASH_ITERATIONS).toString());
        } else {
            User dbUser = userRepository.findByUsername(user.getUsername());
            user.setPassword(dbUser.getPassword());
        }
        return userRepository.save(user);
    }

    @Override
    public User findByOpenId(String openId) {
        return userRepository.findByOpenId(openId);
    }

    @Transactional
    @Override
    public void unBind(Long userId) {
        userRepository.unBind(userId);
    }


}
