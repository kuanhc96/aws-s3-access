/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.extensions.dynamodb.mappingclient.core;

import static software.amazon.awssdk.extensions.dynamodb.mappingclient.core.Utils.createKeyFromItem;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.Key;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.MappedTable;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.MapperExtension;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.TableMetadata;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.TableOperation;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@SdkPublicApi
@ThreadSafe
public class DynamoDbMappedTable<T> implements MappedTable<T> {
    private final DynamoDbClient dynamoDbClient;
    private final MapperExtension mapperExtension;
    private final TableSchema<T> tableSchema;
    private final String tableName;

    DynamoDbMappedTable(DynamoDbClient dynamoDbClient,
                        MapperExtension mapperExtension,
                        TableSchema<T> tableSchema,
                        String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.mapperExtension = mapperExtension;
        this.tableSchema = tableSchema;
        this.tableName = tableName;
    }

    @Override
    public <R> R execute(TableOperation<T, ?, ?, R> operationToPerform) {
        return operationToPerform.executeOnPrimaryIndex(tableSchema, tableName, mapperExtension, dynamoDbClient);
    }

    @Override
    public MapperExtension mapperExtension() {
        return this.mapperExtension;
    }

    @Override
    public TableSchema<T> tableSchema() {
        return this.tableSchema;
    }

    public DynamoDbClient dynamoDbClient() {
        return dynamoDbClient;
    }

    public String tableName() {
        return tableName;
    }

    @Override
    public DynamoDbMappedIndex<T> index(String indexName) {
        // Force a check for the existence of the index
        tableSchema.tableMetadata().indexPartitionKey(indexName);

        return new DynamoDbMappedIndex<>(dynamoDbClient, mapperExtension, tableSchema, tableName, indexName);
    }

    @Override
    public Key keyFrom(T item) {
        return createKeyFromItem(item, tableSchema, TableMetadata.primaryIndexName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DynamoDbMappedTable<?> that = (DynamoDbMappedTable<?>) o;

        if (dynamoDbClient != null ? ! dynamoDbClient.equals(that.dynamoDbClient) : that.dynamoDbClient != null) {
            return false;
        }
        if (mapperExtension != null ? ! mapperExtension.equals(that.mapperExtension) : that.mapperExtension != null) {
            return false;
        }
        if (tableSchema != null ? ! tableSchema.equals(that.tableSchema) : that.tableSchema != null) {
            return false;
        }
        return tableName != null ? tableName.equals(that.tableName) : that.tableName == null;
    }

    @Override
    public int hashCode() {
        int result = dynamoDbClient != null ? dynamoDbClient.hashCode() : 0;
        result = 31 * result + (mapperExtension != null ? mapperExtension.hashCode() : 0);
        result = 31 * result + (tableSchema != null ? tableSchema.hashCode() : 0);
        result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
        return result;
    }
}
