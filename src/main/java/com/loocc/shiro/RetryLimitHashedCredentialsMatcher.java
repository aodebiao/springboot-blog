package com.loocc.shiro;

import com.loocc.util.Const;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.SimpleHash;

import java.util.Objects;


/**
 * 自定义密码比较器
 * 后续完善
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {
    //token封装前台的信息，info封装数据库中的信息
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        UsernamePasswordToken uToken = (UsernamePasswordToken) token;
        String uTokenPassword = new String(uToken.getPassword());
        String dbPassword = (String) info.getCredentials();//数据库中的密码
        if (uTokenPassword.length() == 32) {//qq登录
            return Objects.equals(uTokenPassword, dbPassword);
        }
        uTokenPassword = new SimpleHash(Const.HASH_ALGORITHM_NAME, uTokenPassword,
                ((UsernamePasswordToken) token).getUsername(),
                Const.HASH_ITERATIONS).toString();
        return Objects.equals(uTokenPassword, dbPassword);
    }

    public static void main(String[] args) {
        String username = "Garua";
        String md5 = new SimpleHash("MD5", "111", username, 10).toString();
        System.out.println(md5);
    }
}
