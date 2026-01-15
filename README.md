# Radiant Logic IDDM Connector SDK

The IDDM Connector SDK provides the resources for building custom connectors that enable communication between Radiant Logic IDDM and third-party, non-LDAP data sources.

## Getting the SDK

The IDDM Connector SDK is available from the [Maven Central Repository](https://repo1.maven.org/maven2/com/radiantlogic/iddm-connector-sdk/). To add the SDK to a Maven project, use the dependency:

```text
<dependency>
  <groupId>com.radiantlogic</groupId>
  <artifactId>iddm-connector-sdk</artifactId>
  <version>1.0.0</version>
  <scope>provided</scope>
</dependency>
```

### Compatibility

Connectors built using the SDK must be written in Java 8 and are supported in IDDM v8.2.0+.

## Learning the Connector SDK

Start by following the [Getting Started Tutorial](tutorials/getting-started/README.md) to build your first connector. Afterward, check the [IDDM Connector SDK User Guide](UserGuide.md) and [Javadoc](https://radiantlogicinc.github.io/iddm-connector-sdk-documentation/) to learn more, and use the [Starter Project](tutorials/starter-project/README.md) for quickly starting a new project. 
