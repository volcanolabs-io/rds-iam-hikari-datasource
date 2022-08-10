package io.volcanolabs.rds;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.rds.RdsUtilities;
import software.amazon.awssdk.services.rds.model.GenerateAuthenticationTokenRequest;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.SsmClientBuilder;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

import java.net.URI;

/**
 * This Hikari datasource uses the AWS SDK to generate an auth token. That token is used as the password for an RDS
 * instance configured for IAM Roles based access.
 */
public class RdsIamHikariDataSource extends HikariDataSource {
	private static final Logger log = LoggerFactory.getLogger( RdsIamHikariDataSource.class );
	public static final String PARAMETER_STORE_REGION_KEY = "PARAMETER_STORE_REGION_KEY";

	public RdsIamHikariDataSource() {
		log.trace( "RdsIamHikariDataSource created" );
	}

	@Override
	public String getPassword() {
		log.trace( "RdsIamHikariDataSource.getPassword() called." );
		return getToken();
	}

	private String getToken() {
		var region = getRegion();

		// JDBC URL has a standard URL format, like: jdbc:postgresql://localhost:5432/test_database
		var cleanUrl = getJdbcUrl().substring( 5 );
		log.trace( "cleanUrl: {}", cleanUrl );
		var dbUri = URI.create( cleanUrl );

		GenerateAuthenticationTokenRequest authTokenRequest = GenerateAuthenticationTokenRequest.builder()
				.username( getUsername() )
				.hostname( dbUri.getHost() )
				.build();

		RdsUtilities utilities = RdsUtilities.builder()
				.credentialsProvider( DefaultCredentialsProvider.create() )
				.region( region )
				.build();

		return utilities.generateAuthenticationToken( authTokenRequest );
	}

	private Region getRegion() {
		var region = new DefaultAwsRegionProviderChain().getRegion();
		log.trace( "AWS region: {}", region );

		String regionOverrideKey = System.getenv().get( PARAMETER_STORE_REGION_KEY );
		if ( regionOverrideKey != null ) {
			SsmClientBuilder ssmClientBuilder = SsmClient.builder()
					.region( region )
					.credentialsProvider( ProfileCredentialsProvider.create() );

			try ( SsmClient ssmClient = ssmClientBuilder.build() ) {
				GetParameterRequest parameterRequest = GetParameterRequest.builder()
						.name( regionOverrideKey )
						.build();
				GetParameterResponse parameterResponse = ssmClient.getParameter( parameterRequest );
				var paramValue = parameterResponse.parameter().value();
				var overrideRegion = Region.of( paramValue );
				log.trace( "AWS region: {}", overrideRegion );

				return overrideRegion;
			}
		}
		else {
			return region;
		}
	}
}
