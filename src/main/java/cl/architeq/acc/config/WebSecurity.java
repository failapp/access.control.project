package cl.architeq.acc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // en esta etapa se permitira acceso completo a ruta de endpoint ..
        // jwt se aplicara en proxima version ..
        // se validaran credenciales de acceso a servicios en recurso rest correspondiente ..

        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/ws/**").permitAll();

                /*
                .authorizeRequests()
                .antMatchers("/public/**").permitAll()
                .antMatchers("/private/admin/**").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/private/**").authenticated()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .logout()
                .permitAll();
                 */
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin").password("admin").roles("ADMIN").and()
                .withUser("jona").password("1234").roles("USER");
    }


}
