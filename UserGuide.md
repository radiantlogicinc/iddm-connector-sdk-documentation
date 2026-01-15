# User Guide

## Connector Requirements

The minimum requirements for implementing a custom connector are:

- Apply the `@CustomConnector` annotation to a class to identify it as the connector.
- Implement at least one operation interface to specify what operations the connector supports.
- Specify a configuration file containing details of how to display and configure the connector.

```java
@CustomConnector(configuration = "pennave_connector.json")
public class PennAveConnector
    implements SearchOperations<LdapSearchRequest, LdapResponse<String>> {
  /* ...provide connector implementation... */
}
```

## Operation Interfaces

Operation interfaces are implemented by the connector and specify exactly what operations the connector supports. There are interfaces for common LDAP operations:
 
- `SearchOperations<IN extends SearchRequest, OUT extends SearchResponse>`
- `CreateOperations<IN extends CreateRequest, OUT extends CreateResponse>`
- `ModifyOperations<IN extends ModifyRequest, OUT extends ModifyResponse>`
- `DeleteOperations<IN extends DeleteRequest, OUT extends DeleteResponse>`
- `AuthenticationOperations<IN extends AuthenticationRequest, OUT extends AuthenticationResponse>`

And additional interfaces for IDDM-specific operations:  
 
- `TestConnectionOperations<IN extends TestConnectionRequest, OUT extends TestConnectionResponse>`

The connector should implement interfaces only for the operations it supports. IDDM uses this information to determine what types of requests can be sent to the connector.

## Request and Response Types

Each operation interface includes type arguments specifying the request (input) and response (output) types it expects. There are Java interfaces for implementing custom types, but most users will find it much easier to use the concrete type implementations provided in the SDK. The table below shows the recommend input and output types for each operation interface. (These recommendations are based on what IDDM actually sends to the connector and expects in return.) 

| Operation Interface         | Request Type           | Response Type                           |
|-----------------------------|------------------------|-----------------------------------------|
| `SearchOperations`          | `LdapSearchRequest`    | `LdapResponse`                          |
| `CreateOperations`          | `LdapAddRequest`       | `LdapResponse`                          |
| `ModifyOperations`          | `LdapModifyRequest`    | `LdapResponse`                          |
| `DeleteOperations`          | `LdapDeleteRequest`    | `LdapResponse`                          |
| `AuthenticationOperations`  | `LdapBindRequest`      | `LdapResponse`                          |
| `TestConnectionOperations`  | `TestConnectionRequest`| `TestConnectionResponse`                |

Most operations return an `LdapResponse<T>`. This is a flexible container class for representing an LDAP response that can be returned to IDDM. It includes an `LdapResultCode` and data appropriate for the response, such as search results. The data is not required to follow any specific structure apart from being serializable to JSON. (The connector does not need to actually perform any serialization. IDDM does the work.) But most users will find connector development and integration much easier by following these recommendations when implementing search operations:

1. An LDAP entry is essentially a collection of key-value pairs with at most one nesting level. Structure response data in a similar way.
    ```json
      {
        "username": "washington",
        "firstName": "George",
        "lastName": "Washington"
      }
    ```
2. Store nested data in a list.
    ```json
      {
        "username": "washington",
        "firstName": "George",
        "lastName": "Washington",
        "ranks": ["Colonel", "Major General", "Lieutenant General", "General of the Armies"]
      }
    ```
3. Use primitive data types, such as a numbers, strings and Booleans, to represent values in an entry. 
4. Return search results in a list:
    ```json
   [
      {"username": "washington", "firstName": "George", "lastName": "Washington"},
      {"username": "adams", "firstName": "John", "lastName": "Adams"},
      {"username": "jefferson", "firstName": "Thomas", "lastName": "Jefferson"}
   ]    
    ```

The example below is for a connector that supports search, accepts the incoming request as an `LdapSearchRequest` object, and returns its response to IDDM as an `LdapResponse` containing `List<Map<String, Object>>` data.

