package com.xiaomi.thain.server.config

import com.alibaba.fastjson.JSON
import com.xiaomi.thain.common.entity.ApiResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author miaoyu
 */
@EnableWebSecurity
class WebSecurityConfig(private val customOauth2UserServiceImpl: OAuth2UserService<OidcUserRequest, OidcUser>,
                        @Value("\${thain.login.source}") authenticationProviderName: String,
                        applicationContext: ApplicationContext
) : WebSecurityConfigurerAdapter() {
    private val authenticationProvider: AuthenticationProvider = applicationContext.getBean(authenticationProviderName) as AuthenticationProvider
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(authenticationProvider)
    }

    fun apiAuthenticationFailureHandler(): AuthenticationFailureHandler {
        return AuthenticationFailureHandler { _, response, exception ->
            response.contentType = UTF8_JSON_TYPE
            response.writer.write(JSON.toJSONString(ApiResult.builder()
                    .status(400)
                    .message(exception.message!!).build()))
        }
    }

    fun apiAuthenticationSuccessHandler(): AuthenticationSuccessHandler {
        return AuthenticationSuccessHandler { _, response, authentication ->
            response.contentType = UTF8_JSON_TYPE
            val authorities = authentication.authorities
            val json = JSON.toJSONString(ApiResult.success(authorities))
            response.writer.write(json)
        }
    }

    fun apiAuthenticationEntryPoint(loginPage: String?): AuthenticationEntryPoint {
        return object : LoginUrlAuthenticationEntryPoint(loginPage) {
            @Throws(IOException::class)
            override fun commence(request: HttpServletRequest, response: HttpServletResponse,
                                  authException: AuthenticationException) {
                response.contentType = UTF8_JSON_TYPE
                response.writer.write(JSON.toJSONString(ApiResult.builder().status(401).message("Logon expires, please refresh the page and log in again").build()))
            }
        }
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.exceptionHandling()
                .authenticationEntryPoint(apiAuthenticationEntryPoint("/"))
                .and()
                .authorizeRequests()
                .antMatchers("/api/login", "x5/**", "/api/cas/redirect/login").permitAll()
                .antMatchers("/api/**")
                .authenticated()
                .and()
                .csrf().disable()
                .formLogin()
                .loginProcessingUrl("/api/login")
                .failureHandler(apiAuthenticationFailureHandler())
                .successHandler(apiAuthenticationSuccessHandler())
                .usernameParameter("userId").and()
        thirdConfig(http)
    }

    /**
     * 第三方登录配置
     *
     * @param http HttpSecurity
     */
    @Throws(Exception::class)
    private fun thirdConfig(http: HttpSecurity) {
        http.oauth2Login()
                .loginPage("/")
                .defaultSuccessUrl("/", true)
                .authorizationEndpoint()
                .baseUri("/api/oauth2/authorization")
                .and().userInfoEndpoint().oidcUserService(customOauth2UserServiceImpl)
                .and().and()
                .sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
    }

    companion object {
        private const val UTF8_JSON_TYPE = "application/json; charset=utf-8"
    }

}
