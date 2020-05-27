package com.loocc.dao;


import com.loocc.domain.Role;
import com.loocc.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByUsernameAndPassword(String username, String password);
    @Query(value = "select r.name from Role r,User u where u.role.id = r.id and u.username =:username")
    Set<String> findRoleNameByUsername(String username);

    @Query(value = "select u from User  u")
    List<User> findTop(Pageable pageable);

    //@Query(value = "select u from User u where u.role <> 'admin'")
    //List<User> findAllByRoleNot(Pageable pageable);

    @Query(value = "select u from User u")
    Page<User> findAllByRoleNot(Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "update User u set u.status =?1 where u.id  =?2")
    int updateStatus(boolean status,Long id);

    @Transactional
    @Modifying
    @Query(value = "update User u set u.role = ?1 where u.id =?2")
    int updateRole(Role role, Long userId);

    @Query(value = "select u from User u where u.username like ?1")
    Page<User> searchUser(String username,Pageable pageable);

    /**
     * 根据openid查找用户
     */

    @Query(value = "select u from User u where u.openId = ?1")
    User findByOpenId(String openId);

    @Query(value = "update User u set u.openId = null where u.id = ?1")
    @Modifying
    @Transactional
    void unBind(Long userId);
}
