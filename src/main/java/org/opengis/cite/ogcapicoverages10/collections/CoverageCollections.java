package org.opengis.cite.ogcapicoverages10.collections;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.Method.GET;
import static org.opengis.cite.ogcapicoverages10.EtsAssert.assertTrue;
import static org.opengis.cite.ogcapicoverages10.SuiteAttribute.IUT;
import static org.opengis.cite.ogcapicoverages10.openapi3.OpenApiUtils.retrieveTestPointsForCollectionsMetadata;
import static org.opengis.cite.ogcapicoverages10.util.JsonUtils.findLinkByRel;
import static org.opengis.cite.ogcapicoverages10.util.JsonUtils.findLinksWithSupportedMediaTypeByRel;
import static org.opengis.cite.ogcapicoverages10.util.JsonUtils.findLinksWithoutRelOrType;
import static org.opengis.cite.ogcapicoverages10.util.JsonUtils.findUnsupportedTypes;
import static org.opengis.cite.ogcapicoverages10.util.JsonUtils.linkIncludesRelAndType;
import static org.opengis.cite.ogcapicoverages10.util.JsonUtils.parseAsListOfMaps;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.opengis.cite.ogcapicoverages10.CommonDataFixture;
import org.opengis.cite.ogcapicoverages10.SuiteAttribute;
import org.opengis.cite.ogcapicoverages10.openapi3.TestPoint;
import org.opengis.cite.ogcapicoverages10.openapi3.UriBuilder;
import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/**
 * A.2.5. Coverage Collections {root}/collections
 *
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz </a>
 */
public class CoverageCollections extends CommonDataFixture {

	private final Map<TestPoint, Response> testPointAndResponses = new HashMap<>();

	private final Map<TestPoint, List<Map<String, Object>>> testPointAndCollections = new HashMap<>();

	private Object[][] testPointsData;

	@DataProvider(name = "collectionsUris")
	public Object[][] collectionsUris(ITestContext testContext) {
		if (this.testPointsData == null) {
			URI iut = (URI) testContext.getSuite().getAttribute(IUT.getName());
			List<TestPoint> testPoints = retrieveTestPointsForCollectionsMetadata(getApiModel(), iut);
			this.testPointsData = new Object[testPoints.size()][];
			int i = 0;
			for (TestPoint testPoint : testPoints) {
				this.testPointsData[i++] = new Object[] { testPoint };
			}
		}
		return testPointsData;
	}

	@AfterClass
	public void storeCollectionsInTestContext(ITestContext testContext) {
		List<Map<String, Object>> collections = new ArrayList<>();
		for (List<Map<String, Object>> testPointAndCollection : testPointAndCollections.values()) {
			collections.addAll(testPointAndCollection);
		}
		testContext.getSuite().setAttribute(SuiteAttribute.COLLECTIONS.getName(), collections);
	}

	/**
	 * Test for Requirement 3 (/req/geodata-coverage/coverage-op) which is specified
	 * in Section 7.5.1.1.
	 *
	 * <pre>
	 * The API SHALL support the HTTP GET operation at the path /collections/{coverageid}/coverage.
	 *
	 * </pre>
	 *
	 * @param testPoint the test point to test, never <code>null</code>
	 */
	@Test(description = "Section 7.5.1.1. Implements Test for Requirement 3(/req/geodata-coverage/coverage-op)", groups = "collections", dataProvider = "collectionsUris", alwaysRun = true)
	public void validateCoverageCollectionsMetadataOperationResponse_Links(TestPoint testPoint) {

		JsonPath response;
		Response request = init().baseUri(rootUri.toString()).accept(JSON).when().request(GET, "/collections");

		request.then().statusCode(200);
		response = request.jsonPath();
		List<Object> collections = response.getList("collections");
		
		boolean apiHasAtLeastOneCoverageCollection = false;

		Set<String> coverageCollectionInstances = new HashSet<>();
		
		for (Object collection : collections) {
			Map<String, Object> collectionMap = (Map<String, Object>) collection;
			Object collectionInstance = collectionMap.get("id");
		
			
			boolean coverageCollectionMetadataIsValid = false;
			boolean isCoverageCollection = false;
			boolean hasDomainSetLink = false;
			boolean hasRangeTypeLink = false;

			Object links = collectionMap.get("links");

			List<Map<String, Object>> collectionLinks = (List<Map<String, Object>>) links;
		
			for (Map<String, Object> link : collectionLinks) {
				Object rel = link.get("rel");
				if (rel.equals("http://www.opengis.net/def/rel/ogc/1.0/coverage")) {
					apiHasAtLeastOneCoverageCollection = true;
					isCoverageCollection = true;
				}
				if (rel.equals("http://www.opengis.net/def/rel/ogc/1.0/coverage-domainset")) {
					hasDomainSetLink = true;
				}		
				if (rel.equals("http://www.opengis.net/def/rel/ogc/1.0/coverage-rangetype")) {
					hasRangeTypeLink = true;
				}				
			}
			
			if(isCoverageCollection) {
				
				coverageCollectionInstances.add((String) collectionInstance);
		        assertTrue(hasDomainSetLink && hasRangeTypeLink,
		               "The Coverage Collection "+collectionMap.get("id")+" is missing either a domainset or rangetype link");

			}
			
		}
		
		//validate first collection in coverageCollectionInstances
		
		
		Response coverageRequest = init().baseUri(rootUri.toString()).accept(JSON).when().request(GET, "/collections/"+coverageCollectionInstances.iterator().next());
		coverageRequest.then().statusCode(200);
		JsonPath coverageResponse = coverageRequest.jsonPath();
		System.out.println("GEH ID "+coverageResponse.getString("id"));
		
		//----end validation of collection
	

        assertTrue(apiHasAtLeastOneCoverageCollection,
                "Must have at least one coverage collection. None of the collection have relation http://www.opengis.net/def/rel/ogc/1.0/coverage and a media type");

	}


	
	
}