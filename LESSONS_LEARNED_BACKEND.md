1. Global Directives (High Priority)
- [D-001] Spring OpenAPI : Always check Spring Boot and SpringDoc compatibility matrix when upgrading Spring Boot versions.Add version compatibility testing to CI/CD pipeline. SpringDoc OpenAPI compatibility reference:
Spring Boot 3.0.x - 3.2.x: SpringDoc 2.0.x - 2.3.x
Spring Boot 3.3.x - 3.5.x: SpringDoc 2.5.x - 2.7.x+

2. Execution Log (Chronological)  
  - **ID:2025-12-04_01**
    Swagger UI was completely non-functional. OpenAPI documentation could not be generated. API testing and documentation tools were unavailable
	Outcome: FAILURE -> FIXED.
	ERROR/Observation: SpringDoc OpenAPI Version Incompatibility with Spring Boot 3.5.0.
	Root Cause: SpringDoc OpenAPI version 2.3.0 is incompatible with Spring Boot 3.5.0. The ControllerAdviceBean constructor signature changed between Spring Framework versions, causing a method not found error.
	Corrective Action Taken: 
	Solution
		Upgraded SpringDoc OpenAPI version in parent POM:

		<!-- Before -->
		<springdoc.version>2.3.0</springdoc.version>

		<!-- After -->
		<springdoc.version>2.7.0</springdoc.version>
		Rebuilt configuration-service with updated dependency:

		cd configuration-service && mvn clean install -DskipTests
		Killed old service process and restarted with new version

		Verified endpoints:

		OpenAPI JSON: http://localhost:8082/api/v1/api-docs (200 OK)
		Swagger UI: http://localhost:8082/swagger-ui/index.html (200 OK)

  
