# Radiant Logic Connector SDK

The Radiant Logic Connector SDK provides the resources for building custom connectors that enable communication between Radiant Logic Identity Data Platform and third-party, non-LDAP data sources.

## Getting the SDK

The Connector SDK is available from the [Maven Central Repository](https://repo1.maven.org/maven2/com/radiantlogic/iddm-connector-sdk/). To add the Connector SDK to a Maven project, use the dependency:

```text
<dependency>
  <groupId>com.radiantlogic</groupId>
  <artifactId>iddm-connector-sdk</artifactId>
  <version>2.0.0</version>
  <scope>provided</scope>
</dependency>
```

### Compatibility

Connectors must be written in Java. The required Java version depends on which Connector SDK version you use. Refer to the following table to find the minimum supported Connector SDK version for each Radiant Logic product:

<table>
  <thead>
    <tr>
      <th>Connector SDK Version</th>
      <th>Java Version</th>
      <th>Identity Data Management</th>
      <th>Identity Data Platform</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>2.0.0</td>
      <td>21</td>
      <td>9.0.0</td>
      <td>Not supported</td>
    </tr>
    <tr>
      <td>1.2.0</td>
      <td rowspan="4">8</td>
      <td>8.4.0</td>
      <td>2.2.0</td>
    </tr>
    <tr>
      <td>1.1.1</td>
      <td>8.3.1</td>
      <td>1.4.0</td>
    </tr>
    <tr>
      <td>1.1.0</td>
      <td>8.3.0</td>
      <td>Not supported</td>
    </tr>
    <tr>
      <td>1.0.0</td>
      <td>8.2.0</td>
      <td>Not supported</td>
    </tr>
  </tbody>
</table>


## Learning the Connector SDK

Start by following the [Getting Started Tutorial](tutorials/getting-started/readme.md) to build your first connector. Afterward, check the [Connector SDK User Guide](userguide.md) and [Javadoc](https://radiantlogicinc.github.io/iddm-connector-sdk-documentation/) to learn more, and use the [Starter Project](tutorials/starter-project/readme.md) for quickly starting a new project.
