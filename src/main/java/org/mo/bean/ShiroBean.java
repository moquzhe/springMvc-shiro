package org.mo.bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import com.mysql.jdbc.PreparedStatement;

/**
 * realm 查询数据库，并且得到正确的数据
 * @author mo
 */
public class ShiroBean extends AuthorizingRealm{

	/**
	 * 认证实例
	 * 1、doGetAuthenticationInfo 获取认证信息，如果数据库没有数据，则返回null，如果得到正确的用户名和密码，返回指定类型的对象
	 * 
	 * 2、AuthenticationInfo 可以使用SimpleAuthenticationInfo实现类，封装给你正确的用户名和密码
	 * 
	 * 3、token参数：就是我们需要认证的token
	 *
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// TODO Auto-generated method stub
		// 1、 将token转换成UsernamePasswordToken
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		
		// 2、获取用户名即可
		String userName = upToken.getUsername();
		
		SimpleAuthenticationInfo info = null;
		
		// 3、根据用户名查询数据库
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/huazhong?useUnicode=true&characterEncoding=UTF-8";
			Connection conn = DriverManager.getConnection(url, "root", "admin");
			PreparedStatement ps = (PreparedStatement) conn.prepareStatement("select * from hz_user where user_name = ?");
			ps.setString(1, userName);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				// 4、 查询到了，封装查询结果，返回给我们的调用者
				Object principal = userName; // 用户名
				Object credentials = rs.getString(3); // 数据库中的密码
				String realmName = this.getName();
				String saltString = String.valueOf(rs.getInt(1));
				
				//info = new SimpleAuthenticationInfo(principal, sh, realmName);
				
				// 盐值 使用自增id，或者在数据库中设置一个注册时间的毫秒级数据来作为盐值。
				ByteSource salt = ByteSource.Util.bytes(saltString);
				
				// 将数据库中的密码进行加密比对（存到数据库中的数据就是这个值）
				// 在applicationContext.xml中有配置前台传过来的密码进行加密，然后跟数据库里面加密密码进行比对。
				SimpleHash sh = new SimpleHash("MD5", credentials, salt, 1024); 
				
				// 使用盐值加密
				info = new SimpleAuthenticationInfo(principal, sh, salt,realmName);
			}else{
				// 5、如果没有查询到，抛出一个异常
				throw new AuthenticationException();
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return info;
	}

	/**
	 * 授权实例
	 * */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// 返回值：AuthorizationInfo，封装获取的用户对应的所有角色，SimpleAuthorization
		// 参数列表：PrincipalCollection 登录的身份，登录的角色
		SimpleAuthorizationInfo info = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/huazhong?useUnicode=true&characterEncoding=UTF-8";
			Connection conn = DriverManager.getConnection(url, "root", "admin");
			PreparedStatement ps = (PreparedStatement) conn.prepareStatement("select * from hz_user where user_name = ?");
			
			String userName = principals.toString();
			ps.setString(1, userName);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				// 4、 查询到了，封装查询结果，返回给我们的调用者
				HashSet<String> hs = new HashSet<String>();
				hs.add(rs.getString(5));
				info = new SimpleAuthorizationInfo(hs);
			}else{
				// 5、如果没有查询到，抛出一个异常
				throw new AuthenticationException();
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return info;
	}

}
