
package onlineShop;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.context.annotation.Bean;
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private DataSource dataSource;
	//验证，配置
	protected void configure(HttpSecurity http) throws Exception {
		http
			//安全漏洞：
			.csrf().disable()
			.formLogin()
				.loginPage("/login")
			.and() // 连写，不用重新http;
			.authorizeRequests()
			//**regex:可以有多个区别；
			.antMatchers("/cart/**").hasAuthority("ROLE_USER") //如果要访问购物车必须是登陆过的用户。
			.antMatchers("/get*/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN") //哪个url 用哪个权限不访问；
			.antMatchers("/admin*/**").hasAuthority("ROLE_ADMIN") //商品的权限，admin能添加商品等。只有main user才能访问；
			.anyRequest().permitAll() //剩下的endpoint， 大家都可以访问；
			.and()
			.logout()
				.logoutUrl("/logout"); // logout
	}
	
	//拿到的info是否能匹配到上面的信息；
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//类似于hashtable,在系统里默认的user.
		//inMemory 好处：不用二次创建user;
		
		auth
			.inMemoryAuthentication().withUser("zoebo1225@gmail.com").password("521mljB2Y!").authorities("ROLE_ADMIN");
		//连接mySql 的database; 
		auth
			.jdbcAuthentication()
			.dataSource(dataSource)
			//怎么找用户与密码，此框架去user 表里找user's info
			//从数据库中访问用户信息，拿出来权限；
			.usersByUsernameQuery("SELECT emailId, password, enabled FROM users WHERE emailId=?")
			.authoritiesByUsernameQuery("SELECT emailId, authorities FROM authorities WHERE emailId=?");
	}

       @SuppressWarnings("deprecation")
       //把密码加密，现在存的是名文
	@Bean
	public static NoOpPasswordEncoder passwordEncoder() {
		return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
	}
	
}
