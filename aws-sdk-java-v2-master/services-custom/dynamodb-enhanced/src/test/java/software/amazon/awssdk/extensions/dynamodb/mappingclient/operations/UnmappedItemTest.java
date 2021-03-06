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

package software.amazon.awssdk.extensions.dynamodb.mappingclient.operations;

import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import software.amazon.awssdk.extensions.dynamodb.mappingclient.MappedDatabase;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.MappedTable;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.MapperExtension;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.OperationContext;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.extensions.ReadModification;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.extensions.dynamodb.mappingclient.functionaltests.models.FakeItem;

@RunWith(MockitoJUnitRunner.class)
public class UnmappedItemTest {
    private static final String TABLE_NAME = "table-name";

    @Mock
    DynamoDbClient mockDynamoDbClient;

    @Mock
    MapperExtension mockMapperExtension;

    private MappedTable<FakeItem> createMappedTable(MapperExtension mapperExtension) {
        return MappedDatabase.builder()
                             .dynamoDbClient(mockDynamoDbClient)
                             .extendWith(mapperExtension)
                             .build()
                             .table(TABLE_NAME, FakeItem.getTableSchema());
    }

    @Test
    public void noExtension_mapsToItem() {
        FakeItem fakeItem = FakeItem.createUniqueFakeItem();
        Map<String, AttributeValue> fakeItemMap = FakeItem.getTableSchema().itemToMap(fakeItem, true);
        UnmappedItem unmappedItem = UnmappedItem.of(fakeItemMap);

        assertThat(unmappedItem.getItem(createMappedTable(null)), is(fakeItem));
    }

    @Test
    public void extension_mapsToItem() {
        FakeItem fakeItem = FakeItem.createUniqueFakeItem();
        FakeItem fakeItem2 = FakeItem.createUniqueFakeItem();
        Map<String, AttributeValue> fakeItemMap = FakeItem.getTableSchema().itemToMap(fakeItem, true);
        Map<String, AttributeValue> fakeItemMap2 = FakeItem.getTableSchema().itemToMap(fakeItem2, true);
        when(mockMapperExtension.afterRead(anyMap(), any(), any()))
            .thenReturn(ReadModification.builder().transformedItem(fakeItemMap2).build());

        UnmappedItem unmappedItem = UnmappedItem.of(fakeItemMap);

        MappedTable<FakeItem> mappedTable = createMappedTable(mockMapperExtension);
        assertThat(unmappedItem.getItem(mappedTable), is(fakeItem2));
        verify(mockMapperExtension).afterRead(fakeItemMap,
                                              OperationContext.of(mappedTable.tableName()),
                                              FakeItem.getTableMetadata());
    }

    @Test
    public void nullMapReturnsNullItem() {
        UnmappedItem unmappedItem = UnmappedItem.of(null);

        assertThat(unmappedItem.getItem(createMappedTable(null)), is(nullValue()));
    }

    @Test
    public void emptyMapReturnsNullItem() {
        UnmappedItem unmappedItem = UnmappedItem.of(emptyMap());

        assertThat(unmappedItem.getItem(createMappedTable(null)), is(nullValue()));
    }

}