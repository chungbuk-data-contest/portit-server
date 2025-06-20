package org.ssafy.datacontest.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.security.web.SecurityFilterChain;
import org.ssafy.datacontest.jwt.LoginFilter;

import java.util.Optional;

@Configuration
public class SwaggerConfig {

    private final ApplicationContext applicationContext;

    @Autowired
    public SwaggerConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info()
                .version("v1.0.0")
                .title("Port-It API");

        // SecuritySecheme명
        String jwtSchemeName = "access";
        // API 요청헤더에 인증정보 포함
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        // SecuritySchemes 등록
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP) // HTTP 방식
                        .scheme("bearer")
                        .bearerFormat("JWT")); // 토큰 형식을 지정하는 임의의 문자(Optional)

        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    @Bean
    public OpenApiCustomizer customLoginEndpointOpenApi() {
        FilterChainProxy filterChainProxy = applicationContext.getBean(
                AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME,
                FilterChainProxy.class);

        return openAPI -> {
            for (SecurityFilterChain filterChain : filterChainProxy.getFilterChains()) {
                Optional<LoginFilter> loginFilter = filterChain.getFilters().stream()
                        .filter(LoginFilter.class::isInstance)
                        .map(LoginFilter.class::cast)
                        .findAny();

                if (loginFilter.isPresent()) {
                    Schema<?> schema = new ObjectSchema()
                            .addProperties("email", new StringSchema())
                            .addProperties("password", new StringSchema());

                    RequestBody requestBody = new RequestBody().content(
                            new Content().addMediaType(
                                    org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                    new MediaType().schema(schema)
                            ));

                    Operation operation = new Operation()
                            .summary("로그인")
                            .description("JWT를 발급받기 위한 로그인 API. email/password 입력 필요")
                            .requestBody(requestBody)
                            .responses(new ApiResponses()
                                    .addApiResponse("200", new ApiResponse().description("성공"))
                                    .addApiResponse("401", new ApiResponse().description("인증 실패")))
                            .addTagsItem("Auth");

                    openAPI.getPaths().addPathItem("/auth/login", new PathItem().post(operation));
                }
            }
        };
    }

    @Bean
    public GroupedOpenApi groupedOpenApi(OpenApiCustomizer customLoginEndpointOpenApi) {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/**")
                .addOpenApiCustomizer(customLoginEndpointOpenApi)
                .build();
    }
}
