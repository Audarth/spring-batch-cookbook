package com.marklogic.batch.delimited;

import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.test.AbstractJobRunnerTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

@ContextConfiguration(classes = {ImportDelimitedFileJobConfig.class})
public class ImportNycBabiesTest extends AbstractJobRunnerTest {

    private JobParametersBuilder jpb = new JobParametersBuilder();
    private JobExecution jobExecution;

    @Before
    public void givenDelimitedFileJob() {
        jpb.addString("input_file_path", "./src/test/resources/delimited/baby-names.csv");
        jpb.addString("delimited_root_name", "baby-name");
        jpb.addString("output_collections", "baby-name");

    }


    @Test
    public void ingestDelimitedBabyNamesWithOutputTransformTest() throws Exception {
        jpb.addString("uri_transform", "com.marklogic.batch.delimited.support.BabyNameUriGenerator");
        jpb.addString("output_transform", "com.marklogic.batch.delimited.support.BabyNameColumnMapSerializer");
        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
        getClientTestHelper().assertCollectionSize("Expecting 199 files in baby-name collection", "baby-name", 199);
        List<String> uris = getClientTestHelper().getUrisInCollection("baby-name", 199);
        Fragment f = getClientTestHelper().parseUri(uris.get(0));
        f.assertElementExists("Expecting birth year", "/baby-name/birthYear");
        f.assertElementExists("Expecting birth year", "/baby-name/create_date");
    }

    @Test
    public void ingestDelimitedBabyNamesDefaultThreadAndChunkSizeTest() throws Exception {
        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
        getClientTestHelper().assertCollectionSize("Expecting 199 files in baby-name collection", "baby-name", 199);
    }

    @Test
    public void ingestDelimitedBabyNamesWithUriTransformTest() throws Exception {
        jpb.addString("uri_transform", "com.marklogic.batch.delimited.support.BabyNameUriGenerator");
        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
        getClientTestHelper().assertCollectionSize("Expecting 199 files in baby-name collection", "baby-name", 199);
        List<String> uris = getClientTestHelper().getUrisInCollection("baby-name", 199);
        assertThat(uris.get(0), startsWith("/2011/"));
    }

    @Test
    public void ingestBabyNamesToJsonTest() throws Exception {
        jpb.addString("document_type", "json");
        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
        getClientTestHelper().assertCollectionSize("Expecting 199 files in baby-name collection", "baby-name", 199);

    }

    @Test
    public void ingestBabyNamesWithDefaultDelimitedRootNameTest() throws Exception {
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("input_file_path", "./src/test/resources/delimited/baby-names.csv");
        jpb.addString("output_collections", "baby-name");
        jpb.addString("uri_transform", "com.marklogic.batch.delimited.support.BabyNameUriGenerator");
        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
        getClientTestHelper().assertCollectionSize("Expecting 199 files in baby-name collection", "baby-name", 199);
        List<String> uris = getClientTestHelper().getUrisInCollection("baby-name", 199);
        Fragment f = getClientTestHelper().parseUri(uris.get(0), "baby-name");
        f.assertElementExists("Expecting default delimited root name", "/record");


    }

    @Test
    public void ingestBabyNamesWithUriIdTest() throws Exception {
        jpb.addString("uri_id", "NM");
        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
        getClientTestHelper().assertCollectionSize("Expecting 2811 files in baby-name collection", "baby-name", 199);
        getClientTestHelper().parseUri("HAZEL.xml", "baby-name");
    }

}


