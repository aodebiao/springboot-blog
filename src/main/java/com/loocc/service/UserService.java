package com.loocc.service;

import com.loocc.domain.Role;
import com.loocc.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Set;

public interface UserService {

    User checkUser(String username, String password);

    User findByUserName(String username);
    Page<User>searchUser(String username,Pageable pageable);//为了页面共用，所以即使只有一条数据也返回Page<User>
   User findByEmail(String email);

    User save(User user);

    User findById(Long id);

    Set<String> findRoleNameByUsername(String username);

    List<User> findTop(Pageable pageable);

    Page<User> findAllByRoleNot(Pageable pageable);

    int updateStatus(boolean status,Long id);

    int updateRole(Role role, Long userId);
    User updateUser(User user);

    User findByOpenId(String openId);

    void unBind(Long userId);

}