```java
@Override
public LdapResponse<List<Map<String, Object>>> search(LdapSearchRequest searchRequest) {
  
  // Create a search result entry
  final Map<String, Object> entry1 = new HashMap<>();
  entry1.put("firstName", "George");
  entry1.put("lastName", "Washington");
  entry1.put("termsServed", 2);
  
  // Create a search result entry
  final Map<String, Object> entry2 = new HashMap<>();
  entry2.put("firstName", "William");
  entry2.put("lastName", "Harrison");
  entry2.put("termsServed", 1);
  
  // Collect all the search results
  final List<Map<String, Object>> searchResultData = new ArrayList();
  searchResultData.add(entry1);
  searchResultData.add(entry2);

  // Return a response with the status code and data
  return new LdapResponse<>(LdapResultCode.SUCCESS, searchResultData);
}
```

Other operations generally should not return data except for a result code.

## Property Injection With @Property

The `@Property` annotation enables users to request system configuration information at runtime. Most users will include the `InjectableProperties.CONNECTION_CONFIGURATION`, as shown below, to retrieve information needed for connecting to their external datasource:
 
```java
@CustomConnector(configuration = "pennave_connector.json")
public class PennAveConnector implements SearchOperations<LdapSearchRequest, LdapResponse<String>> { 
  
  private final String host;
  private final String username;
  private final String password;
  
  public PennAveConnector(@Property(name = CUSTOM_DATASOURCE_PROPERTIES) ReadOnlyProperties connectionProperties) {
    // Read values from the injected property set
    this.host = (String) connectionProperties.get("host");
    this.username = (String) connectionProperties.get("username");
    this.password = (String) connectionProperties.get("password");
  }
  
}
```

