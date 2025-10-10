package com.starter.project;

import com.radiantlogic.iddm.annotations.CustomConnector;
import com.radiantlogic.iddm.base.CreateOperations;
import com.radiantlogic.iddm.base.DeleteOperations;
import com.radiantlogic.iddm.base.ModifyOperations;
import com.radiantlogic.iddm.base.SearchOperations;
import com.radiantlogic.iddm.base.TestConnectionOperations;
import com.radiantlogic.iddm.base.TestConnectionRequest;
import com.radiantlogic.iddm.base.TestConnectionResponse;
import com.radiantlogic.iddm.ldap.LdapAddRequest;
import com.radiantlogic.iddm.ldap.LdapDeleteRequest;
import com.radiantlogic.iddm.ldap.LdapModifyRequest;
import com.radiantlogic.iddm.ldap.LdapResponse;
import com.radiantlogic.iddm.ldap.LdapResultCode;
import com.radiantlogic.iddm.ldap.LdapSearchRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom connector allowing Radiant Logic IDDM to communicate with an external data source.
 */
@CustomConnector(configuration = "connector_configuration.json")
@Slf4j
public class StarterProjectConnector implements
    CreateOperations<LdapAddRequest, LdapResponse<String>>,
    SearchOperations<LdapSearchRequest, LdapResponse<String>>,
    ModifyOperations<LdapModifyRequest, LdapResponse<String>>,
    DeleteOperations<LdapDeleteRequest, LdapResponse<String>>,
    TestConnectionOperations<TestConnectionRequest, TestConnectionResponse> {

  /* Custom client used by the connector to communicate with the external data source. */
  private final ExternalDatasourceClient externalDatasourceClient;

  /**
   * Public constructor.
   *
   * @param externalDatasourceClient instance automatically injected by IDDM at runtime.
   */
  public StarterProjectConnector(final ExternalDatasourceClient externalDatasourceClient) {
    this.externalDatasourceClient = externalDatasourceClient;
  }

  /**
   * Method called by IDDM when performing create operations.
   */
  @Override
  public LdapResponse<String> create(final LdapAddRequest ldapAddRequest) {

    // TODO: Implement this method to support LDAP Add operations.
    //  Otherwise, delete this method and remove the CreateOperations interface.

    log.info("{} received an LDAP Add request targeting {}. "
            + "But this placeholder method is not implemented. Returning an error.",
        this.getClass().getSimpleName(), ldapAddRequest.getDn());
    return new LdapResponse<>(LdapResultCode.OTHER);
  }

  /**
   * Method called by IDDM when performing search operations.
   */
  @Override
  public LdapResponse<String> search(final LdapSearchRequest ldapSearchRequest) {

    // TODO: Implement this method to support LDAP Search operations.
    //  Otherwise, delete this method and remove the SearchOperations interface.

    log.info("{} received an LDAP Search request targeting {}. "
            + "But this placeholder method is not implemented. Returning an error.",
        this.getClass().getSimpleName(), ldapSearchRequest.getBaseDN());
    return new LdapResponse<>(LdapResultCode.OTHER);
  }

  /**
   * Method called by IDDM when performing modify operations.
   */
  @Override
  public LdapResponse<String> modify(final LdapModifyRequest ldapModifyRequest) {

    // TODO: Implement this method to support LDAP Modify operations.
    //  Otherwise, delete this method and remove the ModifyOperations interface.

    log.info("{} received an LDAP Modify request targeting {}. "
            + "But this placeholder method is not implemented. Returning an error.",
        this.getClass().getSimpleName(), ldapModifyRequest.getDn());
    return new LdapResponse<>(LdapResultCode.OTHER);
  }

  /**
   * Method called by IDDM for performing delete operations.
   */
  @Override
  public LdapResponse<String> delete(final LdapDeleteRequest ldapDeleteRequest) {

    // TODO: Implement this method to support LDAP Delete operations.
    //  Otherwise, delete this method and remove the DeleteOperations interface.

    log.info("{} received an LDAP Delete request targeting {}. "
            + "But this placeholder method is not implemented. Returning an error.",
        this.getClass().getSimpleName(), ldapDeleteRequest.getDn());
    return new LdapResponse<>(LdapResultCode.OTHER);
  }

  /**
   * Method called by IDDM for performing test connection operations.
   */
  @Override
  public TestConnectionResponse testConnection(final TestConnectionRequest testConnectionRequest) {

    // TODO: Implement this method to support the test connection operation.
    //  Otherwise, delete this method and remove the TestConnectionOperations interface.

    log.info("{} received a Test Connection request. This placeholder method is not implemented. "
            + "Returning success.",
        this.getClass().getSimpleName());
    return new TestConnectionResponse(testConnectionRequest.getTarget(), true);
  }
}
