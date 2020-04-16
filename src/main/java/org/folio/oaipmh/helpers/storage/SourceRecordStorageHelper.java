package org.folio.oaipmh.helpers.storage;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.folio.oaipmh.Request;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

public class SourceRecordStorageHelper extends AbstractStorageHelper {

  private static final String RECORD_ID = "recordId";

  /**
   * Alternative option is to use SourceStorageClient generated by RMB.
   * See https://github.com/folio-org/mod-source-record-storage#rest-client-for-mod-source-record-storage
   */
  public static final String SOURCE_STORAGE_RESULT_URI = "/source-storage/sourceRecords";
  public static final String SOURCE_STORAGE_RECORD_URI = "/source-storage/records/%s";
  private static final String CONTENT = "content";
  private static final String INSTANCE_ID = "instanceId";
  private static final String PARSED_RECORD = "parsedRecord";
  private static final String EXTERNAL_IDS_HOLDER = "externalIdsHolder";

  @Override
  public JsonArray getItems(JsonObject entries) {
    return entries.getJsonArray("sourceRecords");
  }

  @Override
  public String getRecordId(JsonObject entry) {
    return entry.getString(RECORD_ID);
  }

  /**
   * Returns instance id that is linked to record within externalIdsHolder field.
   *
   * @param entry the item returned by source-storage
   * @return instance id
   */
  @Override
  public String getIdentifierId(final JsonObject entry) {
    return entry.getJsonObject(EXTERNAL_IDS_HOLDER).getString(INSTANCE_ID);
  }

  @Override
  public String getInstanceRecordSource(JsonObject entry) {
    return Optional.ofNullable(entry.getJsonObject(PARSED_RECORD))
      .map(record -> record.getJsonObject(CONTENT))
      .map(JsonObject::encode)
      .orElse(null);
  }

  @Override
  public String getRecordSource(JsonObject record) {
    return getInstanceRecordSource(record);
  }

  @Override
  public String buildRecordsEndpoint(Request request) throws UnsupportedEncodingException {
    return SOURCE_STORAGE_RESULT_URI + buildSearchQuery(request);
  }

  @Override
  protected void addSource(CQLQueryBuilder queryBuilder) {
    queryBuilder.addStrictCriteria("recordType", "MARC");
  }

  @Override
  void addSuppressFromDiscovery(final CQLQueryBuilder queryBuilder) {
    queryBuilder.addStrictCriteria("additionalInfo.suppressDiscovery", "false");
  }

  @Override
  protected String getIdentifierName() {
    return EXTERNAL_IDS_HOLDER + "." + INSTANCE_ID;
  }

  @Override
  public String getRecordByIdEndpoint(String id) {
    return String.format(SOURCE_STORAGE_RECORD_URI, id);
  }

}
