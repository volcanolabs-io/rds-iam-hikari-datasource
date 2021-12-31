package io.volcanolabs.rds;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.amazonaws.services.rds.auth.GetIamAuthTokenRequest;
import com.amazonaws.services.rds.auth.RdsIamAuthTokenGenerator;
import com.zaxxer.hikari.HikariDataSource;

import java.net.URI;

public class RdsIamHikariDataSource extends HikariDataSource {

	@Override
	public String getPassword() {
		return getToken();
	}

	private String getToken() {
		var region = new DefaultAwsRegionProviderChain().getRegion();
		var hostnamePort = getHostnamePort();

		RdsIamAuthTokenGenerator generator = RdsIamAuthTokenGenerator.builder()
				.credentials( new DefaultAWSCredentialsProviderChain() )
				.region( region )
				.build();

		GetIamAuthTokenRequest request = GetIamAuthTokenRequest.builder()
				.hostname( hostnamePort.key() )
				.port( hostnamePort.value() )
				.userName( getUsername() )
				.build();

		return generator.getAuthToken( request );
	}

	// JDBC URL has a standard URL format, like: jdbc:postgresql://localhost:5432/test_database
	private Pair<String, Integer> getHostnamePort() {
		var cleanUrl = getJdbcUrl().substring( 5 );
		var dbUri = URI.create( cleanUrl );

		return new Pair<>( dbUri.getHost(), dbUri.getPort() );
	}

	private static class Pair<K, V> {
		K k;
		V v;

		Pair(K k, V v) {
			this.k = k;
			this.v = v;
		}

		K key() {
			return k;
		}

		V value() {
			return v;
		}
	}
}
