package com.starter.project;

import static com.radiantlogic.iddm.base.InjectableProperties.CUSTOM_DATASOURCE_PROPERTIES;

import com.radiantlogic.iddm.annotations.ManagedComponent;
import com.radiantlogic.iddm.annotations.Property;
import com.radiantlogic.iddm.base.ReadOnlyProperties;

/**
 * Custom client for communicating with the external data source.
 */
@ManagedComponent
public class ExternalDatasourceClient {

  /* Custom data source properties set in the IDDM Control Panel. */
  private final ReadOnlyProperties datasourceProperties;

  /**
   * Public constructor.
   *
   * @param datasourceProperties Custom properties from the IDDM data source that is using this
   *     connector. This instance is automatically injected by IDDM at runtime.
   */
  public ExternalDatasourceClient(
      @Property(name = CUSTOM_DATASOURCE_PROPERTIES) ReadOnlyProperties datasourceProperties
  ) {
    this.datasourceProperties = datasourceProperties;
  }

  //----------------------------------------------------------------------------------------
  // TODO: Add methods for communicating with the external, third party data source and
  //  returning data to the connector.
  //----------------------------------------------------------------------------------------
}
