package com.scholastic.srmapi.config;

public class EnvironmentConfig {
    /**
     * Returns a System environment variable
     * @param env The system environment variable name being fetched.
     * @return The environment variable value
     */
    public static String getSystemVar(String env) {
        return System.getenv(env);
    }
}
