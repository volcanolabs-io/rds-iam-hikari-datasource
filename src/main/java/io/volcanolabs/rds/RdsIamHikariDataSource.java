package io.volcanolabs.rds;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.rds.RdsUtilities;
import software.amazon.awssdk.services.rds.model.GenerateAuthenticationTokenRequest;

import java.net.URI;

/**
 * This Hikari datasource uses the AWS SDK to generate an auth token. That token is used as the password for an RDS
 * instance configured for IAM Roles based access.
 */
public class RdsIamHikariDataSource extends HikariDataSource {
	private static final Logger log = LoggerFactory.getLogger( RdsIamHikariDataSource.class );
	public static final String RDS_REGION_OVERRIDE = "RDS_REGION_OVERRIDE";

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
				.hostname( dbUri.getHost() )
				.port( dbUri.getPort() )
				.username( getUsername() )
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

		String rdsRegionOverride = System.getenv().get( RDS_REGION_OVERRIDE );
		if ( rdsRegionOverride != null ) {
			log.info( "RDS region override: {}", rdsRegionOverride );

			return Region.of( rdsRegionOverride  );
		}
		else {
			return region;
		}
	}
}
