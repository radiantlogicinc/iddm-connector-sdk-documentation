# Changelog

## 1.1.1 - 2026-01-29

### Changed

- Deprecate `SearchFilter.getValue()` method that returns the wrong value for some filter types. Use the new `SearchFilter.getAssertionValue()` instead.

### Added

- Add `SearchFilter.getAssertionValue()` method.

### Fixed

- Gracefully handle and log errors when inspecting Java 9+ classes for annotations.

## 1.1.0 - 2026-01-14

### Changed

- Deprecate and replace `SchemaObject` methods `getCandidateKeyName`, `getCandidateKeys`, and `getPrimaryKeys`.
- Deprecate `SchemaObject` methods `getOwner` and `getBaseTable` without adding replacements.
- Deprecate and replace `SchemaObjectBuilder` methods `candidateKeyName`, `candidateKeys`, and `primaryKeys`.
- Deprecate `SchemaObjectBuilder` methods `owner` and `baseTable` without adding replacements.
- Expand `SearchFilter` documentation.
- Expand `SchemaObject` methods' documentation.
- Simplify `Schema` Javadoc by removing unnecessary details.

### Added

- Support code-based IDDM schema authoring using `@Attribute` and `@Entity` annotations.
- Allow JSON configuration file to be located anywhere in the connector JAR.
- Add `Schema.getSchemaObjects()` method.

### Fixed

- `SearchFilter` supports all LDAP search filter types defined in [RFC4515](https://datatracker.ietf.org/doc/html/rfc4515).

## 1.0.0 - 2025-09-25

_Initial release._