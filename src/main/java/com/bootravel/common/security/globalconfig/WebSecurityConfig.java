package com.bootravel.common.security.globalconfig;


import com.bootravel.common.constant.RoleConstants;
import com.bootravel.common.security.jwt.config.JwtAuthenticationEntryPoint;
import com.bootravel.common.security.jwt.config.JwtRequestFilter;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${security.master:#{'/authenticate'}}")
    private String[] MASTER_PATTERN;

    @Value("${security.domain:#{null}}")
    private String[] DOMAIN_PATTERN;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private UserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().csrf().disable()
                // dont authenticate this particular request
                .authorizeRequests().antMatchers(MASTER_PATTERN).permitAll()

                //Supper Admin
                .antMatchers("/marketing/update-status").hasAnyAuthority(RoleConstants.ADMIN)
                .antMatchers("/marketing/search-marketing").hasAnyAuthority(RoleConstants.ADMIN)
                .antMatchers("/marketing/create-marketing").hasAnyAuthority(RoleConstants.ADMIN)
                .antMatchers("/marketing/get-marketing/{id}").hasAnyAuthority(RoleConstants.ADMIN)
                .antMatchers("/business-admin/create-business-admin").hasAnyAuthority(RoleConstants.ADMIN)
                .antMatchers("/business-admin/search-business-admin").hasAnyAuthority(RoleConstants.ADMIN)
                .antMatchers("/business-admin/update-status").hasAnyAuthority(RoleConstants.ADMIN)
                .antMatchers("/business-admin/get-ba/{id}").hasAnyAuthority(RoleConstants.ADMIN)
                .antMatchers("/statistic/system/data").hasAnyAuthority(RoleConstants.ADMIN, RoleConstants.MARKETING)
                .antMatchers("/statistic/system/total-income").hasAnyAuthority(RoleConstants.ADMIN, RoleConstants.MARKETING)
                .antMatchers("/statistic/system/total-booking-weekly").hasAnyAuthority(RoleConstants.ADMIN, RoleConstants.MARKETING)
                .antMatchers("/registered-user/get-registered-user").hasAnyAuthority(RoleConstants.ADMIN)
                .antMatchers("/registered-user/get-registered-user-detail/{userId}").hasAnyAuthority(RoleConstants.ADMIN)
                .antMatchers("/registered-user/get-registered-user/{id}").hasAnyAuthority(RoleConstants.ADMIN)
                .antMatchers("/registered-user/update-status").hasAnyAuthority(RoleConstants.ADMIN)
                //Ba admin
                .antMatchers("/statistic/business-admin/data").hasAnyAuthority(RoleConstants.BUSINESS_ADMIN)
                .antMatchers("/statistic/business-admin/total-income").hasAnyAuthority(RoleConstants.BUSINESS_ADMIN)
                .antMatchers("/statistic/business-admin/total-booking-weekly").hasAnyAuthority(RoleConstants.BUSINESS_ADMIN)
                .antMatchers("/hotel/create-hotel").hasAnyAuthority(RoleConstants.BUSINESS_ADMIN)
                .antMatchers("/hotel/search-hotel").hasAnyAuthority(RoleConstants.BUSINESS_ADMIN)
                .antMatchers("/hotel/update-hotel").hasAnyAuthority(RoleConstants.BUSINESS_ADMIN)
                .antMatchers("/hotel/update-status").hasAnyAuthority(RoleConstants.BUSINESS_ADMIN)
                .antMatchers("/business-owner/create-bo").hasAnyAuthority(RoleConstants.BUSINESS_ADMIN)
                .antMatchers("/business-owner/search-business-owner").hasAnyAuthority(RoleConstants.BUSINESS_ADMIN)
                .antMatchers("/business-owner/update-status").hasAnyAuthority(RoleConstants.BUSINESS_ADMIN)
                .antMatchers("/business-owner/get-bo/{id}").hasAnyAuthority(RoleConstants.BUSINESS_ADMIN)
                .antMatchers("/business-owner/search-bo-my-manager").hasAnyAuthority(RoleConstants.BUSINESS_ADMIN)

                //Bo
                .antMatchers("/statistic/business-owner/data").hasAnyAuthority(RoleConstants.BUSINESS_OWNER)
                .antMatchers("/statistic/business-owner/total-income").hasAnyAuthority(RoleConstants.BUSINESS_OWNER)
                .antMatchers("/statistic/business-owner/total-booking-weekly").hasAnyAuthority(RoleConstants.BUSINESS_OWNER)
                .antMatchers("/management-staff/create-staff").hasAnyAuthority(RoleConstants.BUSINESS_OWNER)
                .antMatchers("/management-staff/get-staff/{id}").hasAnyAuthority(RoleConstants.BUSINESS_OWNER)
                .antMatchers("/management-staff/search-staff").hasAnyAuthority(RoleConstants.BUSINESS_OWNER)
                .antMatchers("/management-staff/update-status").hasAnyAuthority(RoleConstants.BUSINESS_OWNER)

                // Staff
                .antMatchers("/room/**").hasAnyAuthority(
                        RoleConstants.BUSINESS_OWNER,
                        RoleConstants.BOOKING_STAFF
                )
                .antMatchers("/booking-room/update/check-qr/check-out").hasAnyAuthority(RoleConstants.BOOKING_STAFF)
                .antMatchers("/booking-room/update/check-qr/check-in").hasAnyAuthority(RoleConstants.BOOKING_STAFF)
                .antMatchers("/manage-booking/get-booking/{id}").hasAnyAuthority(RoleConstants.BOOKING_STAFF)
                .antMatchers("/manage-booking/search-booking").hasAnyAuthority(RoleConstants.BOOKING_STAFF)
                .antMatchers("/manage-booking/update-status-booking/{id}").hasAnyAuthority(RoleConstants.BOOKING_STAFF)

                .antMatchers("/transaction-be/get-transaction/{id}").hasAnyAuthority(RoleConstants.TRANSACTION_STAFF)
                .antMatchers("/transaction-be/search-transaction").hasAnyAuthority(RoleConstants.TRANSACTION_STAFF)

                //Marketing
                .antMatchers("/marketing/update-status/{id}").hasAnyAuthority(RoleConstants.MARKETING)
                .antMatchers("/banner/create").hasAnyAuthority(RoleConstants.MARKETING)
                .antMatchers("/banner/update-status/{id}").hasAnyAuthority(RoleConstants.MARKETING)
                .antMatchers("/transaction-system/get-transaction/{id}").hasAnyAuthority(RoleConstants.MARKETING)
                .antMatchers("/transaction-system/search-transaction").hasAnyAuthority(RoleConstants.MARKETING)
                .antMatchers("/staff-marketing/send-mail-marketing").hasAnyAuthority(RoleConstants.MARKETING)

                //Api public
                        .antMatchers("/api/insert-user").permitAll()
                        .antMatchers("/api/get-registered-user").permitAll()
                        .antMatchers("/get-account-user").permitAll()
                        .antMatchers("/api/get-all-users").permitAll()
                        .antMatchers("/registered-users/update-user/{userId}").permitAll()
                        .antMatchers("/api/list-province").permitAll()
                        .antMatchers("/api/list-district").permitAll()
                        .antMatchers("/api/list-ward").permitAll()
                        .antMatchers("/api/get-province/{id}").permitAll()
                        .antMatchers("/api/get-district/{id}").permitAll()
                        .antMatchers("/api/get-ward/{id}").permitAll()
                        .antMatchers("/registered-users/login").permitAll()
                        .antMatchers("/registered-users/process-register").permitAll()
                        .antMatchers("/registered-users/verify").permitAll()
                        .antMatchers("/registered-users/forgot-password").permitAll()
                        .antMatchers("/registered-users/change-password").permitAll()
                        .antMatchers("/registered-users/update-profile").permitAll()
                        .antMatchers("/login").permitAll()
                        .antMatchers("/registered-users/princial").permitAll()
                        .antMatchers("/room/search-room").permitAll()
                        .antMatchers("/registered-users/get-profile").permitAll()
                        .antMatchers("/room-users/get-room-details").permitAll()
                        .antMatchers("/room-users/get-room/hotelId").permitAll()
                        .antMatchers("/room-users/get-service-room").permitAll()
                        .antMatchers("/room-users/get/check-room").permitAll()
                        .antMatchers("/booking-room/create").permitAll()
                        .antMatchers("/booking-room/history").permitAll()
                        .antMatchers("/hotel-home/search-hotel").permitAll()
                        .antMatchers("/hotel-home/get-service-hotel").permitAll()
                        .antMatchers("/hotel-home/search-suggest-hotel").permitAll()
                        .antMatchers("/hotel-home/search-suggest-location").permitAll()
                        .antMatchers("/promotion/check-price-promotion").permitAll()
                        .antMatchers("/api/payment/queryTransaction").permitAll()
                        .antMatchers("/hotel/get-hotel/{id}").permitAll()
                        .antMatchers("/banner/list").permitAll()
                        .antMatchers("/promotion/public-list").permitAll()
                        .antMatchers("/service/list-service-hotel", "/service/list-service-room").permitAll()
                // all other requests need to be authenticated
                .anyRequest().authenticated().and().
                // make sure we use stateless session; session won't be used to
                // store user's state.
                        exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration configuration = new CorsConfiguration();
//        if (Objects.nonNull(DOMAIN_PATTERN)) {
//            configuration.setAllowedOrigins(Arrays.asList(DOMAIN_PATTERN));
//        }
//        configuration.applyPermitDefaultValues();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

}

