package com.itmsd.medical.configuration;

import java.util.Arrays;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.itmsd.medical.security.AuthTokenFilter;
import com.itmsd.medical.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
    private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private DataSource dataSource;
	@Value("${spring.queries.users-query}")
	private String usersQuery;
	@Value("${spring.queries.userss-query}")
	private String userssQuery;
	@Value("${spring.queries.roles-query}")
	private String rolesQuery;
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		authenticationManagerBuilder.jdbcAuthentication()
		.usersByUsernameQuery(usersQuery)
		.authoritiesByUsernameQuery(rolesQuery)
		.dataSource(dataSource).passwordEncoder(passwordEncoder());

	}

	 @Bean
	    public CorsConfigurationSource corsConfigurationSource() {
	        CorsConfiguration configuration = new CorsConfiguration();
	        configuration.setAllowCredentials(true);
	        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000","https://my-health-network.netlify.app","https://my-health-network.be","http://my-health-network.be","https://www.my-health-network.be","http://www.my-health-network.be"));
	        configuration.setAllowedMethods(Arrays.asList("*"));
	        configuration.setAllowedHeaders(Arrays.asList("*"));
	        configuration.addAllowedOriginPattern("*");
	        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	        source.registerCorsConfiguration("/**", configuration);
	        return source;
	    }
	 @Override
		protected void configure(HttpSecurity http) throws Exception {
			http.cors().and().csrf().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.authorizeRequests().antMatchers("/auth/**","/search/**","/schedule/**","/chat/**","/users/**").permitAll().and()
				.authorizeRequests().antMatchers("/api/test/mod","/rendezvous/AllRdvClient").hasAuthority("client").and()
				.authorizeRequests().antMatchers("/rendezvous/AllRdvPerso","/rendezvous/AcceptRefuse/**").hasAnyAuthority("Menuisier","peintre","electricien","paysagiste","plombier","carreleur","soudeur","admin").and()
				.authorizeRequests().antMatchers("/api/test/user","/users/**","/notifications/**","/profile/**","/rendezvous/single/**","/rendezvous/add/**").hasAnyAuthority("Menuisier","client","peintre","carreleur","electricien","paysagiste","plombier","soudeur","admin").and()
				.authorizeRequests().antMatchers("/stats/**","/admin/**","/api/test/admin").hasAuthority("admin");
			
			http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		}
}
