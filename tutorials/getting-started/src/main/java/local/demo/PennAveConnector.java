package local.demo;

import static com.radiantlogic.iddm.base.InjectableProperties.CUSTOM_DATASOURCE_PROPERTIES;

import com.radiantlogic.iddm.annotations.CustomConnector;
import com.radiantlogic.iddm.annotations.Property;
import com.radiantlogic.iddm.base.ReadOnlyProperties;
import com.radiantlogic.iddm.base.SearchOperations;
import com.radiantlogic.iddm.base.SearchScope;
import com.radiantlogic.iddm.ldap.DN;
import com.radiantlogic.iddm.ldap.LdapResponse;
import com.radiantlogic.iddm.ldap.LdapResultCode;
import com.radiantlogic.iddm.ldap.LdapSearchRequest;
import com.radiantlogic.iddm.ldap.RDN;
import java.util.List;

@CustomConnector(configuration = "pennave_connector.json")
public class PennAveConnector implements
    SearchOperations<LdapSearchRequest, LdapResponse<String>> {

  // Member variable reference to the PennAveClient created in the constructor
  private final PennAveClient pennAveClient;

  public PennAveConnector(
      @Property(name = CUSTOM_DATASOURCE_PROPERTIES) ReadOnlyProperties connectionProperties) {

    // Read individual connection properties provided by IDDM
    String host = (String) connectionProperties.get("host");
    String username = (String) connectionProperties.get("username");
    String password = (String) connectionProperties.get("password");

    // Create an instance of the REST client and connect
    this.pennAveClient = new PennAveClient(host, username, password);
    this.pennAveClient.connect();
  }

  @Override
  public LdapResponse<String> search(LdapSearchRequest searchRequest) {

    String jsonResponse = null;
    if (searchRequest.getSearchScope() == SearchScope.BASE) {
      // Extract the username from the incoming search request
      String username = getUsername(searchRequest);
      // Search the PennAveIAM server for the specific user
      jsonResponse = pennAveClient.getUser(username);
    } else {
      jsonResponse = pennAveClient.getAllUsers();
    }

    return jsonResponse != null ?
        new LdapResponse<>(LdapResultCode.SUCCESS, jsonResponse) :
        new LdapResponse<>(LdapResultCode.OPERATIONS_ERROR);
  }

  private String getUsername(LdapSearchRequest searchRequest) {
    // Get the base DN from the search request. Ex: "username=washington,o=pennaveiam"
    DN dn = searchRequest.getBaseDN();
    // Split the DN into its individual components. Ex: ["username=washington", "o=pennaveiam"]
    List<RDN> components = dn.getRDNs();
    // Retrieve the left-most RDN. Ex: "username=washington"
    RDN rdn = components.get(0);
    // Retrieve username. Ex: "washington"
    return rdn.getAttributes().get(0).getValues().get(0);
  }
}