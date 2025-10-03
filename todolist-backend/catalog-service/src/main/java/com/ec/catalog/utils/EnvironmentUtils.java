package com.ec.catalog.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class EnvironmentUtils {
	
	@Autowired
	private Environment environment;
	
	public boolean isDevMode() {
		return hasProfile("dev");
	}
	
	public boolean isProdMode() {
		return hasProfile("prod");
	}
	
	public boolean hasProfile(String profile) {
		return Arrays.asList(environment.getActiveProfiles()).contains(profile);
	}
}