The exact object type injected by IDDM depends on the properties requested. ([See the Property Sets section later in this guide](#Property-Sets) or `InjectableProperties` Javadoc for details.) The `@Property` annotation can be applied to only constructor parameters appearing in `@ManagedComponent` and `@CustomConnector` annotated classes. 


## Dependency Injection With @ManagedComponent

Applying the `@ManagedComponent` annotation to a class indicates that class is a "managed component" and makes it eligible for automatic constructor-based injection. Managed components are automatically instantiated and injected by IDDM whenever they appear as constructor parameters for connectors and other managed components. The annotated class must have (1) only one constructor, and (2) that constructor must have either no arguments or all arguments must be annotated with `@Property` or `@ManagedComponent`. 

A typical use case is annotating a custom client that communicates with an external data source. For example:

```java
@ManagedComponent
public class PennAveClient {
  public PennAveClient(
      @Property(name = CUSTOM_DATASOURCE_PROPERTIES) ReadOnlyProperties connectionProperties) {
  ...
  }
}
```

IDDM can now automatically create and inject a new `PennAveClient` instance whenever it appears as a constructor argument in a connector or managed component:

```java
@CustomConnector(configuration = "pennave_connector.json")
public class PennAveConnector implements SearchOperations<LdapSearchRequest, LdapResponse<String>> { 
  
  private final PennAveClient pennAveClient;
  
  public PennAveConnector(final PennAveClient injectedClient) {
    this.pennAveClient = injectedClient;
  }
  ...
}
```

## Connector Configuration

All connectors must include a JSON configuration file describing the connector. For example: 

```json
{
  "name": "PennAveIAM",
  "description": "PennAveIAM custom connector used for the Getting Started tutorial",
  "backendCategory": "custom",
  "userCreated": true,
  "icon": "",
  "isSchemaExtractable": false,
  "meta": [
    {
      "name": "host",
      "description": "Host name of the PennAveIAM server",
      "sectionName": "Properties",
      "defaultValue": "api.pennaveiam.local",
      "dataType": "STRING",
      "isRequired": true,
      "regex": null
    },
    ...
  ]
}
```

The configuration file can be placed anywhere in the connector JAR. Use the `@CustomConnector` `configuration` parameter to specify the configuration file the connector will use. The `configuration` parameter is case-sensitive so it must match the filename exactly. IDDM automatically searches the JAR for this file when loading the connector for the first time. For example, using `@CustomConnector(configuration = "pennave_connector.json")` causes IDDM to search the JAR for the file called `pennave_connector.json`. The connector will not load if the file is not found or if the JAR contains multiple configuration files with the same name.

Top-level properties (except `meta`) describe the connector. Connectors must use the values shown in the example above, customizing only the `name` and `description`. 

| Property name | Type     | Description                               | 
|---------------|----------|-------------------------------------------|
| name          | `String` | Name of the connector                     |
| description   | `String` | Description the connector shown in the UI |

The `meta` value defines the list of properties available via `@Property(name=InjectableProperties.CUSTOM_DATASOURCE_PROPERTIES)`. Each list entry defines a unique property included in the `ReadOnlyProperties` object injected by IDDM.

| Property name | Type     | Description                                                                  |
|---------------|----------|------------------------------------------------------------------------------|
| name          | `String` | Name to display in the UI; used to get the value from `ReadOnlyProperties`   |
| description   | `String` | Property description shown in the UI                                         |
| sectionName   | `String` | Used to group properties in the UI; recommend always using "Properties"      | 
| defaultValue  | `String` | Optional default value; use "null" to not specify a default value            |
| dataType      | `String` | Allowed case-sensitive values: STRING, PASSWORD, BOOLEAN, NUMBER, LIST       |
| isRequired    | boolean  | True if users must provide a value in the UI                                |


## Logging

The IDDM Connector SDK supports logging with [SLF4J](https://www.slf4j.org/), with Log4j as the provider in IDDM. To add logging, include the SLF4J API dependency with `<scope>provided</scope>`: 

```xml
<dependency>
  <groupId>org.slf4j</groupId>
  <artifactId>slf4j-api</artifactId>
  <version>1.7.36</version>
  <scope>provided</scope>
</dependency>
```

And optionally the SLF4J Simple provider for unit testing:

```xml
<dependency>
  <groupId>org.slf4j</groupId>
  <artifactId>slf4j-simple</artifactId>
  <version>1.7.36</version>
  <scope>test</scope>
</dependency>
``` 

Then use the `@Slf4j` annotation wherever logging is needed. Each data source that uses the connector produces its own log files with entries beginning when the connector begins processing a request and ending when the connector returns its response.

## Schema Authoring

The schema authoring feature optionally allows developers to define a schema natively in Java then use it in IDDM. This eliminates the need for an IDDM administrator to manually create the schema through the Control Panel, and helps ensure the connector and IDDM share common model objects. 

Schema authoring is automatically triggered when using a connector to create a new data source. IDDM searches the connector JAR for all `@Entity` annotated classes. These classes are collectively the "schema definition". If found, then the schema definition is used to generate a new schema for the data source. This new schema behaves the same as one manually created through the IDDM Control Panel: it is editable, it can be injected into the connector using the `@Property(name = InjectableProperties.SCHEMAS)` annotation, etc. If no definition is found, then the data source is created without a schema. (Data source creation will fail if a schema definition is found but the schema cannot be generated, for example, because of validation errors, conflicting definitions, etc.)

Use the `@Entity` annotation to define a new schema object (or "table") and the `@Attribute` annotation to define fields within an entity. The connector JAR may contain multiple `@Entity` annotated classes, each of which will become a separate schema object in IDDM. Both annotations include optional parameters for fine-tuning the schema definition but provide sensible defaults. (See the [Connector SDK Javadoc](https://radiantlogicinc.github.io/iddm-connector-sdk-documentation/) for more details.)

A common pattern for defining the schema is annotating model objects the connector already uses. This approach effectively creates a common structure for moving data between the connector and IDDM. For example, the connector might include a `President` POJO for storing data retrieved from the external data source. This same POJO can be annotated to define the IDDM schema:

```java
import com.radiantlogic.iddm.schema.Entity;
import com.radiantlogic.iddm.schema.Attribute;

// Using "name" to specify the schema object name and "namingKey" the RDN key used for LDAP entries
@Entity(name = "vdPresident", namingKey = "name")
@NoArgsConstructor
@Getter
public final class President {

   @Attribute
   private String firstName;

   @Attribute
   private String lastName;

   // Using "isNullable" to indicate an attribute should be optional in the schema
   @Attribute(isNullable = true)
   private String email;

   // Using "name" to give the attribute a different name in the schema than in this class
   @Attribute(name = "username", isNamingAttribute = true)
   private String id;

   // Using "tags" to record additional useful information about the attribute
   @Attribute(
           name = "termsServed",
           asType = Field.Type.INTEGER,
           isNullable = true,
           tags = {"GET /president/{username}"})
   private int numTermsServed;
}
```

IDDM would use this class to create a new schema containing a table called `vdPresident` with five attributes: `firstName`, `lastName`, optional `email`, optional `termsServed`, and the naming attribute ("primary key") `username`. The example below shows how data in a `President` Java object (represented in JSON) maps to an LDAP entry that conforms to the schema definition:

```json
{
   "firstName": "George",
   "lastName": "Washington",
   "email": "washington@executive.gov",
   "id": "washington",
   "numTermsServed": 2
}
```

``` 
   dn: name=washington,ou=presidents,o=pennaveiam
   objectClass: top
   objectClass: vdPresident
   firstName: George
   lastName: Washington
   email: washington@executive.gov
   username: washington
   termsServed: 2
```

> [!IMPORTANT]
> The schema definition and data handling are separate concerns. You can use the same classes for both the schema definition and returning data to IDDM, but the `@Entity` and `@Attribute` annotations only affect schema generation, not runtime data handling such as serialization. You are responsible for ensuring data returned to IDDM is structured to match the schema definition.

When defining entities:

All `@Entity` annotated classes must:
- Have a public, no-argument constructor
- Be a concrete top-level class
- Contain at least one attribute designated as a "naming attribute" (`isNamingAttribute = true`)

> [!IMPORTANT]
> The `@Entity` and `@Attribute` annotations are not inherited. Extending an entity does not make the subclass an entity, and the `@Entity` annotated subclass of an entity does not inherit its parent's attributes. Only explicitly annotated classes and fields are included in the schema definition.


## SDK Component Summary

### Annotations

| Component           | Description                                                                                  |
|---------------------|----------------------------------------------------------------------------------------------|
| `@CustomConnector`  | Annotation applied to the primary connector class and used by IDDM to identify the connector |
| `@Property`       | Annotation for injecting configuration data from IDDM                                        |
| `@ManagedComponent` | Annotation for marking a class as eligible for automatic constructor-based injection         |
| `@Entity`           | Annotation applied to classes that define IDDM schema objects                                |
| `@Attribute`        | Annotation applied to fields that are part of the IDDM schema definition                     |


### Operation Interfaces

| Component                                                                                        | Description                                                          |
|--------------------------------------------------------------------------------------------------|----------------------------------------------------------------------|
| `SearchOperations<IN extends SearchRequest, OUT extends Response>`                               | Interface for connectors that support read/search operations         |
| `CreateOperations<IN extends CreateRequest, OUT extends Response>`                               | Interface for connectors that support create/add operations          |
| `ModifyOperations<IN extends ModifyRequest, OUT extends Response>`                               | Interface for connectors that support update/modify operations       |
| `DeleteOperations<IN extends DeleteRequest, OUT extends Response>`                               | Interface for connectors that support delete operations              |
| `AuthenticationOperations<IN extends AuthenticationRequest, OUT extends Response>`               | Interface for connectors that support authentication/bind operations |
| `TestConnectionOperations<IN extends TestConnectionRequest, OUT extends TestConnectionResponse>` | Interface for connectors that support IDDM's test connection feature |


### Request Implementations

| `Request`               | Description                                                              |
|-------------------------|--------------------------------------------------------------------------|
| `LdapSearchRequest`     | Implements an LDAP "Search Request" based on RFC 4511                   |
| `LdapModifyRequest`     | Implements an LDAP "Modify Request" based on RFC 4511                   |
| `LdapAddRequest`        | Implements an LDAP "Add Request" based on RFC 4511                      |
| `LdapDeleteRequest`     | Implements an LDAP "Delete Request" based on RFC 4511                   |
| `LdapBindRequest`       | Implements an LDAP "Bind Request" based on RFC 4511                     |
| `TestConnectionRequest` | Describes a request to test the connection to a URI-identified resource |


### Response Implementations

| `Response`               | Description                                                                     |
|--------------------------|---------------------------------------------------------------------------------|
| `LdapResponse<T>`        | Represents an LDAP response containing operation status and any associated data |
| `TestConnectionResponse` | Represents the result of a test connection operation                            |

### Property Sets

| Name                           | Object Type          | Description                                                                             |
|--------------------------------|----------------------|-----------------------------------------------------------------------------------------|
| `CUSTOM_DATASOURCE_PROPERTIES` | `ReadOnlyProperties` | Provides current values of custom properties defined in the connector configuration     |
| `PRIMARY_KEY_ATTRIBUTES`       | `ReadOnlyProperties` | Provides the attribute name-value pairs used to form an LDAP entry's "Primary Key"      |
| `TARGET_SCHEMA_OBJECTS`        | `ReadOnlyProperties` | Provides the names of schema objects targeted by the current LDAP operation             |
| `SCHEMAS`                      | `Schema`             | Provides schema information for the datasource that received the current LDAP operation |



