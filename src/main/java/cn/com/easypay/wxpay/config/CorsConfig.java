/**  
 * All rights Reserved, Designed By www.tydic.com
 * @Title: CorsConfig.java   
 * @Package: cn.com.easypay.config   
 * @Description: 
 * @author: wangtao 
 * @date: 2020年2月18日 下午6:29:22
 */
package cn.com.easypay.wxpay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @ClassName: CorsConfig
 * @Description:
 * @author: wangtao
 * @date: 2020年2月18日 下午6:29:22
 */
@Configuration
public class CorsConfig {
	private CorsConfiguration buildConfig() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedOrigin("*"); // 1允许任何域名使用
		corsConfiguration.addAllowedHeader("*"); // 2允许任何头
		corsConfiguration.addAllowedMethod("*"); // 3允许任何方法（post、get等）
		return corsConfiguration;
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", buildConfig()); // 4
		return new CorsFilter(source);
	}
}
