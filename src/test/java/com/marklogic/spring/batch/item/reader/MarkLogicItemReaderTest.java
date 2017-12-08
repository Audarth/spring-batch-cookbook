package com.marklogic.spring.batch.item.reader;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentRecord;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.spring.batch.config.MarkLogicConfiguration;
import com.marklogic.spring.batch.test.AbstractSpringBatchTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@ContextConfiguration(classes = MarkLogicConfiguration.class)
@TestPropertySource("classpath:job.properties")
public class MarkLogicItemReaderTest extends AbstractSpringBatchTest {

    private MarkLogicItemReader itemReader;

    @Before
    public void before() {
        DatabaseClient client = getClient();
        QueryManager qm = client.newQueryManager();
        StructuredQueryBuilder sqb = qm.newStructuredQueryBuilder();
        StructuredQueryDefinition query = sqb.directory(true, "/docs/");

        itemReader = new MarkLogicItemReader(getClient(), query);

        StringHandle handle = new StringHandle();
        handle.set("<hello>hello</hello>");
        XMLDocumentManager docMgr = client.newXMLDocumentManager();
        docMgr.write("/docs/doc.xml", handle);
    }

    @Test
    public void readOneDocumentTest() throws Exception {
        itemReader.open(new ExecutionContext());
        DocumentRecord record = itemReader.read();
        assertEquals(record.getUri(), "/docs/doc.xml");
        itemReader.close();
    }
}