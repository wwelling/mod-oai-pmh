package org.folio.oaipmh.querybuilder;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class ViewTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ViewTest.class);

  private static final Path EXPECTED_ALL_NON_DELETED_INSTANCE_IDS = Path.of("src/test/resources/views/expected_all_non_deleted_instance_ids.csv");
  private static final Path EXPECTED_ALL_DELETED_INSTANCE_IDS = Path.of("src/test/resources/views/expected_all_deleted_instance_ids.csv");
  private static final Path EXPECTED_FOLIO_DELETED_INSTANCE_IDS = Path.of("src/test/resources/views/expected_folio_deleted_instance_ids.csv");
  private static final Path EXPECTED_FOLIO_NON_DELETED_INSTANCE_IDS = Path.of("src/test/resources/views/expected_folio_non_deleted_instance_ids.csv");
  private static final Path EXPECTED_MARC_DELETED_INSTANCE_IDS = Path.of("src/test/resources/views/expected_marc_deleted_instance_ids.csv");
  private static final Path EXPECTED_MARC_NON_DELETED_INSTANCE_IDS = Path.of("src/test/resources/views/expected_marc_non_deleted_instance_ids.csv");
  private static final Path EXPECTED_ALL_NON_DISCOVERY_SUPPRESSED_INSTANCE_IDS = Path.of("src/test/resources/views/expected_all_non_discovery_suppressed_instance_ids.csv");
  private static final Path EXPECTED_ALL_DISCOVERY_SUPPRESSED_INSTANCE_IDS = Path.of("src/test/resources/views/expected_all_discovery_suppressed_instance_ids.csv");
  private static final Path EXPECTED_FOLIO_DISCOVERY_SUPPRESSED_INSTANCE_IDS = Path.of("src/test/resources/views/expected_folio_discovery_suppressed_instance_ids.csv");
  private static final Path EXPECTED_MARC_DISCOVERY_SUPPRESSED_INSTANCE_IDS = Path.of("src/test/resources/views/expected_marc_discovery_suppressed_instance_ids.csv");
  private static final Path EXPECTED_MARC_NON_DISCOVERY_SUPPRESSED_INSTANCE_IDS = Path.of("src/test/resources/views/expected_marc_non_discovery_suppressed_instance_ids.csv");
  private static final Path EXPECTED_FOLIO_NON_DISCOVERY_SUPPRESSED_INSTANCE_IDS = Path.of("src/test/resources/views/expected_folio_non_discovery_suppressed_instance_ids.csv");

  private static final Network network = Network.newNetwork();

  @Container
  private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12-alpine")
    .withNetwork(network)
    .withNetworkAliases("postgres")
    .withExposedPorts(5432)
    .withUsername("username")
    .withPassword("password")
    .withDatabaseName("postgres")
    .withInitScript("sql/init_database_with_data.sql");

  @SneakyThrows
  @Test
  void shouldReturnAllNonDeletedInstances() {
    var query = QueryBuilder.build("oaitest", null, null, null, null,
      false, false, 200);
    LOGGER.debug("\n" + query);
    var actualResponse = doQuery(query, "instance_id");
    assertEquals(Files.readString(EXPECTED_ALL_NON_DELETED_INSTANCE_IDS).trim(), actualResponse.trim());
  }

  @SneakyThrows
  @Test
  void shouldReturnAllDeletedInstances() {
    var query = QueryBuilder.build("oaitest", null, null, null, null,
      false, true, 200);
    LOGGER.debug("\n" + query);
    var actualResponse = doQuery(query, "instance_id");
    assertEquals(Files.readString(EXPECTED_ALL_DELETED_INSTANCE_IDS).trim(), actualResponse.trim());
  }

  @SneakyThrows
  @Test
  void shouldReturnFolioNonDeletedInstances() {
    var query = QueryBuilder.build("oaitest", null, null, null, RecordsSource.FOLIO,
      false, false, 200);
    LOGGER.debug("\n" + query);
    var actualResponse = doQuery(query, "instance_id");
    assertEquals(Files.readString(EXPECTED_FOLIO_NON_DELETED_INSTANCE_IDS).trim(), actualResponse.trim());
  }

  @SneakyThrows
  @Test
  void shouldReturnFolioDeletedInstances() {
    var query = QueryBuilder.build("oaitest", null, null, null, RecordsSource.FOLIO,
      false, true, 200);
    LOGGER.debug("\n" + query);
    var actualResponse = doQuery(query, "instance_id");
    assertEquals(Files.readString(EXPECTED_FOLIO_DELETED_INSTANCE_IDS).trim(), actualResponse.trim());
  }

  @SneakyThrows
  @Test
  void shouldReturnMarcNonDeletedInstances() {
    var query = QueryBuilder.build("oaitest", null, null, null, RecordsSource.MARC,
      false, false, 200);
    LOGGER.debug("\n" + query);
    var actualResponse = doQuery(query, "instance_id");
    assertEquals(Files.readString(EXPECTED_MARC_NON_DELETED_INSTANCE_IDS).trim(), actualResponse.trim());
  }

  @SneakyThrows
  @Test
  void shouldReturnMarcDeletedInstances() {
    var query = QueryBuilder.build("oaitest", null, null, null, RecordsSource.MARC,
      false, true, 200);
    LOGGER.debug("\n" + query);
    var actualResponse = doQuery(query, "instance_id");
    assertEquals(Files.readString(EXPECTED_MARC_DELETED_INSTANCE_IDS).trim(), actualResponse.trim());
  }

  @SneakyThrows
  @Test
  void shouldReturnAllDiscoverySuppressedInstances() {
    var query = QueryBuilder.build("oaitest", null, null, null, null,
      false, false, 200);
    LOGGER.info("\n" + query);
    var actualResponse = doQuery(query, "instance_id");
    assertEquals(Files.readString(EXPECTED_ALL_DISCOVERY_SUPPRESSED_INSTANCE_IDS).trim(), actualResponse.trim());
  }

  @SneakyThrows
  @Test
  void shouldReturnAllNonDiscoverySuppressedInstances() {
    var query = QueryBuilder.build("oaitest", null, null, null, null,
      true, false, 200);
    LOGGER.info("\n" + query);
    var actualResponse = doQuery(query, "instance_id");
    assertEquals(Files.readString(EXPECTED_ALL_NON_DISCOVERY_SUPPRESSED_INSTANCE_IDS).trim(), actualResponse.trim());
  }

  @SneakyThrows
  @Test
  void shouldReturnFolioDiscoverySuppressedInstances() {
    var query = QueryBuilder.build("oaitest", null, null, null, RecordsSource.FOLIO,
      false, false, 200);
    LOGGER.info("\n" + query);
    var actualResponse = doQuery(query, "instance_id");
    assertEquals(Files.readString(EXPECTED_FOLIO_DISCOVERY_SUPPRESSED_INSTANCE_IDS).trim(), actualResponse.trim());
  }

  @SneakyThrows
  @Test
  void shouldReturnFolioNonDiscoverySuppressedInstances() {
    var query = QueryBuilder.build("oaitest", null, null, null, RecordsSource.FOLIO,
      true, false, 200);
    LOGGER.info("\n" + query);
    var actualResponse = doQuery(query, "instance_id");
    assertEquals(Files.readString(EXPECTED_FOLIO_NON_DISCOVERY_SUPPRESSED_INSTANCE_IDS).trim(), actualResponse.trim());
  }

  @SneakyThrows
  @Test
  void shouldReturnMarcDiscoverySuppressedInstances() {
    var query = QueryBuilder.build("oaitest", null, null, null, RecordsSource.MARC,
      false, false, 200);
    LOGGER.info("\n" + query);
    var actualResponse = doQuery(query, "instance_id");
    assertEquals(Files.readString(EXPECTED_MARC_DISCOVERY_SUPPRESSED_INSTANCE_IDS).trim(), actualResponse.trim());
  }

  @SneakyThrows
  @Test
  void shouldReturnMarcNonDiscoverySuppressedInstances() {
    var query = QueryBuilder.build("oaitest", null, null, null, RecordsSource.MARC,
      true, false, 200);
    LOGGER.info("\n" + query);
    var actualResponse = doQuery(query, "instance_id");
    assertEquals(Files.readString(EXPECTED_MARC_NON_DISCOVERY_SUPPRESSED_INSTANCE_IDS).trim(), actualResponse.trim());
  }

  @SneakyThrows
  @Test
  void shouldReturnAllFolioInstancesIfLastUpdate_2023_04_16() {
    var query = QueryBuilder.build("oaitest", null, "2023-04-16T00:00:00Z", "2023-04-16T23:59:59Z",
      null, false, false, 200);
    LOGGER.info("\n" + query);
    var actualResponse = doQuery(query, "instance_id");
    assertEquals(Files.readString(EXPECTED_FOLIO_NON_DELETED_INSTANCE_IDS).trim(), actualResponse.trim());
  }

  @SneakyThrows
  @Test
  void shouldReturnAllMarcInstancesIfLastUpdate_2023_06_30() {
    var query = QueryBuilder.build("oaitest", null, "2023-06-30T00:00:00.00Z", "2023-06-30T23:59:59Z",
      null, false, false, 200);
    LOGGER.info("\n" + query);
    var actualResponse = doQuery(query, "instance_id");
    assertEquals(Files.readString(EXPECTED_MARC_NON_DELETED_INSTANCE_IDS).trim(), actualResponse.trim());
  }

  @SneakyThrows
  @Test
  void shouldReturnOnlyOneFolioInstanceWhoseItemHas_2023_04_18_LastUpdate() {
    var query = QueryBuilder.build("oaitest", null, "2023-04-18T00:00:00.00Z", "2023-04-18T23:59:59Z",
      null, false, false, 200);
    LOGGER.info("\n" + query);
    var actualResponse = doQuery(query, "instance_id");
    assertEquals("a89eccf0-57a6-495e-898d-32b9b2210f2f", actualResponse.trim());
    // If using source=MARC, should return nothing cause a89eccf0-57a6-495e-898d-32b9b2210f2f is FOLIO instance.
    query = QueryBuilder.build("oaitest", null, "2023-04-18T00:00:00.00Z", "2023-04-18T23:59:59Z",
      RecordsSource.MARC, false, false, 200);
    actualResponse = doQuery(query, "instance_id");
    assertEquals("", actualResponse.trim());
  }

  private String doQuery(String query, String... columns) throws SQLException {
    try (Connection connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(),
      postgres.getPassword()); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      return getSqlResponse(preparedStatement.executeQuery(), columns);
    }
  }

  private String getSqlResponse(ResultSet resultSet, String... columns) throws SQLException {
    String rows = "";
    while (resultSet.next()) {
      String row = "";
      for (String col: columns) {
        var elem = resultSet.getObject(col);
        row += elem + ",";
      }
      row = row.substring(0, row.length() - 1);
      rows += row + "\n";
    }
    return rows;
  }

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }
}
